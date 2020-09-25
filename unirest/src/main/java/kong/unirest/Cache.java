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
        private KeyGenerator keyGen;

        CacheManager build() {
            if(backing != null){
                return new CacheManager(backing, keyGen);
            }
            return new CacheManager(depth, ttl, keyGen);
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

        /**
         * Provide a custom key generator.
         * The default key is a hash of the request, the request execution type and the response type.
         * @param keyGenerator a custom cache key generator
         * @return this builder
         */
        public Builder withKeyGen(KeyGenerator keyGenerator) {
            this.keyGen = keyGenerator;
            return this;
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
}
