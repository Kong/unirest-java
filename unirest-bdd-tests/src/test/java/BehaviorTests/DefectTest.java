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

import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefectTest extends BddTest {

    @Test
    void hashOnLinksDoNotMessUpUri() {
        Unirest.get(MockServer.GET + "?a=1&b=2#some_location")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("a", "1")
                .assertParam("b", "2");
    }

    @Test
    void nullAndObjectValuesInMap() {
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("foo", null);
        queryParams.put("baz", "qux");

        Unirest.get(MockServer.GET)
                .queryString(queryParams)
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "")
                .assertParam("baz", "qux")
                .assertQueryString("foo&baz=qux");
    }

    @Test @Disabled
    void trySomeoneElsesGZip() throws Exception {
        JSONObject body = Unirest.get("http://httpbin.org/gzip")
                .asJsonAsync().get().getBody().getObject();

        Assertions.assertEquals(true, body.getBoolean("gzipped"));
        Assertions.assertEquals("gzip", body.getJSONObject("headers").getString("Accept-Encoding"));
    }
}
