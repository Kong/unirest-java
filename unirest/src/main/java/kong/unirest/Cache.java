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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;



public class Cache {

    private final CacheWrapper wrapper = new CacheWrapper();
    private final AsyncWrapper asyncWrapper = new AsyncWrapper();
    private final CacheMap map;

    private Client originalClient;
    private AsyncClient originalAsync;

    public Cache() {
        this(100, 0);
    }

    public Cache(int depth, long ttl) {
        map = new CacheMap(depth, ttl);
    }

    Client wrap(Client client) {
        this.originalClient = client;
        return wrapper;
    }

    AsyncClient wrapAsync(AsyncClient client) {
        this.originalAsync = client;
        return asyncWrapper;
    }

    private <T> Key  getHash(HttpRequest request, Boolean isAsync, Class<?> responseType) {
        return new Key(request, isAsync, responseType);
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

    private class CacheMap extends LinkedHashMap<Key, Object> {
        private final int maxSize;
        private long ttl;

        CacheMap(int maxSize, long ttl) {
            this.maxSize = maxSize;
            this.ttl = ttl;
        }

        @Override
        public Object computeIfAbsent(Key key, Function<? super Key, ?> mappingFunction) {
            if(ttl > 0) {
                Instant now = Util.now();
                keySet().removeIf(k -> ChronoUnit.MILLIS.between(k.getTime(), now) > ttl);
            }
            return super.computeIfAbsent(key, mappingFunction);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Key, Object> eldest) {
            return size() > maxSize;
        }
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private int depth = 100;
        private long ttl = 0;

        public Cache build() {
            return new Cache(depth, ttl);
        }

        public Builder depth(int value) {
            this.depth = value;
            return this;
        }

        public Builder maxAge(long number, TimeUnit units) {
            this.ttl = units.toMillis(number);
            return this;
        }
    }

    public static class Key {
        private final int hash;
        private final Instant time;

        public Key(HttpRequest request, Boolean isAsync, Class<?> responseType) {
            hash = Objects.hash(request.hashCode(), isAsync, responseType);
            time = request.getCreationTime();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return hash == key.hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public Instant getTime() {
            return time;
        }
    }
}
