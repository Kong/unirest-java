package io.github.openunirest.request;

import BehaviorTests.Foo;
import io.github.openunirest.MockApacheResponse;
import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.JsonNode;
import io.github.openunirest.http.exceptions.UnirestException;
import io.github.openunirest.http.options.Option;
import io.github.openunirest.http.options.Options;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import util.JacksonObjectMapper;
import util.TestUtil;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;
import static util.TestUtil.assertException;
import static util.TestUtil.toJson;

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