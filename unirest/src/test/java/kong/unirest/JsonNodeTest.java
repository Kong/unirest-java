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

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonNodeTest {

    @Test
    public void canParseARegularObject() {
        String json = "{\"foo\":\"bar\"}";
        JsonNode node = new JsonNode(json);
        assertEquals(false, node.isArray());
        assertEquals("bar", node.getObject().getString("foo"));
        assertEquals("bar", node.getArray().getJSONObject(0).getString("foo"));
        assertEquals(json, node.toString());
    }

    @Test
    public void canParseArrayObject() {
        String json = "[{\"foo\":\"bar\"}]";
        JsonNode node = new JsonNode(json);
        assertEquals(true, node.isArray());
        assertEquals("bar", node.getArray().getJSONObject(0).getString("foo"));
        assertEquals(null, node.getObject());
        assertEquals(json, node.toString());
    }

    @Test
    public void nullAndEmptyObjectsResultInEmptyJson() {
        assertEquals("{}", new JsonNode("").toString());
        assertEquals("{}", new JsonNode(null).toString());
    }
}