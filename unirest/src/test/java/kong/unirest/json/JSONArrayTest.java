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
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static kong.unirest.TestUtil.assertException;
import static kong.unirest.json.JSONObjectTest.assertEqualJson;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class JSONArrayTest {


    @Test
    public void simpleConvert() {
        String str = "[{\"foo\": \"bar\"}, {\"baz\": 42}]";

        JSONArray array = new JSONArray(str);

        assertEquals(2, array.length());
        assertEquals("bar", array.getJSONObject(0).getString("foo"));
        assertEquals(42, array.getJSONObject(1).getInt("baz"));
    }

    @Test
    public void doubles() {
        JSONArray obj = new JSONArray();
        obj.put(33.5d);
        obj.put("nan");

        assertEquals(33.5d, obj.getDouble(0), 4);
        assertNotFound(() -> obj.getDouble(5));
        assertNotType(() -> obj.getDouble(1), "JSONArray[1] is not a number.");

        assertEquals(33.5d, obj.optDouble(0), 4);
        assertEquals(66.6d, obj.optDouble(1, 66.6d), 4);
        assertEquals(Double.NaN, obj.optDouble(5), 4);
    }

    @Test
    public void floats() {
        JSONArray obj = new JSONArray();
        obj.put(33.5f);
        obj.put("nan");

        assertEquals(33.5f, obj.getFloat(0), 4);
        assertNotFound(() -> obj.getFloat(5));
        assertNotType(() -> obj.getFloat(1), "JSONArray[1] is not a number.");

        assertEquals(33.5f, obj.optFloat(0), 4);
        assertEquals(66.6f, obj.optFloat(5, 66.6f), 4);
        assertEquals(Float.NaN, obj.optFloat(5), 4);
    }

    @Test
    public void longs() {
        JSONArray obj = new JSONArray();
        obj.put(33L);
        obj.put("nan");

        assertEquals(33L, obj.getLong(0));
        assertNotFound(() -> obj.getLong(5));
        assertNotType(() -> obj.getLong(1), "JSONArray[1] is not a number.");

        assertEquals(33L, obj.optLong(0));
        assertEquals(66L, obj.optLong(5, 66));
        assertEquals(0L, obj.optLong(5));
    }

    @Test
    public void ints() {
        JSONArray obj = new JSONArray();
        obj.put(33);
        obj.put("nan");

        assertEquals(33, obj.getInt(0));
        assertNotFound(() -> obj.getInt(5));
        assertNotType(() -> obj.getInt(1), "JSONArray[1] is not a number.");

        assertEquals(33, obj.optInt(0));
        assertEquals(66, obj.optInt(5, 66));
        assertEquals(0, obj.optInt(5));
    }

    @Test
    public void bigInts() {
        JSONArray obj = new JSONArray();
        obj.put(BigInteger.valueOf(33));
        obj.put("nan");

        assertEquals(BigInteger.valueOf(33), obj.getBigInteger(0));
        assertNotFound(() -> obj.getBigInteger(5));
        assertNotType(() -> obj.getBigInteger(1), "JSONArray[1] is not a number.");
        assertEquals(BigInteger.valueOf(33), obj.optBigInteger(0, BigInteger.TEN));
        assertEquals(BigInteger.TEN, obj.optBigInteger(5, BigInteger.TEN));
    }

    @Test
    public void bigDecimal() {
        BigDecimal value = BigDecimal.valueOf(33.5);
        JSONArray obj = new JSONArray();
        obj.put(value);
        obj.put("nan");

        assertEquals(value, obj.getBigDecimal(0));
        assertNotFound(() -> obj.getBigDecimal(5));
        assertNotType(() -> obj.getBigDecimal(1), "JSONArray[1] is not a number.");
        assertEquals(value, obj.optBigDecimal(0, BigDecimal.TEN));
        assertEquals(BigDecimal.TEN, obj.optBigDecimal(5, BigDecimal.TEN));
    }

    @Test
    public void strings() {
        JSONArray obj = new JSONArray();
        obj.put("cheese");
        obj.put(45);

        assertEquals("cheese", obj.getString(0));
        assertNotFound(() -> obj.getString(5));
        assertEquals(obj.getString(1), "45");
        assertEquals("cheese", obj.optString(0));
        assertEquals("logs", obj.optString(5, "logs"));
        assertEquals("", obj.optString(5));
    }

    @Test
    public void jsonObjects() {
        JSONObject subObj = new JSONObject("{\"derp\": 42}");
        JSONArray obj = new JSONArray();
        obj.put(subObj);
        obj.put(45);

        assertEqualJson(subObj, obj.getJSONObject(0));
        assertNotFound(() -> obj.getJSONObject(5));
        assertNotType(() -> obj.getJSONObject(1), "JSONArray[1] is not a JSONObject.");
        assertEqualJson(subObj, obj.optJSONObject(0));
        assertEquals(null, obj.optJSONObject(5));
    }

    @Test
    public void jsonArrays() {
        JSONArray subObj = new JSONArray("[42]");
        JSONArray obj = new JSONArray();
        obj.put(subObj);
        obj.put(45);

        assertEqualJson(subObj, obj.getJSONArray(0));
        assertNotFound(() -> obj.getJSONArray(5));
        assertNotType(() -> obj.getJSONArray(1), "JSONArray[1] is not a JSONArray.");
        assertEqualJson(subObj, obj.optJSONArray(0));
        assertEquals(null, obj.optJSONArray(5));
    }

    @Test
    public void enums() {
        JSONArray obj = new JSONArray();
        obj.put(fruit.orange);
        obj.put("nan");

        assertEquals(fruit.orange, obj.getEnum(fruit.class, 0));
        assertNotType(() -> obj.getEnum(fruit.class, 1), "JSONArray[1] is not an enum of type \"fruit\".");
        assertEquals(fruit.orange, obj.optEnum(fruit.class, 0));
        assertEquals(fruit.apple, obj.optEnum(fruit.class, 1, fruit.apple));
        assertEquals(null, obj.optEnum(fruit.class, 5));
    }

    @Test
    public void joinArray() {
        String str = "[33.5, 42, \"foo\", true, apple]";

        JSONArray array = new JSONArray(str);

        assertEquals("33.5, 42, \"foo\", true, \"apple\"", array.join(", "));
    }

    @Test
    public void toStringIt() {
        String str = "[33.5, 42, \"foo\", true, apple]";

        JSONArray array = new JSONArray(str);

        assertEquals("[33.5,42,\"foo\",true,\"apple\"]", array.toString());
    }

    @Test
    public void toStringItIndent() {
        String str = "[33.5, 42, \"foo\", true, apple]";

        JSONArray array = new JSONArray(str);

        assertEquals("[\n" +
                "  33.5,\n" +
                "  42,\n" +
                "  \"foo\",\n" +
                "  true,\n" +
                "  \"apple\"\n" +
                "]", array.toString(3));
    }

    @Test
    public void rawGet() {
        JSONArray array = new JSONArray(asList(
                33.457848383,
                1,
                "cheese",
                new JSONObject(of("foo", "bar")),
                new JSONArray(asList(1,2))));

        assertThat(array.get(0), instanceOf(Double.class));
        assertThat(array.get(1), instanceOf(Integer.class));
        assertThat(array.get(2), instanceOf(String.class));
        assertThat(array.get(3), instanceOf(JSONObject.class));
        assertThat(array.get(4), instanceOf(JSONArray.class));
    }

    @Test
    public void arraysOfArrays() {
        String str = "[[1,2,3],[6,7,8]]";

        JSONArray array = new JSONArray(str);

        assertEquals(2, array.getJSONArray(0).get(1));
        assertNull(array.optJSONArray(2));
    }

    @Test
    public void writer() {
        String str = "[1,2,3]";

        JSONArray array = new JSONArray(str);

        StringWriter sw = new StringWriter();

        array.write(sw);

        assertEquals(str, sw.toString());
    }

    @Test
    public void writerIndent() {
        String str = "[1,2,3]";

        JSONArray array = new JSONArray(str);

        StringWriter sw = new StringWriter();

        array.write(sw, 3, 3);

        assertEquals("[\n" +
                "  1,\n" +
                "  2,\n" +
                "  3\n" +
                "]", sw.toString());
    }

    @Test
    public void remove() {
        JSONArray array = new JSONArray("[1,2,3]");

        assertEqualJson(2, array.remove(1));
        assertEquals("[1,3]", array.toString());
    }

    @Test
    public void removeMissingIndex() {
        JSONArray array = new JSONArray("[1,2,3]");
        assertNull(array.remove(55));
    }

    @Test
    public void putSimple() {
        JSONArray array = new JSONArray();
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
    public void putByIndex() {
        JSONArray array = new JSONArray();
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
    public void query() {
        JSONArray obj = new JSONArray("[{\"a\":{\"b\": 42}}]");
        assertEquals(42, obj.query("/0/a/b"));
    }

    @Test
    public void putCollection() {
        List ints = asList(1, 1, 2, 3);
        JSONArray array = new JSONArray();
        array.put(ints);

        assertEquals(1, array.length());
        assertEquals(ints, array.getJSONArray(0).toList());
    }

    @Test
    public void constructCollection() {
        List<Integer> ints = asList(1, 1, 2, 3);
        JSONArray array = new JSONArray(ints);

        assertEquals(4, array.length());
        assertEquals(ints, array.toList());
    }

    @Test
    public void constructArray() {
        List<Integer> ints = asList(1, 1, 2, 3);
        JSONArray array = new JSONArray(ints.toArray());

        assertEquals(4, array.length());
        assertEquals(ints, array.toList());
    }

    @Test
    public void constructArrayError() {
        TestUtil.assertException(()-> new JSONArray(new Object()),
                JSONException.class,
                "JSONArray initial value should be a string or collection or array.");
    }

    @Test
    public void nullMembers() {
        JSONArray array = new JSONArray();
        array.put("foo");
        array.put((Object) null);

        assertFalse(array.isNull(0));
        assertTrue(array.isNull(1));
        assertTrue(array.isNull(2));
        assertTrue(array.isNull(33));
    }

    @Test
    public void puttingSomeRandoObjectWillResultInString() {
        JSONArray array = new JSONArray();
        array.put(new Fudge());

        assertEquals("[\"Hello World\"]", array.toString());
    }


    @Test @Ignore // will do with JSONObject Test
    public void maps() {
        JSONArray array = new JSONArray();
        array.put(of("foo", "bar"));

        assertEquals("bar", array.getJSONObject(0).get("foo"));
    }

    @Test
    public void iterateOverArray() {
        List<Integer> list = asList(1, 2, 3, 4);
        JSONArray array = new JSONArray(list);
        for(Object i : array){
            assertTrue(list.contains(i));
        }
    }

    public static void assertNotType(TestUtil.ExRunnable exRunnable, String message) {
        assertException(exRunnable, JSONException.class, message);
    }

    private void assertNotFound(TestUtil.ExRunnable exRunnable) {
        assertNotType(exRunnable, "JSONArray[5] not found.");
    }

    public class Fudge {
        public String foo = "bar";

        public String toString(){
            return "Hello World";
        }
    }

    public enum fruit {orange, apple, pear;}

}





