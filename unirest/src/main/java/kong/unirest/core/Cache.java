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

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Cache interface for HTTP response caching.
 * <p>
 * Implement this interface to provide custom caching strategies for HTTP responses.
 * The cache stores responses keyed by request characteristics and can significantly
 * improve performance for repeated requests to the same endpoints.
 * </p>
 * <p>
 * A default in-memory cache implementation is available via {@link #builder()}.
 * Custom implementations can integrate with external caching systems such as
 * Redis, Memcached, or other distributed caches.
 * </p>
 *
 * @see Config#cacheResponses(Cache.Builder)
 * @see Builder
 */
public interface Cache {

    /**
     * Returns the cached HTTP response for a key, or uses the supplier to fetch and cache the response.
     * <p>
     * If a cached response exists for the given key, it is returned immediately.
     * Otherwise, the fetcher is invoked to execute the request, and the resulting
     * response should be stored in the cache before being returned.
     *
     * @param <T> the type of the response body
     * @param key the cache key identifying the request
     * @param fetcher a supplier that executes the request and returns the response;
     *                the implementation should cache this response
     * @return the cached or newly fetched {@link HttpResponse}
     */
    <T> HttpResponse get(Key key, Supplier<HttpResponse<T>> fetcher);

    /**
     * Returns the cached HTTP response for a key asynchronously, or uses the supplier to fetch and cache the response.
     * <p>
     * If a cached response exists for the given key, it is returned immediately via the future.
     * Otherwise, the fetcher is invoked to execute the request asynchronously, and the resulting
     * response should be stored in the cache before completing the future.
     *
     * @param <T> the type of the response body
     * @param key the cache key identifying the request
     * @param fetcher a supplier that executes the request asynchronously and returns a future
     *                containing the response; the implementation should cache this response
     * @return a {@link CompletableFuture} that will be completed with the cached or newly fetched response
     */
    <T> CompletableFuture getAsync(Key key, Supplier<CompletableFuture<HttpResponse<T>>> fetcher);

    /**
     * Creates a new builder for configuring cache options.
     *
     * @return a new {@link Builder} instance
     */
    static Builder builder(){
        return new Builder();
    }

    /**
     * A builder for configuring and creating cache instances.
     * <p>
     * Use this builder to configure cache properties such as maximum depth,
     * time-to-live, custom backing caches, and key generators.
     */
    class Builder {
        private int depth = 100;
        private long ttl = 0;
        private Cache backing;
        private KeyGenerator keyGen;

        /**
         * Builds and returns a configured {@link CacheManager} instance.
         *
         * @return a new CacheManager configured with this builder's settings
         */
        CacheManager build() {
            if(backing != null){
                return new CacheManager(backing, keyGen);
            }
            return new CacheManager(depth, ttl, keyGen);
        }

        /**
         * Defines the maximum depth of the cache in number of entries.
         * <p>
         * When the cache exceeds this depth, the oldest elements are purged on read.
         * Defaults to 100 entries.
         * <p>
         * Note: Custom {@link Cache} implementations may not honor this setting.
         *
         * @param value the maximum number of entries to store in the cache
         * @return this builder for method chaining
         */
        public Builder depth(int value) {
            this.depth = value;
            return this;
        }

        /**
         * Sets the time-to-live (TTL) for cached response entries.
         * <p>
         * Entries older than the TTL are considered stale and purged on read.
         * By default, there is no TTL and entries are kept indefinitely
         * (subject to depth limits).
         * <p>
         * Note: Custom {@link Cache} implementations may not honor this setting.
         *
         * @param number the duration value
         * @param units the time unit for the duration
         * @return this builder for method chaining
         */
        public Builder maxAge(long number, TimeUnit units) {
            this.ttl = units.toMillis(number);
            return this;
        }

        /**
         * Sets a custom backing cache implementation.
         * <p>
         * Use this to integrate with external caching systems. When a custom
         * backing cache is provided, the {@link #depth(int)} and {@link #maxAge(long, TimeUnit)}
         * settings are ignored; the custom cache must implement its own purging rules.
         *
         * @param cache the custom cache implementation
         * @return this builder for method chaining
         */
        public Builder backingCache(Cache cache) {
            this.backing = cache;
            return this;
        }

        /**
         * Provides a custom key generator for cache lookups.
         * <p>
         * The default key is generated from a hash of the request details,
         * the execution type (sync/async), and the response type.
         *
         * @param keyGenerator a custom cache key generator
         * @return this builder for method chaining
         */
        public Builder withKeyGen(KeyGenerator keyGenerator) {
            this.keyGen = keyGenerator;
            return this;
        }
    }

    /**
     * A functional interface for generating cache keys from HTTP requests.
     * <p>
     * Implement this interface to customize how cache keys are generated.
     * The generated key determines cache hit/miss behavior.
     * </p>
     */
    @FunctionalInterface
    interface KeyGenerator {
        /**
         * Generates a cache key for the given request.
         *
         * @param request the HTTP request to generate a key for
         * @param isAsync {@code true} if the request is being executed asynchronously,
         *                {@code false} for synchronous execution
         * @param responseType the expected response body type (e.g., String.class, JsonNode.class)
         * @return a {@link Key} that uniquely identifies this request for caching purposes
         */
        Key apply(HttpRequest request, Boolean isAsync, Class<?> responseType);
    }

    /**
     * Interface for cache keys used to identify cached responses.
     * <p>
     * Implementations must properly implement {@link #equals(Object)} and {@link #hashCode()}
     * to ensure correct cache lookup behavior. The key must also track its creation time
     * to support time-based expiration.
     * </p>
     */
    interface Key {
        /**
         * Compares this key with another object for equality.
         * <p>
         * Two keys are equal if they represent the same cached request. Implementations
         * should compare all relevant request characteristics used to generate the key.
         * </p>
         *
         * @param obj the object to compare with
         * @return {@code true} if this key equals the specified object, {@code false} otherwise
         */
        @Override
        boolean equals(Object obj);

        /**
         * Returns a hash code for this key.
         * <p>
         * The hash code must be consistent with {@link #equals(Object)} such that
         * equal keys produce the same hash code. This is essential for proper
         * cache lookup using hash-based data structures.
         * </p>
         *
         * @return a hash code value for this key
         */
        @Override
        int hashCode();

        /**
         * Returns the time when this key was created.
         * <p>
         * This timestamp is used by cache purging functions to determine
         * if a cached entry has exceeded its time-to-live.
         * </p>
         *
         * @return the creation time as an {@link Instant}
         */
        Instant getTime();
    }
}
