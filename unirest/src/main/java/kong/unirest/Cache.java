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

package kong.unirest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;



public class Cache {
    private CacheWrapper wrapper = new CacheWrapper();
    private AsyncWrapper asyncWrapper = new AsyncWrapper();
    private CacheMap map = new CacheMap(100);

    private Client originalClient;
    private AsyncClient originalAsync;

    Client wrap(Client client) {
        this.originalClient = client;
        return wrapper;
    }

    AsyncClient wrapAsync(AsyncClient client) {
        this.originalAsync = client;
        return asyncWrapper;
    }

    private <T> int  getHash(HttpRequest request, Boolean isAsync, Class<?> responseType) {
        return Objects.hash(request.hashCode(), isAsync, responseType);
    }

    class CacheWrapper implements Client {

        @Override
        public Object getClient() {
            return originalClient.getClient();
        }

        @Override
        public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer) {
            return request(request, transformer, Object.class);
        }

        @Override
        public <T> HttpResponse<T> request(HttpRequest request,
                                           Function<RawResponse, HttpResponse<T>> transformer,
                                           Class<?> responseType) {
            return (HttpResponse<T>) map.computeIfAbsent(getHash(request, false, responseType),
                    r -> originalClient.request(request, transformer, responseType));
        }

        @Override
        public Stream<Exception> close() {
            return originalClient.close();
        }
        @Override
        public void registerShutdownHook() {
            originalClient.registerShutdownHook();
        }
    }

    private class AsyncWrapper implements AsyncClient {
        @Override
        public <T> T getClient() {
            return originalAsync.getClient();
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request,
                                                              Function<RawResponse, HttpResponse<T>> transformer,
                                                              CompletableFuture<HttpResponse<T>> callback) {
            return request(request, transformer, callback, Object.class);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request,
                                                              Function<RawResponse, HttpResponse<T>> transformer,
                                                              CompletableFuture<HttpResponse<T>> callback,
                                                              Class<?> responseType) {
            return (CompletableFuture<HttpResponse<T>>)map.computeIfAbsent(getHash(request, true, responseType),
                    k -> originalAsync.request(request, transformer, callback, responseType));
        }

        @Override
        public void registerShutdownHook() {
            originalAsync.registerShutdownHook();
        }

        @Override
        public Stream<Exception> close() {
            return originalAsync.close();
        }

        @Override
        public boolean isRunning() {
            return originalAsync.isRunning();
        }
    }


    private class CacheMap<R extends HttpRequest, T> extends LinkedHashMap<Integer, Object> {
        private final int maxSize;

        public CacheMap(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Object> eldest) {
            return size() > maxSize;
        }
    }
}
