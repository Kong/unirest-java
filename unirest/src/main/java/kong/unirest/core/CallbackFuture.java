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

package kong.unirest.core;

import java.util.concurrent.CompletableFuture;

class CallbackFuture  {

    static <T> CompletableFuture<HttpResponse<T>> wrap(Callback<T> source){
        return new CompletableFuture<HttpResponse<T>>(){
            @Override
            public boolean complete(HttpResponse<T> value) {
                source.completed(value);
                return super.complete(value);
            }
            @Override
            public boolean completeExceptionally(Throwable ex) {
                source.failed(new UnirestException(ex));
                return super.completeExceptionally(ex);
            }
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                source.cancelled();
                return super.cancel(mayInterruptIfRunning);
            }
        };
    }
}