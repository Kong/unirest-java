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

package kong.unirest;

import BehaviorTests.RequestCapture;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

class JacksonObjectMapperTest {

    private final JacksonObjectMapper om = new JacksonObjectMapper();

    @Test
    void jsonPatch() throws JSONException {
        JsonPatch patch = new JsonPatch();
        patch.add("/foo", "bar");
        patch.add("/baz", "qux");

        String expectStr = patch.toString();
        String actualStr = om.writeValue(patch);

        JSONAssert.assertEquals(expectStr,
                actualStr,
                true);
    }

    @Test
    void jsonPatchInRequestCapture() throws JSONException {
        JsonPatch patch = new JsonPatch();
        patch.add("/foo", "bar");
        patch.add("/baz", "qux");

        RequestCapture rc = new RequestCapture();
        rc.setPatch(patch);

        String actualStr = om.writeValue(rc);
        System.out.println("actualStr = " + actualStr);
        JSONAssert.assertEquals("{}",
                actualStr,
                false);
    }
}