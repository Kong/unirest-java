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

package kong.tests;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import kong.unirest.HttpMethod;
import kong.unirest.HttpResponseSummary;
import kong.unirest.HttpStatus;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RawResponseTest extends Base {

    @Test
    void getContentAsInputStream() {
        client.expect(HttpMethod.GET, path).thenReturn("hi");

        Unirest.get(path).thenConsume(raw -> {
            InputStream i = raw.getContent();
            String result = readToString(i);
            assertEquals("hi", result);
        });
    }

    @Test
    void getContentAsInputStreamReader() {
        client.expect(HttpMethod.GET, path).thenReturn("hi");

        Unirest.get(path).thenConsume(raw -> {
            InputStreamReader i = raw.getContentReader();
            String result = readToString(i);
            assertEquals("hi", result);
        });
    }

    @Test
    void getContentAsString() {
        client.expect(HttpMethod.GET, path).thenReturn("hi");

        Unirest.get(path).thenConsume(raw -> {
            String i = raw.getContentAsString();
            assertEquals("hi", i);
        });
    }

    @Test
    void getContentAsBytes() {
        client.expect(HttpMethod.GET, path).thenReturn("hi");

        Unirest.get(path).thenConsume(raw -> {
            byte[] i = raw.getContentAsBytes();
            assertEquals("hi", new String(i));
        });
    }

    @Test
    void getContentAsStringWithEncoding() {
        client.expect(HttpMethod.GET, path).thenReturn("hi");

        Unirest.get(path).thenConsume(raw -> {
            String i = raw.getContentAsString("UTF-8");
            assertEquals("hi", i);
        });
    }

    @Test
    void getContentType() {
        client.expect(HttpMethod.GET, path)
                .thenReturn("hi")
                .withHeader("Content-Type", "text/html");

        Unirest.get(path).thenConsume(raw -> {
            String i = raw.getContentType();
            assertEquals("text/html", i);
        });
    }

    @Test
    void getSummary() {
        client.expect(HttpMethod.GET, path)
                .thenReturn("hi")
                .withStatus(HttpStatus.IM_A_TEAPOT, "Tip me over and pour me out");

        Unirest.get(path).thenConsume(raw -> {
            HttpResponseSummary i = raw.toSummary();
            assertEquals(HttpStatus.IM_A_TEAPOT, i.getStatus());
            assertEquals("Tip me over and pour me out", i.getStatusText());
        });
    }

    @Test
    void getStatus() {
        client.expect(HttpMethod.GET, path)
                .thenReturn("hi")
                .withStatus(HttpStatus.IM_A_TEAPOT, "Tip me over and pour me out");

        Unirest.get(path).thenConsume(raw -> {
            assertEquals(HttpStatus.IM_A_TEAPOT, raw.getStatus());
            assertEquals("Tip me over and pour me out", raw.getStatusText());
        });
    }

    @Test
    void getEncoding() {
        client.expect(HttpMethod.GET, path)
                .thenReturn("hi")
                .withHeader("Content-Encoding", "Klingon-32");

        Unirest.get(path).thenConsume(raw -> {
            String encoding = raw.getEncoding();
            assertEquals("Klingon-32", encoding);
        });
    }

    private String readToString(InputStream i) {
        return readToString(new InputStreamReader(i, Charsets.UTF_8));
    }

    private String readToString(InputStreamReader i) {
        try {
            return CharStreams.toString(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
