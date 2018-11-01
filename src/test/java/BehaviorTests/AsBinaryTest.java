package BehaviorTests;

import unirest.HttpResponse;
import unirest.Unirest;
import org.junit.Test;
import unirest.TestUtil;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class AsBinaryTest extends BddTest {
    @Test
    public void canGetBinaryResponse() {
        HttpResponse<InputStream> i = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asBinary();

        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);
        cap.assertParam("foo", "bar");
    }

    @Test
    public void canGetBinaryResponseAsync() throws Exception {
        CompletableFuture<HttpResponse<InputStream>> r = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asBinaryAsync();

        RequestCapture cap = TestUtil.readValue(r.get().getBody(), RequestCapture.class);
        cap.assertParam("foo", "bar");
    }

    @Test
    public void canGetBinaryResponseAsyncWithCallback() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asBinaryAsync(r -> {
                    RequestCapture cap = TestUtil.readValue(r.getBody(), RequestCapture.class);
                    cap.assertParam("foo", "bar");
                    asyncSuccess();
                });

        assertAsync();
    }
}
