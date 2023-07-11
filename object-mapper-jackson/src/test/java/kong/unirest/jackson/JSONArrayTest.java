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


import kong.unirest.core.json.Foo;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONException;
import kong.unirest.core.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;

import static kong.unirest.jackson.JSONObjectTest.assertEqualJson;
import static org.junit.jupiter.api.Assertions.*;

class JSONArrayTest {

    @Test
    void nullForSoManyReasonsWhenZipping() {
        var array  = new JSONArray();
        assertNull(array.toJSONObject(new JSONArray(Collections.singletonList("foo"))));
        array.put(42L);
        assertNull(array.toJSONObject(null));
        assertNull(array.toJSONObject(new JSONArray()));
    }

    @Test
    void serializeNulls() {
        var obj = new JSONArray("[1,null]");
        assertEquals("[1,null]", obj.toString());
    }

    @Test
    void exeptionWhileZippingForNull() {
        var values = new JSONArray(Arrays.asList(1, "foo", false));
        var names = new JSONArray();
        names.put((String)null);

        JSONException ex = assertThrows(JSONException.class, () -> values.toJSONObject(names));
        assertEquals("JSONArray[0] not a string.", ex.getMessage());
    }

    @Test
    void zipAnArray() {
        var values = new JSONArray(Arrays.asList(1, "foo", false));
        var names = new JSONArray(Arrays.asList("one", "two", "three", "four"));
        JSONObject zipped = values.toJSONObject(names);
        assertEquals(1, zipped.get("one"));
        assertEquals("foo", zipped.get("two"));
        assertEquals(false, zipped.get("three"));
    }

    @Test
    void putObject() {
        var array  = new JSONArray();
        array.put(new Foo("fooooo"));
        array.put((Object)"abc");
        array.put((Object)new JSONObject(of("foo", "bar")));

        assertEquals("Foo{bar='fooooo'}", array.get(0).toString());
        assertEquals("abc", array.get(1));
        assertEquals("{\"foo\":\"bar\"}", array.get(2).toString());
    }

    @Test
    void simpleConvert() {
        String str = "[{\"foo\": \"bar\"}, {\"baz\": 42}]";

        var array  = new JSONArray(str);

        assertEquals(2, array.length());
        assertEquals("bar", array.getJSONObject(0).getString("foo"));
        assertEquals(42, array.getJSONObject(1).getInt("baz"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void putObjectAtElement() {
        Object nul = null;
        Object num = 42;
        Object str = "hi";
        Object bool = true;
        Object arr = new JSONArray(asList(1,2,3));
        Object obj = new JSONObject(of("f","b"));

        var array  = new JSONArray()
        .put(5, obj)
        .put(4, arr)
        .put(3, bool)
        .put(2, str)
        .put(1, num)
        .put(0, nul);

        assertEquals(nul, array.get(0));
        assertEquals(num, array.get(1));
        assertEquals(str, array.get(2));
        assertEquals(bool, array.get(3));
        assertEquals(arr, array.get(4));
        assertEquals(obj, array.get(5));
    }

    @Test
    void numbers() {
        var obj = new JSONArray();
        assertSame(obj, obj.put((Number)33));
        obj.put("nan");

        assertEquals(33, obj.getNumber(0));
        assertNotFound(() -> obj.getNumber(5));
        assertNotType(() -> obj.getNumber(1), "JSONArray[1] is not a number.");

        assertEquals(33, obj.optNumber(0));
        assertEquals(66.6d, obj.optNumber(1, 66.6d));
        assertNull(obj.optNumber(5));
    }

    @Test
    void doubles() {
        var obj = new JSONArray();
        assertSame(obj, obj.put(33.5d));
        obj.put("nan");

        assertEquals(33.5d, obj.getDouble(0), 4);
        assertNotFound(() -> obj.getDouble(5));
        assertNotType(() -> obj.getDouble(1), "JSONArray[1] is not a number.");

        assertEquals(33.5d, obj.optDouble(0), 4);
        assertEquals(66.6d, obj.optDouble(1, 66.6d), 4);
        assertEquals(Double.NaN, obj.optDouble(5), 4);
    }

    @Test
    void floats() {
        var obj = new JSONArray();
        assertSame(obj, obj.put(33.5f));
        obj.put("nan");

        assertEquals(33.5f, obj.getFloat(0), 4);
        assertNotFound(() -> obj.getFloat(5));
        assertNotType(() -> obj.getFloat(1), "JSONArray[1] is not a number.");

        assertEquals(33.5f, obj.optFloat(0), 4);
        assertEquals(66.6f, obj.optFloat(5, 66.6f), 4);
        assertEquals(Float.NaN, obj.optFloat(5), 4);
    }

    @Test
    void longs() {
        var obj = new JSONArray();
        assertSame(obj, obj.put(33L));
        obj.put("nan");

        assertEquals(33L, obj.getLong(0));
        assertNotFound(() -> obj.getLong(5));
        assertNotType(() -> obj.getLong(1), "JSONArray[1] is not a number.");

        assertEquals(33L, obj.optLong(0));
        assertEquals(66L, obj.optLong(5, 66));
        assertEquals(0L, obj.optLong(5));
    }

    @Test
    void bools() {
        var obj = new JSONArray();
        assertSame(obj, obj.put(true));
        obj.put("nan");

        assertTrue(obj.getBoolean(0));
        assertNotFound(() -> obj.getBoolean(5));
        assertNotType(() -> obj.getBoolean(1), "JSONArray[1] is not a boolean.");

        assertTrue(obj.optBoolean(0));
        assertTrue(obj.optBoolean(5, true));
        assertFalse(obj.optBoolean(5));
    }

    @Test
    void ints() {
        var obj = new JSONArray();
        assertSame(obj, obj.put(33));
        obj.put("nan");

        assertEquals(33, obj.getInt(0));
        assertNotFound(() -> obj.getInt(5));
        assertNotType(() -> obj.getInt(1), "JSONArray[1] is not a number.");

        assertEquals(33, obj.optInt(0));
        assertEquals(66, obj.optInt(5, 66));
        assertEquals(0, obj.optInt(5));
    }

    @Test
    void bigInts() {
        var obj = new JSONArray();
        assertSame(obj, obj.put(BigInteger.valueOf(33)));
        obj.put("nan");

        assertEquals(BigInteger.valueOf(33), obj.getBigInteger(0));
        assertNotFound(() -> obj.getBigInteger(5));
        assertNotType(() -> obj.getBigInteger(1), "JSONArray[1] is not a number.");
        assertEquals(BigInteger.valueOf(33), obj.optBigInteger(0, BigInteger.TEN));
        assertEquals(BigInteger.TEN, obj.optBigInteger(5, BigInteger.TEN));
    }

    @Test
    void bigDecimal() {
        BigDecimal value = BigDecimal.valueOf(33.5);
        var obj = new JSONArray();
        assertSame(obj, obj.put(value));
        obj.put("nan");

        assertEquals(value, obj.getBigDecimal(0));
        assertNotFound(() -> obj.getBigDecimal(5));
        assertNotType(() -> obj.getBigDecimal(1), "JSONArray[1] is not a number.");
        assertEquals(value, obj.optBigDecimal(0, BigDecimal.TEN));
        assertEquals(BigDecimal.TEN, obj.optBigDecimal(5, BigDecimal.TEN));
    }

    @Test
    void strings() {
        var obj = new JSONArray();
        assertSame(obj, obj.put("cheese"));
        obj.put(45);

        assertEquals("cheese", obj.getString(0));
        assertNotFound(() -> obj.getString(5));
        assertEquals("45", obj.getString(1));
        assertEquals("cheese", obj.optString(0));
        assertEquals("logs", obj.optString(5, "logs"));
        assertEquals("", obj.optString(5));
    }

    @Test
    void jsonObjects() throws Exception {
        JSONObject subObj = new JSONObject("{\"derp\": 42}");
        var obj = new JSONArray();
        assertSame(obj, obj.put(subObj));
        obj.put(45);

        assertEqualJson(subObj, obj.getJSONObject(0));
        assertNotFound(() -> obj.getJSONObject(5));
        assertNotType(() -> obj.getJSONObject(1), "JSONArray[1] is not a JSONObject.");
        assertEqualJson(subObj, obj.optJSONObject(0));
        assertNull(obj.optJSONObject(5));
    }

    @Test
    void jsonArrays() throws Exception {
        var subObj = new JSONArray("[42]");
        var obj = new JSONArray();
        assertSame(obj, obj.put(subObj));
        obj.put(45);

        assertEqualJson(subObj, obj.getJSONArray(0));
        assertNotFound(() -> obj.getJSONArray(5));
        assertNotType(() -> obj.getJSONArray(1), "JSONArray[1] is not a JSONArray.");
        assertEqualJson(subObj, obj.optJSONArray(0));
        assertNull(obj.optJSONArray(5));
    }

    @Test
    void enums() {
        var obj = new JSONArray();
        assertSame(obj, obj.put(fruit.orange));
        obj.put("nan");

        assertEquals(fruit.orange, obj.getEnum(fruit.class, 0));
        assertNotType(() -> obj.getEnum(fruit.class, 1), "JSONArray[1] is not an enum of type \"fruit\".");
        assertEquals(fruit.orange, obj.optEnum(fruit.class, 0));
        assertEquals(fruit.apple, obj.optEnum(fruit.class, 1, fruit.apple));
        assertNull(obj.optEnum(fruit.class, 5));
    }

    @Test
    void joinArray() {
        String str = "[33.5, 42, \"foo\", true, \"apple\"]";

        var array  = new JSONArray(str);

        assertEquals("33.5, 42, \"foo\", true, \"apple\"", array.join(", "));
    }

    @Test
    void toStringIt() {
        String str = "[33.5, 42, \"foo\", true, \"apple\"]";

        var array  = new JSONArray(str);

        assertEquals("[33.5,42,\"foo\",true,\"apple\"]", array.toString());
    }

    @Test
    void toStringItIndent() {
        String str = "[33.5, 42, \"foo\", true, \"apple\"]";

        var array  = new JSONArray(str);

        assertEquals("[ 33.5, 42, \"foo\", true, \"apple\" ]", array.toString(3));
    }

    @Test
    void rawGet() {
        var array  = new JSONArray(asList(
                33.457848383,
                1,
                "cheese",
                new JSONObject(of("foo", "bar")),
                new JSONArray(asList(1,2))));

        assertTrue(array.get(0) instanceof Double);
        assertTrue(array.get(1) instanceof Integer);
        assertTrue(array.get(2) instanceof String);
        assertTrue(array.get(3) instanceof JSONObject);
        assertTrue(array.get(4) instanceof JSONArray);
    }

    @Test
    void arraysOfArrays() {
        String str = "[[1,2,3],[6,7,8]]";

        var array  = new JSONArray(str);

        assertEquals(2, array.getJSONArray(0).get(1));
        assertNull(array.optJSONArray(2));
    }

    @Test
    void writer() {
        String str = "[1,2,3]";

        var array  = new JSONArray(str);

        StringWriter sw = new StringWriter();

        array.write(sw);

        assertEquals(str, sw.toString());
    }

    @Test
    void writerIndent() {
        String str = "[1,2,3]";

        var array  = new JSONArray(str);

        StringWriter sw = new StringWriter();

        array.write(sw, 3, 3);

        assertEquals("[ 1, 2, 3 ]", sw.toString());
    }

    @Test
    void remove() {
        var o = new JSONObject(of("foo","bar"));
        var array  = new JSONArray(asList(1, o));

        Object remove = array.remove(1);
        assertTrue(remove instanceof JSONObject);
        assertEquals(o, remove);
        assertEquals(1, array.length());
        assertNull(array.remove(55));
    }

    @Test
    void removeMissingIndex() {
        var array  = new JSONArray("[1,2,3]");
        assertNull(array.remove(55));
    }

    @Test
    void putSimple() {
        var array  = new JSONArray();
        array.put(1);
        array.put(Long.MAX_VALUE);
        array.put(3.5d);
        array.put(6.4f);
        array.put("howdy");
        array.put(fruit.pear);
        array.put(of("foo", 22));
        array.put(asList(1,2,3));

        assertEquals(1, array.get(0));
        assertEquals(Long.MAX_VALUE, array.get(1));
        assertEquals(3.5d, array.get(2));
        assertEquals(6.4f, ((Double)array.get(3)).floatValue(), 2);
        assertEquals("howdy", array.get(4));
        assertEquals("pear", array.get(5));
        assertTrue(new JSONObject(of("foo", 22)).similar(array.get(6)));
        assertTrue(new JSONArray(asList(1,2,3)).similar(array.get(7)));

        assertEquals("[1,9223372036854775807,3.5,6.4,\"howdy\",\"pear\",{\"foo\":22},[1,2,3]]",
                array.toString());
    }

    @Test
    void putByIndex() {
        var array  = new JSONArray();
        array.put(5, fruit.pear);
        array.put(0, 1);
        array.put(1, Long.MAX_VALUE);
        array.put(2, 3.5d);
        array.put(3, 6.4f);
        array.put(4, "howdy");
        array.put(6, of("foo", 22));
        array.put(7, asList(1,2,3));

        assertEquals(1, array.get(0));
        assertEquals(Long.MAX_VALUE, array.get(1));
        assertEquals(3.5d, array.get(2));
        assertEquals(6.4f, ((Double)array.get(3)).floatValue(), 2);
        assertEquals("howdy", array.get(4));
        assertEquals("pear", array.get(5));
        assertTrue(new JSONObject(of("foo", 22)).similar(array.get(6)));
        assertTrue(new JSONArray(asList(1,2,3)).similar(array.get(7)));

        assertEquals("[1,9223372036854775807,3.5,6.4,\"howdy\",\"pear\",{\"foo\":22},[1,2,3]]",
                array.toString());
    }

    @Test
    void query() {
        var obj = new JSONArray("[{\"a\":{\"b\": 42}}]");
        assertEquals(42, obj.query("/0/a/b"));
    }

    @Test
    void putCollection() {
        var ints = asList(1, 1, 2, 3);
        var array  = new JSONArray();
        array.put(ints);

        assertEquals(1, array.length());
        assertEquals(ints, array.getJSONArray(0).toList());
    }

    @Test
    void constructCollection() {
        var ints = asList(1, 1, 2, 3);
        var array  = new JSONArray(ints);

        assertEquals(4, array.length());
        assertEquals(ints, array.toList());
    }

    @Test
    void constructArray() {
        var ints = asList(1, 1, 2, 3);
        var array  = new JSONArray(ints.toArray());

        assertEquals(4, array.length());
        assertEquals(ints, array.toList());
    }

    @Test
    void constructArrayError() {
        JSONException ex = assertThrows(JSONException.class, ()-> new JSONArray(new Object()));
        assertEquals("JSONArray initial value should be a string or collection or array.", ex.getMessage());
    }

    @Test
    void nullMembers() {
        var array  = new JSONArray();
        array.put("foo");
        array.put((Object) null);

        assertFalse(array.isNull(0));
        assertTrue(array.isNull(1));
        assertTrue(array.isNull(2));
        assertTrue(array.isNull(33));
    }

    @Test
    void puttingSomeRandoObjectWillResultInString() {
        var array  = new JSONArray();
        array.put(new Fudge());

        assertEquals("[\"Hello World\"]", array.toString());
    }

    @Test
    @SuppressWarnings("SuspiciousMethodCalls")
    void iterateOverArray() {
        var list = asList(1, 2, 3, 4);
        var array  = new JSONArray(list);
        for(Object i : array){
            assertTrue(list.contains(i));
        }
    }

    public static void assertNotType(Executable exRunnable, String message) {
        JSONException ex = assertThrows(JSONException.class, exRunnable);
        assertEquals(message, ex.getMessage());
    }

    private void assertNotFound(Executable exRunnable) {
        assertNotType(exRunnable, "JSONArray[5] not found.");
    }

    public static class Fudge {
        public String foo = "bar";

        public String toString(){
            return "Hello World";
        }
    }

    public enum fruit {orange, apple, pear}

}





