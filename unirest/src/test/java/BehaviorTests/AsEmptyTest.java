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

import org.junit.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import static junit.framework.TestCase.assertEquals;

public class AsEmptyTest extends BddTest {

    @Test
    public void canDoAEmptyRequestThatDoesNotParseBodyAtAll() {
        MockServer.addResponseHeader("foo", "bar");
        HttpResponse res = Unirest.get(MockServer.GET).asEmpty();

        assertEquals(200, res.getStatus());
        assertEquals(null, res.getBody());
        assertEquals("bar", res.getHeaders().getFirst("foo"));
    }

    @Test
    public void canDoEmptyAsync() throws Exception {
        MockServer.addResponseHeader("foo", "bar");
        HttpResponse res = Unirest.get(MockServer.GET).asEmptyAsync().get();

        assertEquals(200, res.getStatus());
        assertEquals(null, res.getBody());
        assertEquals("bar", res.getHeaders().getFirst("foo"));
    }

    @Test
    public void canDoEmptyAsyncWithCallback() {
        MockServer.addResponseHeader("foo", "bar");

        Unirest.get(MockServer.GET)
                .asEmptyAsync(res -> {
                    assertEquals(200, res.getStatus());
                    assertEquals(null, res.getBody());
                    assertEquals("bar", res.getHeaders().getFirst("foo"));
                    asyncSuccess();
                });

        assertAsync();
    }
}
