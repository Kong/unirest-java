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

import kong.unirest.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableMap.of;
import static kong.unirest.Cache.builder;
import static org.junit.jupiter.api.Assertions.*;

class CachingTest extends BddTest {

    @Test
    void doesNotCacheByDefault() {
        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET).asEmpty();

        assertEquals(2,  MockServer.timesCalled);
    }

    @Test
    void canCacheResponsesForEqualRequests() {
        Unirest.config().cacheResponses(true);

        String r1 = Unirest.get(MockServer.GET).asObject(RequestCapture.class).getBody().requestId;
        String r2 = Unirest.get(MockServer.GET).asObject(RequestCapture.class).getBody().requestId;

        assertEquals(1,  MockServer.timesCalled);
        assertEquals(r1, r2);
    }

    @Test
    void canCacheResponsesForEqualRequests_async() throws Exception {
        Unirest.config().cacheResponses(true);

        String r1 = Unirest.get(MockServer.GET).asObjectAsync(RequestCapture.class).get().getBody().requestId;
        String r2 = Unirest.get(MockServer.GET).asObjectAsync(RequestCapture.class).get().getBody().requestId;

        assertEquals(1,  MockServer.timesCalled);
        assertEquals(r1, r2);
    }

    @Test
    void theSameRequestForDifferentTypesAreDifferent() {
        Unirest.config().cacheResponses(true);

        assertSame(
                Unirest.get(MockServer.GET).asString(),
                Unirest.get(MockServer.GET).asString()
        );

        assertNotSame(
                Unirest.get(MockServer.GET).asString(),
                Unirest.get(MockServer.GET).asObject(RequestCapture.class)
        );

        assertEquals(2, MockServer.timesCalled);
    }

    @Test
    void allTheTypesAreUniqueSnowflakes() {
        Unirest.config().cacheResponses(true);

        assertNull(Unirest.get(MockServer.GET).asEmpty().getBody());
        assertNull(Unirest.get(MockServer.GET).asEmpty().getBody());
        assertNotNull(Unirest.get(MockServer.GET).asObject(RequestCapture.class).getBody());
        assertNotNull(Unirest.get(MockServer.GET).asObject(RequestCapture.class).getBody());
        assertNotNull(Unirest.get(MockServer.GET).asString().getBody());
        assertNotNull(Unirest.get(MockServer.GET).asString().getBody());
        assertNotNull(Unirest.get(MockServer.GET).asJson().getBody());
        assertNotNull(Unirest.get(MockServer.GET).asJson().getBody());
        assertNotNull(Unirest.get(MockServer.GET).asBytes().getBody());
        assertNotNull(Unirest.get(MockServer.GET).asBytes().getBody());

        assertEquals(5, MockServer.timesCalled);
    }

    @Test
    void canSetAMaxDepthOfCache() {
        Unirest.config().cacheResponses(builder().depth(5));

        IntStream.range(0,10).forEach(i -> {
            Unirest.get(MockServer.GET).queryString("no",i).asEmpty();
            Unirest.get(MockServer.GET).queryString("no",i).asEmpty();
        });

        IntStream.range(5,10).forEach(i -> {
            Unirest.get(MockServer.GET).queryString("no",i).asEmpty();
            Unirest.get(MockServer.GET).queryString("no",i).asEmpty();
        });

        assertEquals(10, MockServer.timesCalled);

        Unirest.get(MockServer.GET).queryString("no",0).asEmpty();

        assertEquals(11, MockServer.timesCalled);
    }

    @Test
    void canSetATTLOfACacheEntry() {
        Instant now = Instant.now();
        TestUtil.freeze(now);

        Unirest.config().cacheResponses(builder().maxAge(5, TimeUnit.MINUTES));

        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET).asEmpty();

        assertEquals(1, MockServer.timesCalled);

        TestUtil.freeze(now.plus(10, ChronoUnit.MINUTES));

        Unirest.get(MockServer.GET).asEmpty();

        assertEquals(2, MockServer.timesCalled);
    }

    @Test
    void doesNotCacheByDefault_json() {
        HttpResponse<JsonNode> response1 = Unirest.get(MockServer.GET).queryString(of("a",1)).asJson();
        HttpResponse<JsonNode> response2 = Unirest.get(MockServer.GET).queryString(of("a",1)).asJson();

        assertNotEquals(
                response1.getBody().getObject().getString("requestId"),
                response2.getBody().getObject().getString("requestId")
        );
        assertEquals(2,  MockServer.timesCalled);
    }

    @Test
    void willCacheIfEnabled_json() {
        Unirest.config().cacheResponses(true);
        HttpResponse<JsonNode> response1 = Unirest.get(MockServer.GET).queryString(of("a",1)).asJson();
        HttpResponse<JsonNode> response2 = Unirest.get(MockServer.GET).queryString(of("a",1)).asJson();

        assertEquals(
                response1.getBody().getObject().getString("requestId"),
                response2.getBody().getObject().getString("requestId")
        );
        assertEquals(1,  MockServer.timesCalled);
    }
}
