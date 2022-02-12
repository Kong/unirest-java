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


/** Simple class for encapsulating prefetch logic used across subscribers. */
class Prefetcher {

    public static final int PREFETCH = 16;
    public static final int PREFETCH_THRESHOLD = (int) (PREFETCH * (50 / 100f));
    private final int prefetch;
    private final int prefetchThreshold;
    private volatile int upstreamWindow;

    public Prefetcher() {
        prefetch = PREFETCH;
        prefetchThreshold = PREFETCH_THRESHOLD;
    }

    public void initialize(Upstream upstream) {
        upstreamWindow = prefetch;
        upstream.request(prefetch);
    }

    public void update(Upstream upstream) {
        // Decrement current window and bring it back to
        // prefetch if became <= prefetchThreshold
        int update = upstreamWindow - 1;
        if (update <= prefetchThreshold) {
            upstreamWindow = prefetch;
            upstream.request(prefetch - update);
        } else {
            upstreamWindow = update;
        }
    }

    // for testing
    int currentWindow() {
        return upstreamWindow;
    }
}
