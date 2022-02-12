/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kong.unirest.core.java;

import kong.unirest.core.ProgressMonitor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;

import static java.nio.charset.StandardCharsets.UTF_8;

class MultipartSubscription implements Flow.Subscription {

    // An executor that executes the runnable in the calling thread.
    static final Executor SYNC_EXECUTOR = Runnable::run;
    private static final int RUN = 0x1; // run signaller task
    private static final int KEEP_ALIVE = 0x2; // keep running signaller task
    private static final int CANCELLED = 0x4; // subscription is cancelled
    private static final int SUBSCRIBED = 0x8; // onSubscribe called

    /*
     * Implementation is loosely modeled after SubmissionPublisher$BufferedSubscription, mainly
     * regarding how execution is controlled by CASes on a state field that manipulate bits
     * representing execution states.
     */
    private static final VarHandle STATE;
    private static final VarHandle PENDING_ERROR;
    private static final VarHandle DEMAND;
    private static final VarHandle PART_SUBSCRIBER;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            PART_SUBSCRIBER = lookup.findVarHandle(MultipartSubscription.class, "partSubscriber", Flow.Subscriber.class);
            STATE = lookup.findVarHandle(MultipartSubscription.class, "state", int.class);
            DEMAND = lookup.findVarHandle(MultipartSubscription.class, "demand", long.class);
            PENDING_ERROR = lookup.findVarHandle(MultipartSubscription.class, "pendingError", Throwable.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // A tombstone to protect against race conditions that would otherwise occur if a
    // thread tries to abort() while another tries to nextPartHeaders(), which might lead
    // to a newly subscribed part being missed by abort().
    private static final Flow.Subscriber<ByteBuffer> CANCELLED_SUBSCRIBER =
            new Flow.Subscriber<>() {
                @Override public void onSubscribe(Flow.Subscription subscription) {}
                @Override public void onNext(ByteBuffer item) {}
                @Override public void onError(Throwable throwable) {}
                @Override public void onComplete() {}
            };

    private final String boundary;
    private final List<Part> parts;
    private int partIndex;
    private boolean complete;
    private final ProgressMonitor monitor;
    private final Flow.Subscriber<? super ByteBuffer> downstream;
    private final Executor executor;


    private volatile Flow.Subscriber<ByteBuffer> partSubscriber;
    private volatile int state;
    private volatile long demand;
    private volatile Throwable pendingError;

    MultipartSubscription(String boundary, List<Part> parts, ProgressMonitor monitor, Flow.Subscriber<? super ByteBuffer> downstream) {
        this.monitor = monitor;
        this.downstream = downstream;
        this.executor = SYNC_EXECUTOR;
        this.boundary = boundary;
        this.parts = parts;
    }

    @Override
    public final void request(long n) {
        if (n > 0 && getAndAddDemand(this, DEMAND, n) == 0) {
            signal();
        } else if (n <= 0) {
            signalError(new IllegalArgumentException("non-positive subscription request"));
        }
    }

    @Override
    public final void cancel() {
        if ((getAndBitwiseOrState(CANCELLED) & CANCELLED) == 0) {
            abort(true);
        }
    }

    /** Adds given count to demand not exceeding {@code Long.MAX_VALUE}. */
    private long getAndAddDemand(Object owner, VarHandle demand, long n) {
        while (true) {
            long currentDemand = (long) demand.getVolatile(owner);
            long addedDemand = currentDemand + n;
            if (addedDemand < 0) { // overflow
                addedDemand = Long.MAX_VALUE;
            }
            if (demand.compareAndSet(owner, currentDemand, addedDemand)) {
                return currentDemand;
            }
        }
    }

    /** Subtracts given count from demand. Caller ensures result won't be negative. */
    private long subtractAndGetDemand(Object owner, VarHandle demand, long n) {
        return (long) demand.getAndAdd(owner, -n) - n;
    }



    /** Schedules a signaller task. {@code force} tells whether to schedule in case of no demand */
    public final void signal(boolean force) {
        if (force || demand > 0) {
            signal();
        }
    }

    public final void signalError(Throwable error) {
        propagateError(error);
        signal();
    }
    
    /** Returns {@code true} if cancelled. {@code false} result is immediately outdated. */
    private final boolean isCancelled() {
        return (state & CANCELLED) != 0;
    }

    /**
     * Returns {@code true} if the subscriber is to be completed exceptionally. {@code false} result
     * is immediately outdated. Can be used by implementation to halt producing items in case the
     * subscription was asynchronously signalled with an error.
     */
    private final boolean hasPendingErrors() {
        return pendingError != null;
    }


    /**
     * Calls downstream's {@code onError} after cancelling this subscription. {@code flowInterrupted}
     * tells whether the error interrupted the normal flow of signals.
     */
    private final void cancelOnError(Flow.Subscriber<? super ByteBuffer> downstream, Throwable error, boolean flowInterrupted) {
        if ((getAndBitwiseOrState(CANCELLED) & CANCELLED) == 0) {
            try {
                downstream.onError(error);
            } finally {
                abort(flowInterrupted);
            }
        }
    }

    /** Calls downstream's {@code onComplete} after cancelling this subscription. */
    private final void cancelOnComplete(Flow.Subscriber<? super ByteBuffer> downstream) {
        if ((getAndBitwiseOrState(CANCELLED) & CANCELLED) == 0) {
            try {
                downstream.onComplete();
            } finally {
                abort(false);
            }
        }
    }

    /** Submits given item to the downstream, returning {@code false} and cancelling on failure. */
    private final boolean submitOnNext(Flow.Subscriber<? super ByteBuffer> downstream, ByteBuffer item) {
        if (!(isCancelled() || hasPendingErrors())) {
            try {
                downstream.onNext(item);
                return true;
            } catch (Throwable t) {
                Throwable error = propagateError(t);
                pendingError = null;
                cancelOnError(downstream, error, true);
            }
        }
        return false;
    }

    private void signal() {
        boolean casSucceeded = false;
        for (int s; !casSucceeded && ((s = state) & CANCELLED) == 0; ) {
            int setBit = (s & RUN) != 0 ? KEEP_ALIVE : RUN; // try to keep alive or run & execute
            casSucceeded = STATE.compareAndSet(this, s, s | setBit);
            if (casSucceeded && setBit == RUN) {
                try {
                    executor.execute(this::run);
                } catch (RuntimeException | Error e) {
                    // this is a problem because we cannot call any of onXXXX methods here
                    // as that would ruin the execution context guarantee. SubmissionPublisher's
                    // behaviour here is followed (cancel & rethrow).
                    cancel();
                    throw e;
                }
            }
        }
    }

    private void run() {
        int s;
        Flow.Subscriber<? super ByteBuffer> d = downstream;
        subscribeOnDrain(d);
        for (long x = 0L, r = demand; ((s = state) & CANCELLED) == 0; ) {
            long emitted;
            Throwable error = pendingError;
            if (error != null) {
                pendingError = null;
                cancelOnError(d, error, false);
            } else if ((emitted = emit(d, r - x)) > 0L) {
                x += emitted;
                r = demand; // get fresh demand
                if (x == r) { // 'x' needs to be flushed
                    r = subtractAndGetDemand(this, DEMAND, x);
                    x = 0L;
                }
            } else if (r == (r = demand)) { // check that emit() actually failed on a fresh demand
                // un keep-alive or kill task if a dead-end is reached, which is possible if:
                // - there is no active emission (x <= 0)
                // - there is no active demand (r <= 0 after flushing x)
                // - cancelled (checked by the loop condition)
                boolean exhausted = x <= 0L;
                if (!exhausted) {
                    r = subtractAndGetDemand(this, DEMAND, x);
                    x = 0L;
                    exhausted = r <= 0L;
                }
                int unsetBit = (s & KEEP_ALIVE) != 0 ? KEEP_ALIVE : RUN;
                if (exhausted && STATE.compareAndSet(this, s, s & ~unsetBit) && unsetBit == RUN) {
                    break;
                }
            }
        }
    }

    private void subscribeOnDrain(Flow.Subscriber<? super ByteBuffer> downstream) {
        if ((state & (SUBSCRIBED | CANCELLED)) == 0
                && (getAndBitwiseOrState(SUBSCRIBED) & (SUBSCRIBED | CANCELLED)) == 0) {
            try {
                downstream.onSubscribe(this);
            } catch (Throwable t) {
                Throwable e = propagateError(t);
                pendingError = null;
                cancelOnError(downstream, e, true);
            }
        }
    }

    /** Sets pending error or adds new one as suppressed in case of multiple error sources. */
    private Throwable propagateError(Throwable error) {
        while(true) {
            Throwable currentError = pendingError;
            if (currentError != null) {
                currentError.addSuppressed(error); // addSuppressed is thread-safe
                return currentError;
            }
            if (PENDING_ERROR.compareAndSet(this, null, error)) {
                return error;
            }
        }
    }

    private int getAndBitwiseOrState(int bits) {
        return (int) STATE.getAndBitwiseOr(this, bits);
    }

    /**
     * Main method for item emission. At most {@code e} items are emitted to the downstream using
     * {submitOnNext(Flow.Subscriber, Object)} as long as it returns {@code true}. The actual number
     * of emitted items is returned, may be {@code 0} in case of cancellation. If the underlying
     * source is finished, the subscriber is completed with {@link #cancelOnComplete(Flow.Subscriber)}.
     */
    private long emit(Flow.Subscriber<? super ByteBuffer> downstream, long emit) {
        long submitted = 0L;
        while (true) {
            ByteBuffer batch;
            if (complete) {
                cancelOnComplete(downstream);
                return 0;
            } else if (submitted >= emit || (batch = pollNext()) == null) { // exhausted demand or batches
                return submitted;
            } else if (submitOnNext(downstream, batch)) {
                submitted++;
            } else {
                return 0;
            }
        }
    }

    /**
     * Called when the subscription is cancelled. {@code flowInterrupted} specifies whether
     * cancellation was due to ending the normal flow of signals (signal|signalError) or due to flow
     * interruption by downstream (e.g. calling {@code cancel()} or throwing from {@code onNext}).
     */
    private void abort(boolean flowInterrupted) {
        Flow.Subscriber<ByteBuffer> previous =
                (Flow.Subscriber<ByteBuffer>) PART_SUBSCRIBER.getAndSet(this, CANCELLED_SUBSCRIBER);
        if (previous instanceof PartSubscriber) {
            ((PartSubscriber) previous).abortUpstream(flowInterrupted);
        }
    }

    private ByteBuffer pollNext() {

        Flow.Subscriber<ByteBuffer> subscriber = partSubscriber;
        if (subscriber instanceof PartSubscriber) { // not cancelled & not null
            ByteBuffer next = ((PartSubscriber) subscriber).pollNext();
            if (next != PartSubscriber.END_OF_PART) {
                return next;
            }
        }
        return subscriber != CANCELLED_SUBSCRIBER ? nextPartHeaders() : null;
    }

    private ByteBuffer nextPartHeaders() {
        StringBuilder heading = new StringBuilder();
        BoundaryAppender.get(partIndex, parts.size()).append(heading, boundary);
        if (partIndex < parts.size()) {
            Part part = parts.get(partIndex++);
            if (!subscribeToPart(part)) {
                return null;
            }
            MultipartBodyPublisher.appendPartHeaders(heading, part);
            heading.append("\r\n");
        } else {
            partSubscriber = CANCELLED_SUBSCRIBER; // race against abort() here is OK
            complete = true;
        }
        return UTF_8.encode(CharBuffer.wrap(heading));
    }

    private boolean subscribeToPart(Part part) {
        PartSubscriber subscriber = new PartSubscriber(this, part, monitor);
        Flow.Subscriber<ByteBuffer> current = partSubscriber;
        if (current != CANCELLED_SUBSCRIBER && PART_SUBSCRIBER.compareAndSet(this, current, subscriber)) {
            part.bodyPublisher().subscribe(subscriber);
            return true;
        }
        return false;
    }
}
