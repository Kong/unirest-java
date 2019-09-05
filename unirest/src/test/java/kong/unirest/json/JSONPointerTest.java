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

package kong.unirest.json;

import kong.unirest.TestUtil;
import org.json.JSONArray;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static org.junit.Assert.*;

public class JSONPointerTest {
    // https://tools.ietf.org/html/rfc6901
    private static String RFC_TEST = TestUtil.getResource("JSON_POINTER_REF.json");
    private JSONObject obj = new JSONObject(RFC_TEST);

    @Test
    public void nullQuery() {
        TestUtil.assertException(() -> obj.query((String)null),
                NullPointerException.class,
                "pointer cannot be null");
    }

    @Test
    public void invalidPathQuery() {
        TestUtil.assertException(() -> obj.query("foo"),
                IllegalArgumentException.class,
                "a JSON pointer should start with '/' or '#/'");
    }

    @Test
    public void arrayPartThatDoesNotExist() {
        TestUtil.assertException(() -> obj.query("/foo/5"),
                JSONPointerException.class,
                "index 5 is out of bounds - the array has 2 elements");
    }

    @Test
    public void referenceAnArrayAsAThing() {
        TestUtil.assertException(() -> obj.query("/foo/bar"),
                JSONPointerException.class,
                "bar is not an array index");
    }

    @Test
    public void constructorMayNotTakeNull() {
        TestUtil.assertException(() -> new JSONPointer((String) null),
                NullPointerException.class,
                "pointer cannot be null");
    }

    @Test
    public void toStringReturnsOriginalString() {
        assertEquals("/foo/g~0h/baz", new JSONPointer("/foo/g~h/baz").toString());
        assertEquals("/foo/g~0h/baz", JSONPointer.compile("/foo/g~h/baz").toString());
    }

    @Test
    public void canGetAsURIFragmanet() {
        assertEquals("#/foo/g%7Eh/baz", new JSONPointer("/foo/g~h/baz").toURIFragment());
    }

    @Test
    public void elementInObjectDoesNotExist() {
        assertNull(obj.query("/derpa"));
    }

    @Test
    public void testRef_all() {
        assertQueryJson(RFC_TEST, "");
    }

    @Test
    public void testRef_Array(){
        assertQueryJson("[\"bar\", \"baz\"]", "/foo");
    }

    @Test
    public void testRef_ArrayZero() {
        assertEquals("bar", obj.query("/foo/0").toString());
    }

    @Test
    public void testRef_Slash() {
        assertEquals(0, obj.query("/"));
    }

    @Test
    public void testRef_ab() {
        assertEquals(1, obj.query("/a~1b"));
    }

    @Test
    public void testRef_cd() {
        assertEquals(2, obj.query("/c%d"));
    }

    @Test
    public void testRef_ef() {
        assertEquals(3, obj.query("/e^f"));
    }

    @Test
    public void testRef_gh() {
        assertEquals(4, obj.query("/g|h"));
    }

    @Test
    public void testRef_ij() {
        assertEquals(5, obj.query("/i\\j"));
    }

    @Test
    public void testRef_kl() {
        assertEquals(6, obj.query("/k\"l"));
    }

    @Test
    public void testRef_space() {
        assertEquals(7, obj.query("/ "));
    }

    @Test
    public void testRef_mn() {
        assertEquals(8, obj.query("/m~0n"));
    }

    @Test
    public void letsGoDeep() {
        assertEquals(true, obj.query("/cucu/0/banana/pants"));
    }

    @Test
    public void builder(){
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

    private void assertQueryJson(String s, String s2) {
        JSONAssert.assertEquals(s, obj.query(s2).toString(), true);
    }

}
