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
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static java.util.Arrays.asList;
import static kong.unirest.TestUtil.rezFile;
import static org.junit.Assert.assertEquals;

public class FormPostingTest extends BddTest {

    @Test
    public void testFormFields() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param1", "value1")
                .field("param2", "bye")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("param1", "value1")
                .assertParam("param2", "bye")
                .assertUrlEncodedContent();
    }

    @Test
    public void formPostAsync() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param1", "value1")
                .field("param2", "bye")
                .asJsonAsync(new MockCallback<>(this, r -> {
                    RequestCapture req = parse(r);
                    req.assertParam("param1", "value1");
                    req.assertParam("param2", "bye");
                    req.assertUrlEncodedContent();
                }));

        assertAsync();
    }

    @Test
    public void testAsyncCustomContentTypeAndFormParams() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("name", "Mark")
                .field("hello", "world")
                .asJsonAsync(new MockCallback<>(this, r -> parse(r)
                        .assertParam("name", "Mark")
                        .assertParam("hello", "world")
                        .assertContentType("application/x-www-form-urlencoded")
                ));

        assertAsync();
    }

    @Test
    public void testPostArray() {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("name", "Tom")
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrlEncodedContent()
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testPostUTF8() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param3", "こんにちは")
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrlEncodedContent()
                .assertParam("param3", "こんにちは");
    }

    @Test
    public void testPostCollection() {
        Unirest.post(MockServer.POST)
                .field("name", asList("Mark", "Tom"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrlEncodedContent()
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void nullMapDoesntBomb() {
        Unirest.post(MockServer.POST)
                .queryString("foo","bar")
                .fields(null)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrlEncodedContent()
                .assertParam("foo", "bar");
    }

    @Test
    public void testDelete() {
        Unirest.delete(MockServer.DELETE)
                .field("name", "mark")
                .field("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrlEncodedContent()
                .assertParam("name", "mark")
                .assertParam("foo", "bar");
    }

    @Test
    public void testChangingEncodingToForms(){
        Unirest.post(MockServer.POST)
                .charset(StandardCharsets.US_ASCII)
                .field("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("application/x-www-form-urlencoded; charset=US-ASCII")
                .assertParam("foo", "bar")
                .assertCharset(StandardCharsets.US_ASCII);
    }

    @Test
    public void testChangingEncodingAfterMovingToForm(){
        Unirest.post(MockServer.POST)
                .field("foo", "bar")
                .charset(StandardCharsets.US_ASCII)
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("application/x-www-form-urlencoded; charset=US-ASCII")
                .assertParam("foo", "bar")
                .assertCharset(StandardCharsets.US_ASCII);
    }

}
