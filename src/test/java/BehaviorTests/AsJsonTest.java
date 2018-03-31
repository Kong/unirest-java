package BehaviorTests;

import io.github.openunirest.http.*;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class AsJsonTest extends BddTest {
    @Test
    public void canGetBinaryResponse() {
        HttpResponse<JsonNode> i = Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asJson();

        assertJson(i);
    }

    @Test
    public void canGetBinaryResponseAsync() throws ExecutionException, InterruptedException {
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

    private void assertJson(HttpResponse<JsonNode> i) {
        assertEquals("bar",i.getBody().getObject().getJSONObject("params").getJSONArray("foo").get(0));
    }
}
