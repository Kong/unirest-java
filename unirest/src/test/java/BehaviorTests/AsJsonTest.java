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
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AsJsonTest extends BddTest {

    @Test
    public void whenNoBodyIsReturned() {
        HttpResponse<JsonNode> i = Unirest.get(MockServer.NOBODY).asJson();

        assertEquals(HttpStatus.OK, i.getStatus());
        assertEquals("{}", i.getBody().toString());
    }

    @Test
    public void toStringAObject() {
        MockServer.setStringResponse(new JSONObject().put("f",1).put("a", Arrays.asList(2,3,4)).toString());
        HttpResponse<JsonNode> i = Unirest.get(MockServer.GET).asJson();

        assertEquals("{\"f\":1,\"a\":[2,3,4]}", i.getBody().toString());
    }

    @Test
    public void toPrettyStringAObject() {
        MockServer.setStringResponse(new JSONObject().put("f",1).put("a", Arrays.asList(2,3,4)).toString());
        HttpResponse<JsonNode> i = Unirest.get(MockServer.GET).asJson();

        assertEquals("{\n" +
                "  \"f\": 1,\n" +
                "  \"a\": [\n" +
                "    2,\n" +
                "    3,\n" +
                "    4\n" +
                "  ]\n" +
                "}", i.getBody().toPrettyString());
    }

    @Test
    public void canGetBinaryResponse() {
        HttpResponse<JsonNode> i = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asJson();

        assertJson(i);
    }

    @Test
    public void canGetBinaryResponseAsync() throws Exception {
        CompletableFuture<HttpResponse<JsonNode>> r = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asJsonAsync();

        assertJson(r.get());
    }

    @Test
    public void canGetBinaryResponseAsyncWithCallback() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asJsonAsync(r -> {
                    assertJson(r);
                    asyncSuccess();
                });

        assertAsync();
    }

    @Test
    public void failureToReturnValidJsonWillResultInAnEmptyNode() {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.INVALID_REQUEST).asJson();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertNull(response.getBody());
        UnirestParsingException ex = response.getParsingError().get();
        assertEquals("You did something bad", ex.getOriginalBody());
        assertEquals("kong.unirest.json.JSONException: Invalid JSON",
                response.getParsingError().get().getMessage());

    }

    @Test
    public void failureToReturnValidJsonWillResultInAnEmptyNodeAsync() {
        Unirest.get(MockServer.INVALID_REQUEST)
                .asJsonAsync(new MockCallback<>(this, response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
                    assertNull(response.getBody());
                    assertEquals(null, response.getBody());
                    assertEquals("kong.unirest.json.JSONException: Invalid JSON",
                            response.getParsingError().get().getMessage());

                }));

        assertAsync();
    }

    private void assertJson(HttpResponse<JsonNode> i) {
        assertEquals("bar",i.getBody().getObject().getJSONObject("params").getJSONArray("foo").get(0));
    }
}
