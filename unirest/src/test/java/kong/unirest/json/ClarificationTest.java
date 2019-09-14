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
import org.junit.Test;
import org.json.JSONPointer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ClarificationTest {

    @Test
    public void name() {
        JSONArray s = new JSONArray("[1]");
        JSONObject o = new JSONObject();
        System.out.println("s.remove(55) = " + s.remove(0));
        System.out.println("s.length() = " + s.length());
        assertEquals(null, o.remove("foo"));
        o.put("bar", "X");
        assertEquals("X",  o.remove("bar"));
//        JSONArray array = new JSONArray("[1,2,3]");
//        TestUtil.assertException(() -> array.remove(55),
//                JSONException.class,
//                "");
    }

    @Test
    public void toStringReturnsOriginalString() {
        assertEquals("/foo/g~0h/baz", new JSONPointer("/foo/g~h/baz").toString());
    }

    @Test
    public void canGetAsURIFragmanet() {
        assertEquals("#/foo/g%7Eh/baz", new JSONPointer("/foo/g~h/baz").toURIFragment());
    }

    @Test
    public void constructorMayNotTakeNull() {
        TestUtil.assertException(() -> new JSONPointer((String) null),
                NullPointerException.class,
                "pointer cannot be null");
    }

    @Test
    public void listConstructorMayNotTakeNull() {
        TestUtil.assertException(() -> new JSONPointer((List<String>) null),
                NullPointerException.class,
                null);
    }

    @Test
    public void builder() {
        JSONPointer pointer = JSONPointer.builder()
                .append("foo")
                .append(4)
                .append("n~t")
                .append("bar/1")
                .build();

        assertEquals("/foo/4/n~0t/bar~11", pointer.toString());
    }

}
