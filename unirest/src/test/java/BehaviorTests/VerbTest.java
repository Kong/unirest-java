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

import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerbTest extends BddTest {

    @Test
    void get() {
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMethod(HttpMethod.GET);
    }

    @Test
    void post() {
        Unirest.post(MockServer.POST)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMethod(HttpMethod.POST);
    }

    @Test
    void put() {
        Unirest.put(MockServer.POST)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMethod(HttpMethod.PUT);
    }

    @Test
    void patch() {
        Unirest.patch(MockServer.PATCH)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMethod(HttpMethod.PATCH);
    }

    @Test
    void head() {
        HttpResponse response = Unirest.head(MockServer.GET).asEmpty();

        assertEquals(200, response.getStatus());
        assertEquals("text/html;charset=utf-8", response.getHeaders().getFirst("Content-Type"));
    }

    @Test
    void option() {
        Unirest.options(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMethod(HttpMethod.OPTIONS);
    }

    @Test
    void weirdVerbs() {
        Unirest.request("CHEESE", MockServer.CHEESE)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMethod(HttpMethod.valueOf("CHEESE"))
                .assertBody("");
    }
}
