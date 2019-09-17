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

import BehaviorTests.Foo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import kong.unirest.TestUtil;
import org.json.JSONException;
import org.junit.Test;
import org.json.JSONPointer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableMap.of;
import static org.junit.Assert.*;

public class ClarificationTest {

    public static Set<String> sigsArray(){
        return Halp.getPublicMinus(JSONArray.class);
    }

    @Test
    public void zipAnArray() {
        JSONArray values = new JSONArray(Arrays.asList(1, "foo", false));
        JSONArray names = new JSONArray(Arrays.asList("one", "two", "three", "four"));
        JSONObject zipped = values.toJSONObject(names);
        assertEquals(1, zipped.get("one"));
        assertEquals("foo", zipped.get("two"));
        assertEquals(false, zipped.get("three"));

        TestUtil.assertException(() ->  values.toJSONObject(new JSONArray(Lists.newArrayList((String)null))),
                JSONException.class,
                "JSONArray[0] not a string.");

    }

    @Test
    public void nullForSoManyReasonsWhenZipping() {
        JSONArray array = new JSONArray();
        assertNull(null, array.toJSONObject(new JSONArray(Arrays.asList("foo"))));
        array.put(42L);
        assertNull(null, array.toJSONObject(null));
        assertNull(null, array.toJSONObject(new JSONArray()));
    }

    @Test
    public void putObject() {
        JSONArray array  = new JSONArray();
        array.put(new Foo("fooooo"));
        array.put((Object)"abc");
        array.put((Object)new JSONObject(of("foo", "bar")));

        assertEquals("Foo{bar=fooooo}", array.get(0).toString());
        assertEquals("abc", array.get(1));
        assertEquals("{\"foo\":\"bar\"}", array.get(2).toString());
    }

    @Test
    public void name() {
        JSONArray s = new JSONArray("[1]");
        JSONObject o = new JSONObject();
        System.out.println("s.remove(55) = " + s.remove(0));
        System.out.println("s.length() = " + s.length());
        assertEquals(null, o.remove("foo"));
        assertEquals(null, s.remove(44));
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
