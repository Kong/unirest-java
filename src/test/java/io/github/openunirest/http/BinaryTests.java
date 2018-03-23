package io.github.openunirest.http;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class BinaryTests extends BddTest {
    @Test
    public void canGetBinaryResponse() throws IOException {
        HttpResponse<InputStream> i = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asBinary();

        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);

        cap.assertParam("foo", "bar");
    }
}
