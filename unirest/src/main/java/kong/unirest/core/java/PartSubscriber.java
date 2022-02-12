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

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Flow;

import static java.util.Objects.requireNonNull;

class PartSubscriber implements Flow.Subscriber<ByteBuffer> {

    static final ByteBuffer END_OF_PART = ByteBuffer.allocate(0);

    private final MultipartSubscription downstream; // for signalling
    private final Part part;
    private final ProgressMonitor monitor;
    private final ConcurrentLinkedQueue<ByteBuffer> buffers;
    private final Upstream upstream;
    private final Prefetcher prefetcher;
    private long total;

    PartSubscriber(MultipartSubscription downstream, Part part, ProgressMonitor monitor) {
        this.downstream = downstream;
        this.part = part;
        this.monitor = monitor;
        buffers = new ConcurrentLinkedQueue<>();
        upstream = new Upstream();
        prefetcher = new Prefetcher();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        requireNonNull(subscription);
        if (upstream.setOrCancel(subscription)) {
            // The only possible concurrent access to prefetcher applies here.
            // But the operation need not be atomic as other reads/writes
            // are done serially when ByteBuffers are polled, which is only
            // possible after this volatile write.
            prefetcher.initialize(upstream);
        }
    }

    @Override
    public void onNext(ByteBuffer item) {
        requireNonNull(item);
        buffers.offer(item);
        if(monitor != null) {
            this.total = total + item.remaining();
            monitor.accept(part.getFieldName(), part.getFilename(), Long.valueOf(item.remaining()), total);
        }
        downstream.signal(false);
    }

    @Override
    public void onError(Throwable throwable) {
        requireNonNull(throwable);
        abortUpstream(false);
        downstream.signalError(throwable);
    }

    @Override
    public void onComplete() {
        abortUpstream(false);
        buffers.offer(END_OF_PART);
        downstream.signal(true); // force completion signal
    }

    void abortUpstream(boolean cancel) {
        if (cancel) {
            upstream.cancel();
        } else {
            upstream.clear();
        }
    }


    ByteBuffer pollNext() {
        ByteBuffer next = buffers.peek();
        if (next != null && next != END_OF_PART) {
            buffers.poll(); // remove
            prefetcher.update(upstream);
        }
        return next;
    }

    public Part getPart() {
        return part;
    }
}
