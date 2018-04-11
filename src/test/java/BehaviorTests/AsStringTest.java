package BehaviorTests;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.Unirest;
import org.junit.Test;
import util.TestUtil;

import java.util.concurrent.CompletableFuture;

public class AsStringTest extends BddTest {


    @Test
    public void canGetBinaryResponse() {
        HttpResponse<String> i = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asString();

        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);
        cap.assertParam("foo", "bar");
    }

    @Test
    public void canGetBinaryResponseAsync() throws Exception {
        CompletableFuture<HttpResponse<String>> r = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asStringAsync();

        RequestCapture cap = TestUtil.readValue(r.get().getBody(), RequestCapture.class);
        cap.assertParam("foo", "bar");
    }

    @Test
    public void canGetBinaryResponseAsyncWithCallback() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asStringAsync(r -> {
                    RequestCapture cap = TestUtil.readValue(r.getBody(), RequestCapture.class);
                    cap.assertParam("foo", "bar");
                    asyncSuccess();
                });

        assertAsync();
    }
}
