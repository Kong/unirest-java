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

import org.junit.jupiter.api.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("rawtypes")
class AsEmptyTest extends BddTest {

    @Test
    void canDoAEmptyRequestThatDoesNotParseBodyAtAll() {
        MockServer.addResponseHeader("Content-Type", "json");
        HttpResponse res = Unirest.get(MockServer.GET).asEmpty();

        assertNull(res.getBody());
        assertEquals(200, res.getStatus());
        assertEquals("json;charset=utf-8", res.getHeaders().getFirst("Content-Type"));
    }

    @Test
    void canDoEmptyAsync() throws Exception {
        MockServer.addResponseHeader("Content-Type", "json");
        HttpResponse res = Unirest.get(MockServer.GET).asEmptyAsync().get();

        assertNull(res.getBody());
        assertEquals(200, res.getStatus());
        assertEquals("json;charset=utf-8", res.getHeaders().getFirst("Content-Type"));
    }

    @Test
    void canDoEmptyAsyncWithCallback() {
        MockServer.addResponseHeader("Content-Type", "json;charset=utf-8");

        Unirest.get(MockServer.GET)
                .asEmptyAsync(res -> {
                    assertNull(res.getBody());
                    assertEquals(200, res.getStatus());
                    assertEquals("json;charset=utf-8", res.getHeaders().getFirst("Content-Type"));
                    asyncSuccess();
                });

        assertAsync();
    }
}
