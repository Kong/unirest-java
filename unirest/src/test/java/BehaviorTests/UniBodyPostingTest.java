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

import kong.unirest.JsonNode;
import kong.unirest.MockCallback;
import kong.unirest.Unirest;
import kong.unirest.UnirestConfigException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static kong.unirest.TestUtil.assertException;

public class UniBodyPostingTest extends BddTest {
    @Test
    public void canSetCharsetOfBody(){
        Unirest.post(MockServer.POST)
                .charset(StandardCharsets.US_ASCII)
                .body("foo")
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("text/plain; charset=US-ASCII")
                .asserBody("foo")
                .assertCharset(StandardCharsets.US_ASCII);
    }

    @Test
    public void canSetCharsetOfBodyAfterMovingToBody(){
        Unirest.post(MockServer.POST)
                .body("foo")
                .charset(StandardCharsets.US_ASCII)
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("text/plain; charset=US-ASCII")
                .asserBody("foo")
                .assertCharset(StandardCharsets.US_ASCII);
    }

    @Test
    public void testPostRawBody() {
        String sourceString = "'\"@こんにちは-test-123-" + Math.random();
        byte[] sentBytes = sourceString.getBytes();

        Unirest.post(MockServer.POST)
                .body(sentBytes)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody(sourceString);
    }

    @Test
    public void testAsyncCustomContentType() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{\"hello\":\"world\"}")
                .asJsonAsync(new MockCallback<>(this, r -> parse(r)
                        .asserBody("{\"hello\":\"world\"}")
                        .assertHeader("Content-Type", "application/json"))
                );

        assertAsync();
    }


    @Test
    public void postAPojoObjectUsingTheMapper() {
        GetResponse postResponseMock = new GetResponse();
        postResponseMock.setUrl(MockServer.POST);

        Unirest.post(postResponseMock.getUrl())
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(postResponseMock)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("{\"url\":\"http://localhost:4567/post\"}");
    }

    @Test
    public void testDeleteBody() {
        String body = "{\"jsonString\":{\"members\":\"members1\"}}";
        Unirest.delete(MockServer.DELETE)
                .body(body)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody(body);
    }

    @Test
    public void postBodyAsJson() {
        JSONObject body = new JSONObject();
        body.put("krusty","krab");

        Unirest.post(MockServer.POST)
                .body(body)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("{\"krusty\":\"krab\"}");
    }

    @Test
    public void postBodyAsJsonArray() {
        JSONArray body = new JSONArray();
        body.put(0, "krusty");
        body.put(1, "krab");

        Unirest.post(MockServer.POST)
                .body(body)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("[\"krusty\",\"krab\"]");
    }

    @Test
    public void postBodyAsJsonNode() {
        JsonNode body = new JsonNode("{\"krusty\":\"krab\"}");

        Unirest.post(MockServer.POST)
                .body(body)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("{\"krusty\":\"krab\"}");
    }

    @Test
    public void cantPostObjectWithoutObjectMapper(){
        Unirest.config().setObjectMapper(null);

        assertException(() -> Unirest.post(MockServer.POST).body(new Foo("die")),
                UnirestConfigException.class,
                "No Object Mapper Configured. Please config one with Unirest.config().setObjectMapper");
    }
}
