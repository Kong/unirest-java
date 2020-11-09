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

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GZipTest extends BddTest {

    @Test
    void emptyGzip() {
        HttpResponse<String> result = Unirest.post(MockServer.EMPTY_GZIP)
                .asString();
        assertFalse(result.getParsingError().isPresent());
        assertEquals("", result.getBody());
    }

    @Test
    void testGzip() {
        HttpResponse<RequestCapture> resp = Unirest.get(MockServer.GZIP)
                .queryString("zipme", "up")
                .asObject(RequestCapture.class);

        resp.getBody()
                .assertParam("zipme", "up")
                .assertHeader("Accept-Encoding","gzip");

        assertEquals(0, resp.getHeaders().get("Content-Encoding").size());
    }

    @Test
    void testGzipAsync() throws Exception {
        HttpResponse<RequestCapture> resp = Unirest.get(MockServer.GZIP)
                .queryString("zipme", "up")
                .asObjectAsync(RequestCapture.class)
                .get();

        resp.getBody()
                .assertParam("zipme", "up")
                .assertHeader("Accept-Encoding", "gzip");

        assertEquals(0, resp.getHeaders().get("Content-Encoding").size());
    }

    @Test
    void canDisableGZip() throws Exception {
        Unirest.config().requestCompression(false);

        Unirest.get(MockServer.GET)
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertNoHeader("Accept-Encoding");
    }
}
