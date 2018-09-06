package BehaviorTests;

import com.google.common.collect.ImmutableMap;
import io.github.openunirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static io.github.openunirest.request.JsonPatchOperation.*;

public class JsonPatchTest extends BddTest {

    @Test
    public void canAddThings() {
        Unirest.jsonPatch(MockServer.PATCH)
                .add("/some/path", "a value")
                .add("/another/path", 42)
                .add("/third/path", true)
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(add, "/some/path", "a value")
                .assertJsonPatch(add, "/another/path", 42)
                .assertJsonPatch(add, "/third/path", true);
    }

    @Test
    public void canRemoveThings() {
        Unirest.jsonPatch(MockServer.PATCH)
                .remove("/some/path")
                .remove("/another/path")
                .remove("/third/path")
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(remove, "/some/path", null)
                .assertJsonPatch(remove, "/another/path", null)
                .assertJsonPatch(remove, "/third/path", null);
    }

    @Test
    public void canReplaceThings() {
        Unirest.jsonPatch(MockServer.PATCH)
                .replace("/some/path", "a value")
                .replace("/another/path", 42)
                .replace("/third/path", true)
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(replace, "/some/path", "a value")
                .assertJsonPatch(replace, "/another/path", 42)
                .assertJsonPatch(replace, "/third/path", true);
    }

    @Test
    public void canTestThings() {
        Unirest.jsonPatch(MockServer.PATCH)
                .test("/some/path", "a value")
                .test("/another/path", 42)
                .test("/third/path", true)
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(test, "/some/path", "a value")
                .assertJsonPatch(test, "/another/path", 42)
                .assertJsonPatch(test, "/third/path", true);
    }

    @Test
    public void canUseJsonForValues() {
        Unirest.jsonPatch(MockServer.PATCH)
                .add("/some/path", new JSONObject().put("id", "foo"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(add, "/some/path", new JSONObject().put("id", "foo"));
    }

    @Test
    public void lotsOfDifferentWaysToMakeObjects() {
        JSONObject basicJson = new JSONObject().put("foo", "bar");

        Unirest.jsonPatch(MockServer.PATCH)
                .add("/stringArrays", new String[]{"foo, bar"})
                .add("/maps", ImmutableMap.of("foo", "bar"))
                .add("/jsonObjects", basicJson)
                .add("/jsonArrays", new JSONArray().put(basicJson))
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(add, "/stringArrays", new String[]{"foo, bar"})
                .assertJsonPatch(add, "/maps", basicJson)
                .assertJsonPatch(add, "/jsonObjects", basicJson)
                .assertJsonPatch(add, "/jsonArrays", new  JSONArray().put(basicJson))
        ;
    }

    @Test
    public void canMoveObjects() {
        Unirest.jsonPatch(MockServer.PATCH)
                .move("/old/location", "/new/location")
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(move, "/new/location", "/old/location");
    }

    @Test
    public void canCopyObjects() {
        Unirest.jsonPatch(MockServer.PATCH)
                .copy("/old/location", "/new/location")
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(copy, "/new/location", "/old/location");
    }

    @Test
    public void allTogetherNow() {
        Unirest.jsonPatch("http://localhost/thing")
                .add("/fruits/-", "Apple")
                .remove("/bugs")
                .replace("/lastname", "Flintstone")
                .test("/firstname", "Fred")
                .move("/old/location", "/new/location")
                .copy("/original/location", "/new/location")
                .asJson();
        /* resulting body
          [
              {"op":"add","path":"/fruits/-","value":"Apple"},
              {"op":"remove","path":"/bugs"},
              {"op":"replace","path":"/lastname","value":"Flintstone"},
              {"op":"test","path":"/firstname","value":"Fred"},
              {"op":"move","path":"/new/location","from":"/old/location"},
              {"op":"copy","path":"/new/location","from":"/original/location"}
          ]
         */
    }
}
