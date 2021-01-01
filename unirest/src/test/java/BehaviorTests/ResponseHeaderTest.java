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
import kong.unirest.Header;
import kong.unirest.Headers;
import kong.unirest.Unirest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ResponseHeaderTest extends BddTest {

    @Test
    void responseHeadersAreInTheSameOrderAsTheResponse() {
        MockServer.addResponseHeader("zed", "oranges");
        MockServer.addResponseHeader("alpha", "apples");
        MockServer.addResponseHeader("Content", "application/xml");
        MockServer.expectCookie("JSESSIONID", "ABC123");

        Headers h = Unirest.get(MockServer.GET).asString().getHeaders();

       // assertHeader("Date", "Fri, 04 Jan 2019 01:46:34 GMT", h.all().get(0));
        assertHeader("Set-Cookie", "JSESSIONID=ABC123", h);
        assertHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT", h);
        assertHeader("zed", "oranges", h);
        assertHeader("alpha", "apples", h);
        assertHeader("Content", "application/xml", h);
        assertHeader("Content-Type", "text/html;charset=utf-8", h);
        assertHeader("Transfer-Encoding", "chunked", h);
    }

    @Test
    void multipleHeaders() {
        MockServer.addResponseHeader("fruit", "oranges");
        MockServer.addResponseHeader("fruit", "apples");
        MockServer.addResponseHeader("fruit", "grapes");

        Headers h = Unirest.get(MockServer.GET).asString().getHeaders();

        List<String> headers = h.get("fruit");
        assertEquals(3, headers.size());
        assertEquals(Arrays.asList("oranges","apples","grapes"), headers);
    }

    private void assertHeader(String name, String value, Headers header) {
        List<String> v = header.get(name);
        assertFalse(v.isEmpty(), "Missing header for " + name);
        assertEquals(1, v.size(), "Expected only 1 header for " + name);
        assertEquals(value, v.get(0));
    }
}
