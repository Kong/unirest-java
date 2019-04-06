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
import kong.unirest.Header;
import kong.unirest.Headers;
import kong.unirest.Unirest;

import static org.junit.Assert.assertEquals;

public class ResponseHeaderTest extends BddTest {


    @Test
    public void responseHeadersAreInTheSameOrderAsTheResponse() {
        MockServer.addResponseHeader("zed", "oranges");
        MockServer.addResponseHeader("alpha", "apples");
        MockServer.addResponseHeader("Content", "application/xml");
        MockServer.addResponseHeader("zed", "grapes");
        MockServer.expectCookie("JSESSIONID", "ABC123");

        Headers h = Unirest.get(MockServer.GET).asString().getHeaders();

       // assertHeader("Date", "Fri, 04 Jan 2019 01:46:34 GMT", h.all().get(0));
        assertHeader("Set-Cookie", "JSESSIONID=ABC123", h.all().get(1));
        assertHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT", h.all().get(2));
        assertHeader("zed", "oranges", h.all().get(3));
        assertHeader("alpha", "apples", h.all().get(4));
        assertHeader("Content", "application/xml", h.all().get(5));
        assertHeader("zed", "grapes", h.all().get(6));
        assertHeader("Content-Type", "text/html;charset=utf-8", h.all().get(7));
        assertHeader("Transfer-Encoding", "chunked", h.all().get(8));
        assertHeader("Server", "Jetty(9.4.12.v20180830)", h.all().get(9));

    }

    private void assertHeader(String name, String value, Header header) {
        assertEquals(name, header.getName());
        assertEquals(value, header.getValue());
    }
}
