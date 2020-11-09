/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package BehaviorTests;

import com.google.common.collect.ImmutableMap;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import kong.unirest.TestUtil;

import static kong.unirest.JsonPatchOperation.*;

class JsonPatchTest extends BddTest {

    @Test
    void canAddThings() {
        Unirest.jsonPatch(MockServer.PATCH)
                .add("/some/path", "a value")
                .add("/another/path", 42)
                .add("/third/path", true)
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("application/json-patch+json")
                .assertJsonPatch(add, "/some/path", "a value")
                .assertJsonPatch(add, "/another/path", 42)
                .assertJsonPatch(add, "/third/path", true);
    }

    @Test
    void canRemoveThings() {
        Unirest.jsonPatch(MockServer.PATCH)
                .remove("/some/path")
                .remove("/another/path")
                .remove("/third/path")
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("application/json-patch+json")
                .assertJsonPatch(remove, "/some/path", null)
                .assertJsonPatch(remove, "/another/path", null)
                .assertJsonPatch(remove, "/third/path", null);
    }

    @Test
    void canReplaceThings() {
        Unirest.jsonPatch(MockServer.PATCH)
                .replace("/some/path", "a value")
                .replace("/another/path", 42)
                .replace("/third/path", true)
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("application/json-patch+json")
                .assertJsonPatch(replace, "/some/path", "a value")
                .assertJsonPatch(replace, "/another/path", 42)
                .assertJsonPatch(replace, "/third/path", true);
    }

    @Test
    void canTestThings() {
        Unirest.jsonPatch(MockServer.PATCH)
                .test("/some/path", "a value")
                .test("/another/path", 42)
                .test("/third/path", true)
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("application/json-patch+json")
                .assertJsonPatch(test, "/some/path", "a value")
                .assertJsonPatch(test, "/another/path", 42)
                .assertJsonPatch(test, "/third/path", true);
    }

    @Test
    void canUseJsonForValues() {
        Unirest.jsonPatch(MockServer.PATCH)
                .add("/some/path", new JSONObject().put("id", "foo"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(add, "/some/path", new JSONObject().put("id", "foo"));
    }

    @Test
    void lotsOfDifferentWaysToMakeObjects() {
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
                .assertJsonPatch(add, "/jsonArrays", new JSONArray().put(basicJson))
        ;
    }

    @Test
    void canMoveObjects() {
        Unirest.jsonPatch(MockServer.PATCH)
                .move("/old/location", "/new/location")
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(move, "/new/location", "/old/location");
    }

    @Test
    void canCopyObjects() {
        Unirest.jsonPatch(MockServer.PATCH)
                .copy("/old/location", "/new/location")
                .asObject(RequestCapture.class)
                .getBody()
                .assertJsonPatch(copy, "/new/location", "/old/location");
    }

    @Test
    void thatsSomeValidJson() throws Exception {
        String patch = Unirest.jsonPatch(MockServer.PATCH)
                .add("/fruits/-", "Apple")
                .remove("/bugs")
                .replace("/lastname", "Flintstone")
                .test("/firstname", "Fred")
                .move("/old/location", "/new/location")
                .copy("/original/location", "/new/location")
                .asObject(RequestCapture.class)
                .getBody()
                .body;

        String expected = TestUtil.getResource("test-json-patch.json");

        JSONAssert.assertEquals(expected, patch, true);
    }

}
