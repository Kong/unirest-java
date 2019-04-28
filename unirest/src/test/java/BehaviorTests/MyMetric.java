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

package BehaviorTests;

import com.google.common.collect.ArrayListMultimap;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponseSummary;
import kong.unirest.MetricContext;
import kong.unirest.UniMetric;

import java.util.function.Function;

public class MyMetric implements UniMetric {
    private final Function<HttpRequestSummary, String> keyFunction;
    public ArrayListMultimap<String, Execution> routes = ArrayListMultimap.create();

    public MyMetric(Function<HttpRequestSummary, String> keyFunction){
        this.keyFunction = keyFunction;
    }

    @Override
    public MetricContext begin(HttpRequestSummary request) {
        long startNanos = System.nanoTime();
        return (r, e) -> {
            routes.put(keyFunction.apply(request), new Execution(System.nanoTime() - startNanos, statusOr(r), e));
        };
    }

    private int statusOr(HttpResponseSummary r) {
        if(r == null){
            return -1;
        }
        return r.getStatus();
    }

    public long countResponses(int i) {
        return routes.entries()
                .stream()
                .filter(e -> e.getValue().status == i)
                .count();
    }

    public static class Execution {
        public final long time;
        public final int status;
        public final Exception e;

        public Execution(long time, int status, Exception e){
            this.time = time;
            this.status = status;
            this.e = e;
        }
    }
}
