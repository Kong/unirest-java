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


import io.javalin.http.Header;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsStringTest extends BddTest {

    @Test
    void simpleExample() {
        MockServer.setStringResponse("Hi Mom");
        String body = Unirest.get(MockServer.GET).asString().getBody();
        assertEquals("Hi Mom", body);
    }

    @Test
    void whenNoBodyIsReturned() {
        HttpResponse<String> i = Unirest.get(MockServer.NOBODY).asString();

        assertEquals(200, i.getStatus());
        assertEquals("", i.getBody());
    }

    @Test
    void canParseGzippedStringResponse() {
        HttpResponse<String> i = Unirest.get(MockServer.GZIP)
                .queryString("foo", "bar")
                .header(Header.ACCEPT_ENCODING, "gzip")
                .asString();

        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);
        assertEquals(200, i.getStatus());
        cap.assertParam("foo", "bar");
    }

    @Test
    void canParseGzippedResponseAsync() throws Exception {
        HttpResponse<String> i = Unirest.get(MockServer.GZIP)
                .queryString("foo", "bar")
                .asStringAsync().get();

        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);
        assertEquals(200, i.getStatus());
        cap.assertParam("foo", "bar");
    }

    @Test
    void canGetBinaryResponse() {
        HttpResponse<String> i = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asString();

        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);
        cap.assertParam("foo", "bar");
    }

    @Test
    void canGetBinaryResponseAsync() throws Exception {
        CompletableFuture<HttpResponse<String>> r = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asStringAsync();

        RequestCapture cap = TestUtil.readValue(r.get().getBody(), RequestCapture.class);
        cap.assertParam("foo", "bar");
    }

    @Test
    void canGetBinaryResponseAsyncWithCallback() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asStringAsync(r -> {
                    RequestCapture cap = TestUtil.readValue(r.getBody(), RequestCapture.class);
                    cap.assertParam("foo", "bar");
                    asyncSuccess();
                });

        assertAsync();
    }

    @Test
    void unicodeResponse() {
        MockServer.setStringResponse("ěščřžýáíé");

        assertEquals("ěščřžýáíé", Unirest.get(MockServer.GET).asString().getBody());
    }

    @Test
    void unicodeResponseAsync() throws Exception {
        MockServer.setStringResponse("ěščřžýáíé");

        Unirest.get(MockServer.GET)
                .asStringAsync(r -> {
                    assertEquals("ěščřžýáíé", r.getBody());
                    asyncSuccess();
                });

        assertAsync();
    }

    @Test @Disabled
    void canSetExpectedCharsetOfResponse() {
        HttpResponse<String> response = Unirest.get(MockServer.WINDOWS_LATIN_1_FILE)
                .responseEncoding("windows-1250")
                .asString();

        assertEquals(200, response.getStatus());
        assertEquals("šžýáíé", response.getBody());
    }

    @Test @Disabled
    void canSetDefaultCharsetOfResponse() {
        Unirest.config().setDefaultResponseEncoding("windows-1250");

        HttpResponse<String> response = Unirest.get(MockServer.WINDOWS_LATIN_1_FILE)
                .asString();

        assertEquals(200, response.getStatus());
        assertEquals("šžýáíé", response.getBody());
    }
}
