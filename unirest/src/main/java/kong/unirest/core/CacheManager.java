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

import kong.unirest.core.java.Event;

import java.net.http.WebSocket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CacheManager {

    private final CacheWrapper wrapper = new CacheWrapper();
    private final CacheWrapper asyncWrapper = new CacheWrapper();
    private final Cache backingCache;

    private Client originalClient;

    public CacheManager() {
        this(100, 0);
    }

    public CacheManager(int depth, long ttl) {
        this(new CacheMap(depth, ttl));
    }

    public CacheManager(Cache backing) {
        backingCache = backing;
    }

    Client wrap(Client client) {
        this.originalClient = client;
        return wrapper;
    }

    Client wrapAsync(Client client) {
        this.originalClient = client;
        return asyncWrapper;
    }

    private class CacheWrapper implements Client {

        @Override
        public Object getClient() {
            return originalClient.getClient();
        }

        @Override
        public <T> HttpResponse<T> request(HttpRequest request,
                                           Function<RawResponse, HttpResponse<T>> transformer,
                                           Class<?> responseType) {

            return backingCache.get(request, responseType,
                    () -> originalClient.request(request, transformer, responseType));
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request,
                                                              Function<RawResponse, HttpResponse<T>> transformer,
                                                              CompletableFuture<HttpResponse<T>> callback,
                                                              Class<?> responseType) {
            return backingCache.getAsync(request, responseType,
                    () -> originalClient.request(request, transformer, callback, responseType));
        }

        @Override //
        public WebSocketResponse websocket(WebSocketRequest request, WebSocket.Listener listener) {
            return originalClient.websocket(request, listener);
        }

        @Override
        public CompletableFuture<Void> sse(SseRequest request, SseHandler handler) {
            return originalClient.sse(request, handler);
        }

        @Override
        public Stream<Event> sse(SseRequest request) {
            return Stream.empty();
        }
    }

    /**
     * A functional interface to generate a cache key
     */
    @FunctionalInterface
    interface KeyGenerator {
        /**
         * A function to generate a cache key
         * @param request the current http request
         * @param isAsync indicates if this request is being executed async
         * @param responseType the response type (String, JsonNode, etc)
         * @return a key which can be used as a hash for the cache
         */
        Key apply(HttpRequest request, Boolean isAsync, Class<?> responseType);
    }

    /**
     * Interface for the cache key which can be implemented by consumers
     * The key should implement equals and hashCode
     * It must must return the time the key was created.
     */
    interface Key {
        /**
         * @param   obj   the reference object with which to compare.
         * @return  {@code true} if this object is the same as the obj
         *          argument; {@code false} otherwise.
         * @see     #hashCode()
         * @see     java.util.HashMap
         */
        @Override
        boolean equals(Object obj);

        /**
         * As much as is reasonably practical, the hashCode method defined
         * by class {@code Object} does return distinct integers for
         * distinct objects. (The hashCode may or may not be implemented
         * as some function of an object's memory address at some point
         * in time.)
         *
         * @return  a hash code value for this object.
         * @see     java.lang.Object#equals(java.lang.Object)
         * @see     java.lang.System#identityHashCode
         */
        @Override
        int hashCode();

        /**
         * The time the key was created to be used by purging functions
         * @return the time as an instant
         */
        Instant getTime();
    }

    private static class CacheMap extends LinkedHashMap<Key, Object> implements Cache {
        private final int maxSize;
        private long ttl;
        private KeyGenerator keyGen = HashKey::new;

        CacheMap(int maxSize, long ttl) {
            this.maxSize = maxSize;
            this.ttl = ttl;
        }

        @Override
        public <T> HttpResponse<T> get(HttpRequest request, Class<?> responseType, Supplier<HttpResponse<T>> fetcher) {
            clearOld();
            var key = keyGen.apply(request, false, responseType);
            return (HttpResponse<T>)super.computeIfAbsent(key, (k) -> fetcher.get());
        }

        @Override
        public <T> CompletableFuture getAsync(HttpRequest request, Class<?> responseType, Supplier<CompletableFuture<HttpResponse<T>>> fetcher) {
            clearOld();
            var key = keyGen.apply(request, true, responseType);
            return (CompletableFuture)super.computeIfAbsent(key, (k) -> fetcher.get());
        }

        private void clearOld() {
            if (ttl > 0) {
                Instant now = Util.now();
                keySet().removeIf(k -> ChronoUnit.MILLIS.between(k.getTime(), now) > ttl);
            }
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Key, Object> eldest) {
            return size() > maxSize;
        }

        private static class HashKey implements Key  {
            private final HttpMethod method;
            private final String url;
            private final List<String> headers;
            private final Boolean isAsync;
            private final Class<?> responseType;
            private final int hash;
            private final Instant time;

            HashKey(HttpRequest request, Boolean isAsync, Class<?> responseType) {
                this.method = request.getHttpMethod();
                this.url = request.getUrl();
                this.headers = request.getHeaders().all().stream()
                        .map(h -> h.getName() + ": " + h.getValue())
                        .collect(Collectors.toList());
                this.isAsync = isAsync;
                this.responseType = responseType;
                this.hash = Objects.hash(method, url, headers, isAsync, responseType);
                this.time = request.getCreationTime();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                HashKey key = (HashKey) o;
                return hash == key.hash &&
                        Objects.equals(method, key.method) &&
                        Objects.equals(url, key.url) &&
                        Objects.equals(headers, key.headers) &&
                        Objects.equals(isAsync, key.isAsync) &&
                        Objects.equals(responseType, key.responseType);
            }

            @Override
            public int hashCode() {
                return hash;
            }

            @Override
            public Instant getTime() {
                return time;
            }
        }
    }
}
