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
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Sets.newHashSet;
import static kong.unirest.TestUtil.assertException;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class JSONObjectTest {

    @Test
    public void simpleConvert() {
        String str = "{\"foo\": {\"baz\": 42}}";

        JSONObject obj = new JSONObject(str);

        assertTrue(obj.has("foo"));
        assertEquals(1, obj.length());
        assertEquals(42, obj.getJSONObject("foo").getInt("baz"));
    }

    @Test
    public void doubles() {
        JSONObject obj = new JSONObject();
        obj.put("key", 33.5d);
        obj.put("not", "nan");

        assertEquals(33.5d, obj.getDouble("key"), 4);
        assertNotFound(() -> obj.getDouble("boo"));
        assertJSONEx(() -> obj.getDouble("not"), "JSONObject[\"not\"] is not a number.");
        isTypeAndValue(33.5, Double.class, obj.get("key"));

        assertEquals(33.5d, obj.optDouble("key"), 4);
        assertEquals(66.6d, obj.optDouble("boo", 66.6d), 4);
        assertEquals(Double.NaN, obj.optDouble("boo"), 4);
    }

    @Test
    public void floats() {
        JSONObject obj = new JSONObject();
        obj.put("key", 33.5f);
        obj.put("not", "nan");

        assertEquals(33.5f, obj.getFloat("key"), 4);
        assertNotFound(() -> obj.getFloat("boo"));
        assertJSONEx(() -> obj.getFloat("not"), "JSONObject[\"not\"] is not a number.");
        isTypeAndValue(33.5, Double.class, obj.get("key"));

        assertEquals(33.5f, obj.optFloat("key"), 4);
        assertEquals(66.6f, obj.optFloat("boo", 66.6f), 4);
        assertEquals(Float.NaN, obj.optFloat("boo"), 4);
    }

    @Test
    public void longs() {
        JSONObject obj = new JSONObject();
        obj.put("key", Long.MAX_VALUE);
        obj.put("not", "nan");

        assertEquals(Long.MAX_VALUE, obj.getLong("key"));
        assertNotFound(() -> obj.getLong("boo"));
        assertJSONEx(() -> obj.getLong("not"), "JSONObject[\"not\"] is not a number.");
        isTypeAndValue(Long.MAX_VALUE, Long.class, obj.get("key"));

        assertEquals(Long.MAX_VALUE, obj.optLong("key"));
        assertEquals(66L, obj.optLong("boo", 66));
        assertEquals(0L, obj.optLong("boo"));
    }

    @Test
    public void ints() {
        JSONObject obj = new JSONObject();
        obj.put("key", 33);
        obj.put("not", "nan");

        assertEquals(33, obj.getInt("key"));
        assertNotFound(() -> obj.getInt("boo"));
        assertJSONEx(() -> obj.getInt("not"), "JSONObject[\"not\"] is not a number.");
        isTypeAndValue(33, Integer.class, obj.get("key"));

        assertEquals(33, obj.optInt("key"));
        assertEquals(66, obj.optInt("boo", 66));
        assertEquals(0, obj.optInt("boo"));
    }

    @Test
    public void bigInts() {
        JSONObject obj = new JSONObject();
        obj.put("key", BigInteger.valueOf(33));
        obj.put("not", "nan");

        assertEquals(BigInteger.valueOf(33), obj.getBigInteger("key"));
        assertNotFound(() -> obj.getBigInteger("boo"));
        assertJSONEx(() -> obj.getBigInteger("not"), "JSONObject[\"not\"] is not a number.");
        assertEquals(BigInteger.valueOf(33), obj.optBigInteger("key", BigInteger.TEN));
        assertEquals(BigInteger.TEN, obj.optBigInteger("boo", BigInteger.TEN));
        isTypeAndValue(33, Integer.class, obj.get("key"));
    }

    @Test
    public void bigDecimal() {
        BigDecimal value = BigDecimal.valueOf(33.5);
        JSONObject obj = new JSONObject();
        obj.put("key", value);
        obj.put("not", "nan");

        assertEquals(value, obj.getBigDecimal("key"));
        assertNotFound(() -> obj.getBigDecimal("boo"));
        assertJSONEx(() -> obj.getBigDecimal("not"), "JSONObject[\"not\"] is not a number.");
        assertEquals(value, obj.optBigDecimal("key", BigDecimal.TEN));
        assertEquals(BigDecimal.TEN, obj.optBigDecimal("boo", BigDecimal.TEN));
        isTypeAndValue(33.5, Double.class, obj.get("key"));
    }

    @Test
    public void strings() {
        JSONObject obj = new JSONObject();
        obj.put("key", "cheese");
        obj.put("not", 45);

        assertEquals("cheese", obj.getString("key"));
        assertNotFound(() -> obj.getString("boo"));
        assertEquals("45", obj.getString("not"));
        assertEquals("cheese", obj.optString("key"));
        assertEquals("logs", obj.optString("boo", "logs"));
        assertEquals("", obj.optString("boo"));
        isTypeAndValue("cheese", String.class, obj.get("key"));
    }

    @Test
    public void jsonObjects() {
        JSONObject subObj = new JSONObject("{\"derp\": 42}");
        JSONObject obj = new JSONObject();
        obj.put("key", subObj);
        obj.put("not", 45);

        assertEqualJson(subObj, obj.getJSONObject("key"));
        assertNotFound(() -> obj.getJSONObject("boo"));
        assertJSONEx(() -> obj.getJSONObject("not"), "JSONObject[\"not\"] is not a JSONObject.");
        assertEqualJson(subObj, obj.optJSONObject("key"));
        assertEquals(null, obj.optJSONObject("boo"));
        assertTrue(subObj.similar(obj.get("key")));
    }

    @Test
    public void jsonArrays() {
        JSONArray subObj = new JSONArray("[42]");
        JSONObject obj = new JSONObject();
        obj.put("key", subObj);
        obj.put("not", 45);

        assertEqualJson(subObj, obj.getJSONArray("key"));
        assertNotFound(() -> obj.getJSONArray("boo"));
        assertJSONEx(() -> obj.getJSONArray("not"), "JSONObject[\"not\"] is not a JSONArray.");
        assertEqualJson(subObj, obj.optJSONArray("key"));
        assertEquals(null, obj.optJSONArray("boo"));
        assertTrue(subObj.similar(obj.get("key")));
    }

    @Test
    public void enums() {
        JSONObject obj = new JSONObject();
        obj.put("key", fruit.orange);
        obj.put("not", "nan");

        assertEquals(fruit.orange, obj.getEnum(fruit.class, "key"));
        assertJSONEx(() -> obj.getEnum(fruit.class, "not"), "JSONObject[\"not\"] is not an enum of type \"fruit\".");
        assertEquals(fruit.orange, obj.optEnum(fruit.class, "key"));
        assertEquals(fruit.apple, obj.optEnum(fruit.class, "boo", fruit.apple));
        assertEquals(null, obj.optEnum(fruit.class, "boo"));
        isTypeAndValue(obj.get("key"), String.class, "orange");
    }

    @Test
    public void toStringIt() {
        String str = "{\"foo\": 42}";

        JSONObject obj = new JSONObject(str);

        assertEquals("{\"foo\":42}", obj.toString());
    }

    @Test
    public void toStringItIndent() {
        String str = "{\"foo\": 42, \"bar\": true}";

        JSONObject obj = new JSONObject(str);

        assertEquals("{\n" +
                "  \"foo\": 42,\n" +
                "  \"bar\": true\n" +
                "}", obj.toString(3));
    }

    @Test
    public void objProperties() {
        String str = "{\"foos\": [6,7,8]}";

        JSONObject obj = new JSONObject(str);

        assertEquals(7, obj.getJSONArray("foos").get(1));
        assertEquals(7, obj.optJSONArray("foos").get(1));
        assertEquals(null, obj.optJSONArray("bars"));
    }

    @Test
    public void writer() {
        String str = "{\"foo\":42}";

        JSONObject obj = new JSONObject(str);

        StringWriter sw = new StringWriter();

        obj.write(sw);

        assertEquals(str, sw.toString());
    }

    @Test
    public void writerIndent() {
        String str = "{\"foo\": 42, \"bar\": true}";

        JSONObject obj = new JSONObject(str);

        StringWriter sw = new StringWriter();

        obj.write(sw, 3, 3);

        assertEquals("{\n" +
                "  \"foo\": 42,\n" +
                "  \"bar\": true\n" +
                "}", sw.toString());
    }

    @Test
    public void remove() {
        JSONObject obj = new JSONObject("{\"foo\": 42, \"bar\": true}");
        obj.remove("foo");

        assertEquals("{\"bar\":true}", obj.toString());
    }

    @Test
    public void removeAThingThatDoesntExist() {
        JSONObject obj = new JSONObject();
        obj.remove("foo");

        assertEquals(0, obj.length());
    }

    @Test
    public void putReplace() {
        JSONObject obj = new JSONObject("{\"bar\": 42}");
        assertEquals(42, obj.get("bar"));
        obj.put("bar", 33);
        assertEquals(33, obj.get("bar"));
        assertException(() -> obj.put(null, "hi"), NullPointerException.class, "key == null");
    }

    @Test
    public void accumulateDoesNotCreate() {
        JSONObject obj = new JSONObject();
        obj.accumulate("bar", 42);
        assertEquals(0, obj.length());
    }

    @Test
    public void accumulate() {
        JSONObject obj = new JSONObject("{\"bar\": 42}");
        obj.accumulate("bar", 33);
        assertEquals(2, obj.getJSONArray("bar").length());
        assertEquals(42, obj.getJSONArray("bar").get(0));
        assertEquals(33, obj.getJSONArray("bar").get(1));
    }

    @Test
    public void accumulateNullKey() {
        assertException(() -> new JSONObject().accumulate(null, "hi"),
                NullPointerException.class,
                "Null key.");
    }

    @Test
    public void append() {
        JSONObject obj = new JSONObject();
        obj.append("bar", 42);
        obj.append("bar", 33);
        assertEquals(2, obj.getJSONArray("bar").length());
        assertEquals(42, obj.getJSONArray("bar").get(0));
        assertEquals(33, obj.getJSONArray("bar").get(1));
    }

    @Test
    public void appendNullKey() {
        assertException(() -> new JSONObject().append(null, "hi"),
                NullPointerException.class,
                "Null key.");
    }

    @Test
    public void appendToNotAnArrary() {
        JSONObject obj = new JSONObject();
        obj.put("bar", "not");
        assertException(() -> obj.append("bar", 33),
                JSONException.class,
                "JSONObject[\"bar\"] is not a JSONArray.");
    }

    @Test
    public void increment() {
        JSONObject obj = new JSONObject();
        obj.increment("cool-beans");
        assertEquals(1, obj.get("cool-beans"));
        obj.increment("cool-beans");
        obj.increment("cool-beans");
        obj.increment("cool-beans");
        assertEquals(4, obj.get("cool-beans"));
    }

    @Test
    public void incrementDouble() {
        JSONObject obj = new JSONObject();
        obj.put("cool-beans", 1.5);
        obj.increment("cool-beans");
        assertEquals(2.5, obj.get("cool-beans"));
    }


    @Test
    public void putOnce() {
        JSONObject obj = new JSONObject();
        obj.putOnce("foo", "bar");
        assertJSONEx(() -> obj.putOnce("foo", "baz"), "Duplicate key \"foo\"");
        assertEquals("bar", obj.getString("foo"));
    }

    @Test
    public void optPut() {
        JSONObject obj = new JSONObject();
        obj.putOpt("foo", "bar");
        obj.putOpt(null, "bar");
        obj.putOpt("foo", null);
        assertEquals("bar", obj.get("foo"));
        obj.putOpt("foo", "qux");
        assertEquals("qux", obj.get("foo"));
    }

    @Test
    public void keySet() {
        JSONObject obj = new JSONObject();
        obj.put("one", "a");
        obj.put("two", "b");
        Set<String> exp = newHashSet("one", "two");
        assertEquals(exp, obj.keySet());
        assertEquals(exp, newHashSet(obj.keys()));
    }

    @Test
    public void similar() {
        JSONObject obj1 = new JSONObject("{\"foo\":42}");
        JSONObject obj2 = new JSONObject("{\"foo\":42}");
        assertTrue(obj1.similar(obj2));
        obj1.put("foo", -9);
        assertFalse(obj1.similar(obj2));
    }

    @Test
    public void query() {
        JSONObject obj = new JSONObject("{\"a\":{\"b\": 42}}");
        assertEquals(42, obj.query("/a/b"));
    }

    @Test
    public void maps() {
        JSONObject obj = new JSONObject("{\"foo\": {\"bar\": 42}, \"baz\": 55}");

        Map map = obj.toMap();
        assertEquals(55.0, map.get("baz"));
        JSONObject sub = (JSONObject)obj.get("foo");
        assertEquals(42, sub.get("bar"));
    }

    @Test
    public void names() {
        JSONObject obj = new JSONObject(of("foo", 1, "bar", 2, "baz", 3));
        JSONArray names = obj.names();
        assertEquals(
                newHashSet("foo", "bar", "baz"),
                newHashSet(names.toList())
        );
    }

    private void assertNotFound(TestUtil.ExRunnable exRunnable) {
        assertJSONEx(exRunnable, "JSONObject[\"boo\"] not found.");
    }

    public static void assertJSONEx(TestUtil.ExRunnable exRunnable, String message) {
        assertException(exRunnable, JSONException.class, message);
    }

    public static void assertEqualJson(Object subObj, Object value) {
        JSONAssert.assertEquals(subObj.toString(), value.toString(), true);
    }

    public static void isTypeAndValue(Object o, Class<?> type, Object value){
        assertEquals(o, value);
        assertTrue(type.isInstance(o));
    }

    public enum fruit {orange, apple, pear;}
}
