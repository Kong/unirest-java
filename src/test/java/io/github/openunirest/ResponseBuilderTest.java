package io.github.openunirest;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.ResponseBuilder;
import io.github.openunirest.http.TestUtil;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ResponseBuilderTest {
    private MockApacheResponse response;

    @Before
    public void setUp() {
        response = new MockApacheResponse();
    }

    @Test
    public void willStartOffWithHeaders() {
        response.addHeader("foo", "bar");
        response.addHeader("foo", "baz");
        response.addHeader("qux", "zip");

        HttpResponse<InputStream> r = new ResponseBuilder(response).asBinary();

        assertThat(r.getHeaders().get("foo"), hasItem("bar"));
        assertThat(r.getHeaders().get("qux"), hasItem("zip"));
    }

    @Test
    public void willPassInStatusCode() {
        response.setStatusLine(new BasicStatusLine(new ProtocolVersion("http", 1, 2),
                418, "I am a teapot"));

        HttpResponse<InputStream> r = new ResponseBuilder(response).asBinary();

        assertEquals(418, r.getStatus());
        assertEquals("I am a teapot", r.getStatusText());
    }

    @Test
    public void canGetBinaryResponse() {
        response.setBody("I like cheese");

        HttpResponse<InputStream> r = new ResponseBuilder(response).asBinary();

        assertEquals("I like cheese", TestUtil.toString(r.getBody()));
    }
}