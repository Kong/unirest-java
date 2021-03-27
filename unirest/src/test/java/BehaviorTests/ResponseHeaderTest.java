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

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import kong.unirest.Header;
import kong.unirest.Headers;
import kong.unirest.Unirest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseHeaderTest extends BddTest {


    @Test
    void readingResponseHeaders() {
        MockServer.addResponseHeader("zed", "oranges");
        MockServer.addResponseHeader("alpha", "apples");
        MockServer.addResponseHeader("zed", "grapes");
        MockServer.expectCookie("JSESSIONID", "ABC123");

        Headers h = Unirest.get(MockServer.GET).asString().getHeaders();

       // assertHeader("Date", "Fri, 04 Jan 2019 01:46:34 GMT", h.all().get(0));
        assertEquals("Javalin", h.getFirst("Server"));
        assertEquals("text/plain;charset=utf-8", h.getFirst("Content-Type"));
        assertEquals("JSESSIONID=ABC123; Path=/", h.getFirst("Set-Cookie"));
        assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", h.getFirst("Expires"));
        assertEquals(Lists.newArrayList("oranges", "grapes"), h.get("zed"));
        assertEquals("apples", h.getFirst("alpha"));
    }
}
