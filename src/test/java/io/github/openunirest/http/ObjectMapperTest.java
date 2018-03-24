package io.github.openunirest.http;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class ObjectMapperTest extends BddTest {
    @Test
    public void canGetObjectResponse() {
         Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    public void canGetObjectResponseAsync() throws ExecutionException, InterruptedException {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    public void canGetObjectResponseAsyncWithCallback() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObjectAsync(RequestCapture.class, r -> {
                    RequestCapture cap = r.getBody();
                    cap.assertParam("foo", "bar");
                    asyncSuccess();
                });

        assertAsync();
    }
}
