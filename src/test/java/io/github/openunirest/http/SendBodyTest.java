package io.github.openunirest.http;

import BehaviorTests.*;
import util.MockCallback;
import io.github.openunirest.http.exceptions.UnirestConfigException;
import io.github.openunirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static util.TestUtil.assertException;

public class SendBodyTest extends BddTest {
    @Test
    public void testPostRawBody() {
        String sourceString = "'\"@こんにちは-test-123-" + Math.random();
        byte[] sentBytes = sourceString.getBytes();

        Unirest.post(MockServer.POST)
                .body(sentBytes)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody(sourceString);
    }

    @Test
    public void testAsyncCustomContentType() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{\"hello\":\"world\"}")
                .asJsonAsync(new MockCallback<>(this, r -> parse(r)
                        .asserBody("{\"hello\":\"world\"}")
                        .assertHeader("Content-Type", "application/json"))
                );

        assertAsync();
    }


    @Test
    public void postAPojoObjectUsingTheMapper() {
        GetResponse postResponseMock = new GetResponse();
        postResponseMock.setUrl(MockServer.POST);

        Unirest.post(postResponseMock.getUrl())
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(postResponseMock)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("{\"url\":\"http://localhost:4567/post\"}");
    }

    @Test
    public void testDeleteBody() throws JSONException, UnirestException {
        String body = "{\"jsonString\":{\"members\":\"members1\"}}";
        Unirest.delete(MockServer.DELETE)
                .body(body)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody(body);
    }

    @Test
    public void postBodyAsJson() throws JSONException, UnirestException {
        JSONObject body = new JSONObject();
        body.put("krusty","krab");

        Unirest.post(MockServer.POST)
                .body(body)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("{\"krusty\":\"krab\"}");
    }

    @Test
    public void postBodyAsJsonArray() throws JSONException, UnirestException {
        JSONArray body = new JSONArray();
        body.put(0, "krusty");
        body.put(1, "krab");

        Unirest.post(MockServer.POST)
                .body(body)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("[\"krusty\",\"krab\"]");
    }

    @Test
    public void postBodyAsJsonNode() throws JSONException, UnirestException {
        JsonNode body = new JsonNode("{\"krusty\":\"krab\"}");

        Unirest.post(MockServer.POST)
                .body(body)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("{\"krusty\":\"krab\"}");
    }

    @Test
    public void cantPostObjectWithoutObjectMapper(){
        Unirest.setObjectMapper(null);

        assertException(() -> Unirest.post(MockServer.POST).body(new Foo("die")),
                UnirestConfigException.class,
                "Serialization Impossible. Can't find an ObjectMapper implementation.");
    }
}
