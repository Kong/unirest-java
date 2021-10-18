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

package kong.unirest.jackson;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import kong.unirest.json.JSONObject;
import kong.unirest.json.JSONPointer;
import kong.unirest.json.JSONPointerException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.jupiter.api.Assertions.*;

class JSONPointerTest {
    // https://tools.ietf.org/html/rfc6901
    private static final String RFC_TEST = getResource("JSON_POINTER_REF.json");
    private final JSONObject obj = new JSONObject(RFC_TEST);

    public static String getResource(String resourceName){
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void nullQuery() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> obj.query((String)null));
        assertEquals("pointer cannot be null", ex.getMessage());
    }

    @Test
    void invalidPathQuery() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> obj.query("foo"));
        assertEquals("a JSON pointer should start with '/' or '#/'", ex.getMessage());
    }

    @Test
    void invalidPathQuery_downpath() {
        JSONPointerException ex = assertThrows(JSONPointerException.class, () -> obj.query("/shwoop/dedoop"));
        assertEquals("Path Segment Missing: shwoop", ex.getMessage());
    }

    @Test
    void arrayPartThatDoesNotExist() {
        JSONPointerException ex = assertThrows(JSONPointerException.class, () -> obj.query("/foo/5"));
        assertEquals("index 5 is out of bounds - the array has 2 elements", ex.getMessage());
    }

    @Test
    void referenceAnArrayAsAThing() {
        JSONPointerException ex = assertThrows(JSONPointerException.class, () -> obj.query("/foo/bar"));
        assertEquals("bar is not an array index", ex.getMessage());
    }

    @Test
    @SuppressWarnings("RedundantCast")
    void constructorMayNotTakeNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new JSONPointer((String) null));
        assertEquals("pointer cannot be null", ex.getMessage());
    }

    @Test
    void toStringReturnsOriginalString() {
        assertEquals("/foo/g~0h/baz", new JSONPointer("/foo/g~h/baz").toString());
        assertEquals("/foo/g~0h/baz", JSONPointer.compile("/foo/g~h/baz").toString());
    }

    @Test
    void canGetAsURIFragmanet() {
        assertEquals("#/foo/g%7Eh/baz", new JSONPointer("/foo/g~h/baz").toURIFragment());
    }

    @Test
    void elementInObjectDoesNotExist() {
        assertNull(obj.query("/derpa"));
    }

    @Test
    void testRef_all() throws Exception {
        assertQueryJson(RFC_TEST, "");
    }

    @Test
    void testRef_Array() throws Exception {
        assertQueryJson("[\"bar\", \"baz\"]", "/foo");
    }

    @Test
    void testRef_ArrayZero() {
        assertEquals("bar", obj.query("/foo/0").toString());
    }

    @Test
    void testRef_Slash() {
        assertEquals(0, obj.query("/"));
    }

    @Test
    void testRef_ab() {
        assertEquals(1, obj.query("/a~1b"));
    }

    @Test
    void testRef_cd() {
        assertEquals(2, obj.query("/c%d"));
    }

    @Test
    void testRef_ef() {
        assertEquals(3, obj.query("/e^f"));
    }

    @Test
    void testRef_gh() {
        assertEquals(4, obj.query("/g|h"));
    }

    @Test
    void testRef_ij() {
        assertEquals(5, obj.query("/i\\j"));
    }

    @Test
    void testRef_kl() {
        assertEquals(6, obj.query("/k\"l"));
    }

    @Test
    void testRef_space() {
        assertEquals(7, obj.query("/ "));
    }

    @Test
    void testRef_mn() {
        assertEquals(8, obj.query("/m~0n"));
    }

    @Test
    void letsGoDeep() {
        assertEquals(true, obj.query("/cucu/0/banana/pants"));
    }

    @Test
    void builder(){
        JSONPointer pointer = JSONPointer
                .builder()
                .append("foo")
                .append(4)
                .append("n~t")
                .append("bar/1")
                .build();

        assertEquals(new JSONPointer("/foo/4/n~0t/bar/1").toString(),
                pointer.toString());
    }

    private void assertQueryJson(String s, String s2) throws Exception {
        JSONAssert.assertEquals(s, obj.query(s2).toString(), true);
    }

}
