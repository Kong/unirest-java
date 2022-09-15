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

import com.google.common.cache.CacheBuilder;
import kong.unirest.Cache;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class CachingAlternativeTest extends BddTest {

    @Test
    void canSupplyCustomCache() {
        GuavaCache cache = new GuavaCache();
        Unirest.config().cacheResponses(Cache.builder().backingCache(cache));

        assertSame(
                Unirest.get(MockServer.GET).asString(),
                Unirest.get(MockServer.GET).asString()
        );

        assertNotSame(
                Unirest.get(MockServer.GET).asString(),
                Unirest.get(MockServer.GET).asObject(RequestCapture.class)
        );

        assertEquals(2, MockServer.timesCalled);

        cache.invalidate();

        assertSame(
                Unirest.get(MockServer.GET).asString(),
                Unirest.get(MockServer.GET).asString()
        );

        assertEquals(3, MockServer.timesCalled);
    }

    @Test
    void supplyCustomCacheKeyFunction() {
        Unirest.config().cacheResponses(Cache.builder().withKeyGen((r,a,z) -> new CustomKey()));

        assertSame(
                Unirest.get(MockServer.GET).asString(),
                Unirest.get(MockServer.POST).asString()
        );

        assertEquals(1, MockServer.timesCalled);
    }

    public static class GuavaCache implements Cache{
        com.google.common.cache.Cache<Key, HttpResponse> regular = CacheBuilder.newBuilder().build();
        com.google.common.cache.Cache<Key, CompletableFuture> async = CacheBuilder.newBuilder().build();
        @Override
        public <T> HttpResponse get(Key key, Supplier<HttpResponse<T>> fetcher) {
            try {
                return regular.get(key, fetcher::get);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T> CompletableFuture getAsync(Key key, Supplier<CompletableFuture<HttpResponse<T>>> fetcher) {
            try {
                return async.get(key, fetcher::get);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        public void invalidate() {
            regular.invalidateAll();
            async.invalidateAll();
        }
    }

    class CustomKey implements Cache.Key {
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }

        @Override
        public Instant getTime() {
            return  Instant.now();
        }
    }
}
