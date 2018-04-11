package BehaviorTests;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.JsonNode;
import io.github.openunirest.http.Unirest;
import org.junit.Test;
import util.MockCallback;
import util.TestUtil;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AsJsonTest extends BddTest {
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

        assertEquals(400, response.getStatus());
        assertNull(response.getBody());
        assertEquals("You did something bad", TestUtil.toString(response.getRawBody()));
        assertEquals("org.json.JSONException: A JSONArray text must start with '[' at 1 [character 2 line 1]",
                response.getParsingError().get().getMessage());
    }

    @Test
    public void failureToReturnValidJsonWillResultInAnEmptyNodeAsync() {
        Unirest.get(MockServer.INVALID_REQUEST)
                .asJsonAsync(new MockCallback<>(this, response -> {
                    assertEquals(400, response.getStatus());
                    assertNull(response.getBody());
                    assertEquals("You did something bad", TestUtil.toString(response.getRawBody()));
                    assertEquals("org.json.JSONException: A JSONArray text must start with '[' at 1 [character 2 line 1]",
                            response.getParsingError().get().getMessage());

                }));

        assertAsync();
    }

    private void assertJson(HttpResponse<JsonNode> i) {
        assertEquals("bar",i.getBody().getObject().getJSONObject("params").getJSONArray("foo").get(0));
    }
}
