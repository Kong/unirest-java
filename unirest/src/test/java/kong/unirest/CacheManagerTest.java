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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static kong.unirest.HttpMethod.GET;
import static org.junit.jupiter.api.Assertions.*;

class CacheManagerTest {
    Config config = new Config();
    CacheManager cache;
    MockClient client;

    @BeforeEach
    void setUp() {
        cache = new CacheManager();
        client = new MockClient();
    }

    @Test
    void cacheSameRequests() {
        assertSame(
                cache.wrap(client).request(new HttpRequestNoBody(config, GET, "/"), null, Object.class),
                cache.wrap(client).request(new HttpRequestNoBody(config, GET, "/"), null, Object.class)
        );

        assertEquals(1, client.invokes);
    }

    @Test
    void cacheSameRequestsAsync() {
        assertSame(
                cache.wrapAsync(client).request(new HttpRequestNoBody(config, GET, "/"), null,null, Empty.class),
                cache.wrapAsync(client).request(new HttpRequestNoBody(config, GET, "/"), null,null, Empty.class)
        );

        assertEquals(1, client.invokes);
    }


    @Test
    void asyncAndSyncrequestsAreDifferent() {
        assertNotSame(
                cache.wrap(client).request(new HttpRequestNoBody(config, GET, "/"), null, Object.class),
                cache.wrapAsync(client).request(new HttpRequestNoBody(config, GET, "/"), null, null, Object.class)
        );

        assertEquals(2, client.invokes);
    }

    @Test
    void responsesAreDifferentForDifferentTypes() {
        assertNotSame(
                cache.wrap(client).request(new HttpRequestNoBody(config, GET, "/"), r -> new StringResponse(new TestRawResponse(config), ""), String.class),
                cache.wrap(client).request(new HttpRequestNoBody(config, GET, "/"), r -> new BasicResponse(new TestRawResponse(config), ""), Empty.class)
        );

        assertEquals(2, client.invokes);
    }

    private static class MockClient implements Client, AsyncClient {
        public int invokes = 0;
        @Override
        public <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, CompletableFuture<HttpResponse<T>> callback) {
            invokes++;
            return new CompletableFuture<>();
        }

        @Override
        public Object getClient() {
            return null;
        }

        @Override
        public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer) {
            invokes++;
            return new MockResponse();
        }

        @Override
        public Stream<Exception> close() {
            return null;
        }

        @Override
        public void registerShutdownHook() {

        }
    }
}