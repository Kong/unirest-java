/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

import BehaviorTests.Foo;
import unirest.MockApacheResponse;
import unirest.*;
import unirest.JacksonObjectMapper;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import unirest.TestUtil;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;
import static unirest.TestUtil.assertException;
import static unirest.TestUtil.toJson;

public class ResponseBuilderTest {
    private final ResponseBuilder builder = new ResponseBuilder();
    private MockApacheResponse response;

    @Before
    public void setUp() {
        response = new MockApacheResponse();
        Options.setOption(Option.OBJECT_MAPPER, new JacksonObjectMapper());
    }

    @Test
    public void willStartOffWithHeaders() {
        response.addHeader("foo", "bar");
        response.addHeader("foo", "baz");
        response.addHeader("qux", "zip");

        HttpResponse<InputStream> r = builder.asBinary(response);

        assertThat(r.getHeaders().get("foo"), hasItem("bar"));
        assertThat(r.getHeaders().get("qux"), hasItem("zip"));
    }

    @Test
    public void willPassInStatusCode() {
        response.setStatusLine(new BasicStatusLine(new ProtocolVersion("http", 1, 2),
                418, "I am a teapot"));

        HttpResponse<InputStream> r = builder.asBinary(response);

        assertEquals(418, r.getStatus());
        assertEquals("I am a teapot", r.getStatusText());
    }

    @Test
    public void canGetBinaryResponse() {
        response.setBody("I like cheese");

        HttpResponse<InputStream> r = builder.asBinary(response);

        assertEquals("I like cheese", TestUtil.toString(r.getBody()));
    }

    @Test
    public void canGetObjectMappedResponse() {
        response.setBody(toJson(new Foo("I like cheese")));

        HttpResponse<Foo> r = builder.asObject(response, Foo.class);

        assertEquals("I like cheese", r.getBody().bar);
    }

    @Test
    public void willThrowErrorIfNoObjectMapperIsConfigured(){
        Options.removeOption(Option.OBJECT_MAPPER);

        response.setBody(toJson(new Foo("I like cheese")));

        assertException(() -> builder.asObject(response, Foo.class),
                UnirestException.class,
                "No Object Mapper Configured. Please configure one with Unirest.setObjectMapper");
    }

    @Test
    public void ifObjectMapperHasProblemParsingReturnTheError(){
        response.setBody("I like cheese");

        HttpResponse<Foo> foo = builder.asObject(response, Foo.class);

        assertEquals("com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'I': was expecting 'null', 'true', 'false' or NaN\n" +
                " at [Source: (String)\"I like cheese\"; line: 1, column: 2]",
                foo.getParsingError().get().getMessage());
    }

    @Test
    public void canGetStringResponse() {
        response.setBody("I like cheese");

        HttpResponse<String> r = builder.asString(response);

        assertEquals("I like cheese", r.getBody());
    }

    @Test
    public void canGetJsonResponse() {
        response.setBody("{\"foo\": \"I like cheese\"}");

        HttpResponse<JsonNode> r = builder.asJson(response);

        assertEquals("I like cheese", r.getBody().getObject().get("foo"));
    }

    @Test
    public void willNotBlowUpOnError() {
        response.setBody("I like cheese");

        HttpResponse<JsonNode> r = builder.asJson(response);

        assertNull(r.getBody());
        assertEquals("org.json.JSONException: A JSONArray text must start with '[' at 1 [character 2 line 1]",
                r.getParsingError().get().getMessage());
    }
}