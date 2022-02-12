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


import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.Flow.Subscription;

/** A one-use atomic reference to an upstream subscription. */
class Upstream {

    // A subscription that does nothing
    public static final Subscription NOOP_SUBSCRIPTION =
            new Subscription() {
                @Override
                public void request(long n) {}

                @Override
                public void cancel() {}
            };
    private static final VarHandle SUBSCRIPTION;

    static {
        try {
            SUBSCRIPTION =
                    MethodHandles.lookup().findVarHandle(Upstream.class, "subscription", Subscription.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private volatile Subscription subscription;

    public Upstream() {}

    /** Sets incoming subscription, cancels it if already set. */
    public boolean setOrCancel(Subscription incoming) {
        if (!SUBSCRIPTION.compareAndSet(this, null, incoming)) {
            incoming.cancel();
            return false;
        }
        return true;
    }

    /** Requests {@code n} items from upstream if set. */
    public void request(long n) {
        Subscription currentSubscription = subscription;
        if (currentSubscription != null) {
            currentSubscription.request(n);
        }
    }

    /** Cancels the upstream if set. */
    public void cancel() {
        Subscription currentSubscription =
                (Subscription) SUBSCRIPTION.getAndSet(this, NOOP_SUBSCRIPTION);
        if (currentSubscription != null) {
            currentSubscription.cancel();
        }
    }

    /** Just loses the reference to upstream if cancellation it is not required. */
    public void clear() {
        subscription = NOOP_SUBSCRIPTION;
    }
}
