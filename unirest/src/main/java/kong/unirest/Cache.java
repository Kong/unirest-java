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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Cache interface for response caching
 */
public interface Cache {

    /**
     * Returns the cached HttpResponse for a key or uses the Supplier to fetch the response
     * @param key the cache key
     * @param fetcher a function to execute the request and return the response. This response should be
     *                cached by the implementation
     * @param <T> the type of response
     * @return the Http Response
     */
    <T> HttpResponse get(Key key, Supplier<HttpResponse<T>> fetcher);

    /**
     * Returns the cached HttpResponse for a key or uses the Supplier to fetch the response
     * @param key the cache key
     * @param fetcher a function to execute the request and return the response. This response should be
     *                cached by the implementation
     * @param <T> the type of response
     * @return the CompletableFuture for the response
     */
    <T> CompletableFuture getAsync(Key key, Supplier<CompletableFuture<HttpResponse<T>>> fetcher);

    /**
     * a builder for cache options
     * @return a new Builder.
     */
    static Builder builder(){
        return new Builder();
    }
    class Builder {
        private int depth = 100;
        private long ttl = 0;
        private Cache backing;

        CacheManager build() {
            if(backing != null){
                return new CacheManager(backing);
            }
            return new CacheManager(depth, ttl);
        }

        /**
         * defines the max depth of the cache in number of values.
         * defaults to 100.
         * Elements exceeding the depth are purged on read.
         * Custom Cache implementations may not honor this setting
         * @param value the max depth
         * @return the current builder.
         */
        public Builder depth(int value) {
            this.depth = value;
            return this;
        }

        /**
         * Sets a Time-To-Live for response objects.
         * There is no TTL by default and objects will be kept indefinitely
         * Elements exceeding the TTL are purged on read.
         * Custom Cache implementations may not honor this setting
         * @param number a number
         * @param units the TimeUnits of the number
         * @return this builder.
         */
        public Builder maxAge(long number, TimeUnit units) {
            this.ttl = units.toMillis(number);
            return this;
        }

        /**
         * Sets a custom backing cache. This cache must implement it's own purging rules
         * There is no TTL by default and objects will be kept indefinitely
         * @param cache the backing cache implementation
         * @return this builder.
         */
        public Builder backingCache(Cache cache) {
            this.backing = cache;
            return this;
        }
    }

    
    class Key {
        private final int hash;
        private final Instant time;

        Key(HttpRequest request, Boolean isAsync, Class<?> responseType) {
            this(Objects.hash(request.hashCode(), isAsync, responseType),
                 request.getCreationTime());
        }

        public Key(int hash, Instant time) {
            this.hash = hash;
            this.time = time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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
