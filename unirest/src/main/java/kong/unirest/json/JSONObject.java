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

import com.google.gson.*;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * https://json.org/
 * https://tools.ietf.org/html/rfc7159#section-4
 * represents a JSON Object
 */
public class JSONObject extends JSONElement {

    public static final Object NULL = new NullObject();
    private transient final JsonObject obj;

    /**
     * https://tools.ietf.org/html/rfc7159#section-4
     * @param string a json object string
     */
    public JSONObject(String string) {
        this(fromJson(string, JsonObject.class));
    }

    /**
     * construct using a map
     * @param map a map representing the elements of a JSON Object
     */
    public JSONObject(Map<String, Object> map) {
        this(toTree(map));
    }

    /**
     * construct using an object. The Properties of the JSONObject
     * will be taken from getters and properties of the object
     * @param object the object to turn into a JSONObject
     */
    public JSONObject(Object object) {
        this(toTree(object));
    }

    /**
     * an empty JSON object
     */
    public JSONObject() {
        this(new JsonObject());
    }


    JSONObject(JsonElement jsonElement) {
        super(jsonElement);
        this.obj = jsonElement.getAsJsonObject();
    }

    /**
     * quite escape a string
     * @param s a string
     * @return a quoted string
     */
    public static String quote(String s) {
        return new Gson().toJson(s);
    }

    /**
     * quite escape a string
     * @param s a string
     * @param writer a writer to write the string to
     * @return the same writer
     * @throws IOException if some IO thing goes wrong
     */
    public static Writer quote(String s, Writer writer) throws IOException {
        writer.write(quote(s));
        return writer;
    }

    /**
     * convert a primitive JSON type in a string (bool, number, null) to its primitive type
     * all decimal types will become doubles
     * @param str a string
     * @return a object
     */
    public static Object stringToValue(String str) {
        if(str.contentEquals("null")){
            return NULL;
        } else if (str.equalsIgnoreCase("true")){
            return true;
        }else if (str.equalsIgnoreCase("false")) {
            return false;
        }
        if(str.contains(".")){
            return Double.valueOf(str);
        } else {
            return Integer.valueOf(str);
        }
    }

    /**
     * Convert an object to a object that can be added to a JSONElement
     *    If the object is null return the NULL object
     *    If the object is primitive return the original object
     *    If the object is a map convert it to a JSONObject
     *    If the object is a Collection or array return a JSONArray
     *    If the object is anything else return a empty JSON Object
     * @param obj the object
     * @return another object suitable for use as JSON
     */
    public static Object wrap(Object obj) {
        if(obj == null || obj.equals(NULL)){
            return NULL;
        }
        if(isPrimitive(obj)){
            return obj;
        }
        if(obj instanceof Map){
            return new JSONObject((Map)obj);
        }
        if(obj instanceof Collection){
            return new JSONArray((Collection)obj);
        }
        if(obj.getClass().isArray()){
            return wrapArray(obj);
        }
        return new JSONObject();
    }

    private static JSONArray wrapArray(Object obj) {
        JSONArray array = new JSONArray();
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i ++) {
            Object arrayElement = Array.get(obj, i);
            array.put(arrayElement);
        }
        return array;
    }

    /**
     * convert a primitive number to a string
     * if the double can be converted to a whole number the decimal will be dropped
     * @param d a double
     * @return a string representation of the double
     */
    public static String doubleToString(double d) {
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            return Integer.toString((int)d);
        }
        return Double.toString(d);
    }

    /**
     * Convert a number to a string
     * @param number the number to convert
     * @return a string representation of that number
     * @throws JSONException if something goes wrong
     */
    public static String numberToString(Number number) throws JSONException {
        return String.valueOf(number);
    }

    /**
     * Converts an object to a JSON String
     * @param o any object
     * @return a json string
     * @throws JSONException if something goes wrong
     */
    public static String valueToString(Object o) throws JSONException {
        if(o == null){
            return "null";
        }
        if(o instanceof JSONString){
            return ((JSONString)o).toJSONString();
        }
        if(o instanceof JSONElement){
            return o.toString();
        }
        return new Gson().toJson(o);
    }

    /**
     * get all of the keys of a JSONObject
     * @param jsonObject a JSONObject
     * @return a String[] of the objects keys
     */
    public static String[] getNames(JSONObject jsonObject) {
        if(jsonObject == null || jsonObject.isEmpty()){
            return null;
        }
        List<String> list = jsonObject.names().toList();
        return list.toArray(new String[list.size()]);
    }

    /**
     * get all of the keys of a JSONObject or a empty array if not an JSONObject
     * @param o a Object
     * @return a String[] of the objects keys
     */
    public static String[] getNames(Object o) {
        if(o instanceof JSONObject){
            return getNames((JSONObject)o);
        }
        return new String[]{};
    }

    JsonElement asElement() {
        return obj;
    }

    /**
     * @return the object as a JSON string with no formatting
     */
    @Override
    public String toString() {
        return toJson(obj);
    }

    /**
     * render the  object as a JSON String
     * @param i (ignored due to limitations  in gson which uses a hardcoded indentation)
     * @return a JSON  String
     */
    public String toString(int i)  throws JSONException {
        return toPrettyJson(obj);
    }

    /**
     * indicates if a JSONObject has the same elements as another JSONObject
     * @param o another object
     * @return a bool
     */
    public boolean similar(Object o) {
        if (!(o instanceof JSONObject)) {
            return false;
        }
        JSONObject cst = (JSONObject) o;
        return this.obj.equals(cst.obj);
    }


    /**
     * @param key the key element to operate on
     * @return indicates that the structure has this key
     */
    public boolean has(String key) {
        return this.obj.has(key);
    }

    /**
     * @return number of keys in the structure
     */
    public int length() {
        return this.obj.size();
    }

    /**
     * get and element by key as its native object
     * @param key the key element to operate on
     * @return the object, this could be an object, array or primitive
     * @throws JSONException if the key does not exist
     */
    public Object get(String key) throws JSONException {
        return MAPPER.apply(getProperty(key));
    }

    /**
     * get the element as a JSONObject
     * @param key the key element to operate on
     * @return the element as a JSONObject
     * @throws JSONException  if it is not a object or the key does not exist
     */
    public JSONObject getJSONObject(String key) throws JSONException {
        try {
            return new JSONObject(getProperty(key).getAsJsonObject());
        } catch (IllegalStateException e) {
            throw new JSONException("JSONObject[\"%s\"] is not a JSONObject.", key);
        }
    }

    /**
     * get the element as a JSONObject
     * @param key the key element to operate on
     * @return an object or null if it is not an object or the key does not exist
     */
    public JSONObject optJSONObject(String key) {
        return getOrDefault(() -> getJSONObject(key), null);
    }

    /**
     * get the element as a JSONArray
     * @param key the key element to operate on
     * @return the element as a JSONArray
     * @throws JSONException  if it is not an array or the key does not exist
     */
    public JSONArray getJSONArray(String key) throws JSONException {
        try {
            return new JSONArray(getProperty(key).getAsJsonArray());
        } catch (IllegalStateException e) {
            throw new JSONException("JSONObject[\"%s\"] is not a JSONArray.", key);
        }
    }

    /**
     * optionally get the element as a JSONArray
     * @param key the key element to operate on
     * @return the element as a JSONArray or null if it doesn't exist or is not an array
     */
    public JSONArray optJSONArray(String key) {
        return getOrDefault(() -> getJSONArray(key), null);
    }

    /**
     * get a element property as a string
     * @param key the key element to operate on
     * @return a string representation of the value
     * @throws JSONException if the key does not exist
     */
    public String getString(String key) throws JSONException {
        return getProperty(key).getAsString();
    }

    /**
     * get a element property as a string
     * @param key the key element to operate on
     * @return a string representation of the value or null of it doesn't exist
     */
    public String optString(String key) {
        return optString(key, "");
    }

    /**
     * get a element property as a string
     * @param key the key element to operate on
     * @param defaultValue default value if the key does not exist or cannot be converted to a string
     * @return a string representation of the value or default value
     */
    public String optString(String key, String defaultValue) {
        return getOrDefault(() -> getString(key), defaultValue);
    }

    /**
     * get the value as a double
     * @param key the key element to operate on
     * @return the value
     * @throws JSONException if the object is not a number or does not exist
     */
    public double getDouble(String key) throws JSONException {
        return tryNumber(() -> getProperty(key).getAsDouble(), key);
    }

    /**
     * the value as double or NaN
     * @param key the key element to operate on
     * @return the value as a double or NaN if the key doesn't exist or the value is not a number
     */
    public double optDouble(String key) {
        return optDouble(key, Double.NaN);
    }

    /**
     * get the  value as a double or default value
     * @param key the key element to operate on
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return return value as double or a default value if value is not viable
     */
    public double optDouble(String key, double defaultValue) {
        return getOrDefault(() -> getDouble(key), defaultValue);
    }

    /**
     * get the value as a float
     * @param key the key element to operate on
     * @return the value
     * @throws JSONException if the object is not a number or does not exist
     */
    public float getFloat(String key) throws JSONException {
        return tryNumber(() -> getProperty(key).getAsFloat(), key);
    }

    /**
     * the value as double or NaN
     * @param key the key element to operate on
     * @return the value as a float or NaN if the key doesn't exist or the value is not a number
     */
    public float optFloat(String key) {
        return optFloat(key, Float.NaN);
    }

    /**
     * get the  value as a float or default value
     * @param key the key element to operate on
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return return value as double or a default value if value is not viable
     */
    public float optFloat(String key, float defaultValue) {
        return getOrDefault(() -> getFloat(key), defaultValue);
    }

    /**
     * get the value as a long
     * @param key the key element to operate on
     * @return the value
     * @throws JSONException if the object is not a number or does not exist
     */
    public long getLong(String key) throws JSONException {
        return tryNumber(() -> getProperty(key).getAsLong(), key);
    }

    /**
     * the value as long or NaN
     * @param key the key element to operate on
     * @return the value as a long or NaN if the key doesn't exist or the value is not a number
     */
    public long optLong(String key) {
        return optLong(key, 0L);
    }

    /**
     * get the  value as a long or default value
     * @param key the key element to operate on
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return return value as long or a default value if value is not viable
     */
    public long optLong(String key, long defaultValue) {
        return getOrDefault(() -> getLong(key), defaultValue);
    }

    /**
     * get an element property as a Number
     * @param key the key element to operate on
     * @return the element as a Number if it can be cast to one.
     * @throws JSONException  if it is not a number or the key does not exist
     */
    public Number getNumber(String key) throws JSONException {
        return tryNumber(() -> getProperty(key).getAsInt(), key);
    }

    /**
     * the value as int or 0
     * @param key the key element to operate on
     * @return the value as a int or 0 if the key doesn't exist or the value is not a number
     */
    public Number optNumber(String key) {
        return optNumber(key, 0);
    }

    /**
     * get the value as a Number or default value
     * @param key the key element to operate on
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return return value as long or a default value if value is not viable
     */
    public Number optNumber(String key, Number defaultValue) {
        return getOrDefault(() -> getNumber(key), defaultValue);
    }

    /**
     * get an element property as a int
     * @param key the key element to operate on
     * @return the element as a int if it can be cast to one.
     * @throws JSONException  if it is not a number or the key does not exist
     */
    public int getInt(String key) throws JSONException {
        return tryNumber(() -> getProperty(key).getAsInt(), key);
    }

    /**
     * the value as int or NaN
     * @param key the key element to operate on
     * @return the value as a int or 0 if the key doesn't exist or the value is not a number
     */
    public int optInt(String key) {
        return optInt(key, 0);
    }

    /**
     * get the  value as a int or default value
     * @param key the key element to operate on
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return return value as long or a default value if value is not viable
     */
    public int optInt(String key, int defaultValue) {
        return getOrDefault(() -> getInt(key), defaultValue);
    }

    /**
     * get an element property as a BigInteger
     * @param key the key element to operate on
     * @return the element as a BigInteger if it can be cast to one.
     * @throws JSONException  if it is not a number or the key does not exist
     */
    public BigInteger getBigInteger(String key) throws JSONException {
        return tryNumber(() -> getProperty(key).getAsBigInteger(), key);
    }

    /**
     * get the  value as a BigInteger or default value
     * @param key the key element to operate on
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return return value as BigInteger or a default value if value is not viable
     */
    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        return getOrDefault(() -> getBigInteger(key), defaultValue);
    }

    /**
     * get an element property as a BigDecimal
     * @param key the key element to operate on
     * @return the element as a BigInteger if it can be cast to one.
     * @throws JSONException  if it is not a number or the key does not exist
     */
    public BigDecimal getBigDecimal(String key) throws JSONException {
        return tryNumber(() -> getProperty(key).getAsBigDecimal(), key);
    }

    /**
     * get the  value as a BigDecimal or default value
     * @param key the key element to operate on
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return return value as BigDecimal or a default value if value is not viable
     */
    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        return getOrDefault(() -> getBigDecimal(key), defaultValue);
    }


    /**
     * gets a boolean value at a particular key
     * @param key the key
     * @return a boolean
     * @throws JSONException if the element does not exist or is not a boolean
     */
    public boolean getBoolean(String key) throws JSONException {
        JsonElement e = getProperty(key);
        if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isBoolean()) {
            throw new JSONException("JSONObject[\"%s\"] is not a boolean.", key);
        }
        return e.getAsBoolean();
    }

    /**
     * gets a boolean value at a particular key or false as default
     * @param key the key
     * @return a boolean
     */
    public boolean optBoolean(String key) {
        return optBoolean(key, false);
    }

    /**
     * gets a boolean value at a particular key or a default value
     * @param key the key
     * @param defaultValue a default value if the key does not exist or value is not a boolean
     * @return a boolean
     */
    public boolean optBoolean(String key, boolean defaultValue) {
        return getOrDefault(() -> getBoolean(key), defaultValue);
    }

    /**
     * get element as a enum value
     * @param <T> the type of enum you want
     * @param enumClass a enum class
     * @param key the key element to operate on
     * @return the value as a enum of T
     * @throws JSONException  if it does not map to a enum of T or the key does not exist
     */
    public <T extends Enum<T>> T getEnum(Class<T> enumClass, String key) throws JSONException {
        try {
            String v = getProperty(key).getAsString();
            return Enum.valueOf(enumClass, v);
        } catch (IllegalArgumentException e) {
            throw new JSONException("JSONObject[\"%s\"] is not an enum of type \"%s\".", key, enumClass.getSimpleName());
        }
    }

    /**
     * get element as a enum value or null if the value cannot be mapped
     * @param <T> the type of enum you want
     * @param enumClass a enum class
     * @param key the key element to operate on
     * @return the value as a enum of T
     */
    public <T extends Enum<T>> T optEnum(Class<T> enumClass, String key) {
        return optEnum(enumClass, key, null);
    }

    /**
     * get element as a enum value or a default value if the value cannot be mapped
     * @param <T> the type of enum you want
     * @param enumClass a enum class
     * @param key the key element to operate on
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return the value as a enum of T
     */
    public <T extends Enum<T>> T optEnum(Class<T> enumClass, String key, T defaultValue) {
        return getOrDefault(() -> getEnum(enumClass, key), defaultValue);
    }

    /**
     * put a JSONObject at a particular key
     * @param key the key element to operate on
     * @param object JSONObject
     * @return this JSONObject
     */
    public JSONObject put(String key, JSONObject object) throws JSONException {
        obj.add(key, object.obj);
        return this;
    }

    /**
     * put a JSONArray at a particular key
     * @param key the key element to operate on
     * @param array JSONArray
     * @return this JSONObject
     */
    public JSONObject put(String key, JSONArray array) throws JSONException {
        obj.add(key, array.getArray());
        return this;
    }

    /**
     * put a boolean at a particular key
     * @param key the key element to operate on
     * @param value the boolean value to put
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public JSONObject put(String key, boolean value) throws JSONException {
        obj.addProperty(key, value);
        return this;
    }

    /**
     * put a Number at a particular key
     * @param key the key element to operate on
     * @param value Number
     * @return this JSONObject
     */
    public JSONObject put(String key, Number value) throws JSONException {
        this.obj.addProperty(key, value);
        return this;
    }

    /**
     * put a double at a particular key
     * @param key the key element to operate on
     * @param value double
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public JSONObject put(String key, double value) throws JSONException {
        this.obj.addProperty(key, value);
        return this;
    }

    /**
     * put a float at a particular key
     * @param key the key element to operate on
     * @param value float
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public JSONObject put(String key, float value) throws JSONException {
        this.obj.addProperty(key, value);
        return this;
    }

    /**
     * put a long at a particular key
     * @param key the key element to operate on
     * @param value long
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public JSONObject put(String key, long value) throws JSONException {
        this.obj.addProperty(key, value);
        return this;
    }

    /**
     * put a int at a particular key
     * @param key the key element to operate on
     * @param value int
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public JSONObject put(String key, int value) throws JSONException {
        this.obj.addProperty(key, value);
        return this;
    }

    /**
     * put a String at a particular key
     * @param key the key element to operate on
     * @param value Number
     * @return this JSONObject
     */
    public JSONObject put(String key, String value) throws JSONException {
        this.obj.addProperty(key, value);
        return this;
    }

    /**
     * put a Collection as a JSONArray at a particular key
     * @param key the key element to operate on
     * @param value Collection
     * @return this JSONObject
     */
    public JSONObject put(String key, Collection value) throws JSONException {
        this.put(key, new JSONArray(value));
        return this;
    }

    /**
     * put a Collection as a JSONArray at a particular key
     * @param key the key element to operate on
     * @param value Collection
     * @return this JSONObject
     */
    public JSONObject put(String key, Map value) throws JSONException {
        this.put(key, new JSONObject(value));
        return this;
    }

    /**
     * put a enum at a particular key. The enum will be stored as a string by name
     * @param <T> a type of enum
     * @param key the key element to operate on
     * @param enumvalue a enum
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public <T extends Enum<T>> JSONObject put(String key, T enumvalue) throws JSONException {
        obj.add(key, enumvalue == null ? JsonNull.INSTANCE : new JsonPrimitive(enumvalue.name()));
        return this;
    }

    /**
     * remove a element by key name
     * @param key the key element to operate on
     * @return the object value that was removed
     */
    public Object remove(String key) {
        if(!has(key)){
            return null;
        }
        Object o = get(key);
        obj.remove(key);
        return o;
    }

    /**
     * Add a element to a JSONArray in a element. If the value is not
     * already an array it will be made one with the original value as the first element
     * @param key the key element to operate on
     * @param additionalValue value to append to the array
     * @return this JSONObject
     */
    public JSONObject accumulate(String key, Object additionalValue) throws JSONException {
        requireNonNull(key, "Null key.");
        if (!obj.has(key)) {
            return this;
        }
        Object existing = get(key);
        if (existing instanceof JSONArray) {
            ((JSONArray) existing).put(additionalValue);
            put(key, (JSONArray) existing);
        } else {
            JSONArray a = new JSONArray();
            a.put(existing);
            a.put(additionalValue);
            put(key, a);
        }
        return  this;
    }

    /**
     * appends to an existing array
     * @param key the key element to operate on
     * @param value the object to put
     * @throws JSONException if the value exists and is not an array
     * @return this JSONObject
     */
    public JSONObject append(String key, Object value) throws JSONException {
        requireNonNull(key, "Null key.");
        if (has(key)) {
            JSONArray arr = getJSONArray(key);
            arr.put(value);
            put(key, arr);
        } else {
            JSONArray arr = new JSONArray();
            arr.put(value);
            put(key, arr);
        }
        return this;
    }

    /**
     * increments a numeric value by 1, or creates it with a value of 1 if
     * it does not exist.
     * @param key the key element to operate on
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public JSONObject increment(String key) throws JSONException {
        if (!has(key)) {
            put(key, 1);
        } else {
            Object n = get(key);
            if (!(n instanceof Number)) {
                throw new JSONException("");
            } else if (n instanceof Integer) {
                put(key, ((Integer) n) + 1);
            } else if (n instanceof Double) {
                put(key, ((Double) n) + 1);
            }
        }
        return this;
    }

    /**
     * put a value to a key only if it does not exist
     * @param key the key element to operate on
     * @param value the object to put
     * @return this JSONObject
     * @throws JSONException if the key exists.
     */
    public JSONObject putOnce(String key, Object value) throws JSONException {
        if(has(key)){
            throw new JSONException("Duplicate key \"foo\"");
        }
        return put(key, value);
    }

    /**
     * put an object to a key.
     * the value must be a JSON type
     * @param key the key element to operate on
     * @param value the  object to put
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public JSONObject put(String key, Object value) throws JSONException {
        if(value == null){
            put(key, (String) value);
        } else if (value instanceof Number){
            put(key, (Number) value);
        } else if (value instanceof Boolean){
            put(key, (boolean) value);
        } else if (value instanceof JSONArray) {
            put(key, (JSONArray) value);
        } else if (value instanceof JSONObject) {
            put(key, (JSONObject) value);
        } else if (value instanceof Map){
            put(key, (Map) value);
        } else if (value instanceof Collection) {
            put(key, (Collection) value);
        } else if (value.getClass().isArray()){
            put(key, wrapArray(value));
        } else {
            put(key, String.valueOf(value));
        }
        return this;
    }

    /**
     * optional put a value at a key as long as both they key and value are not null
     * otherwise it does nothing
     * @param key the key element to operate on
     * @param value the  object to put
     * @return this JSONObject
     * @throws JSONException if something goes wrong
     */
    public JSONObject putOpt(String key, Object value) throws JSONException {
        if(key == null || value == null){
            return this;
        }
        return put(key, value);
    }

    /**
     * get all the keys as a set
     * @return a set of keys
     */
    public Set<String> keySet() {
        return obj.keySet();
    }

    /**
     * get a iterator for the keyset
     * @return a Iterator of keys
     */
    public Iterator<String> keys() {
        return obj.keySet().iterator();
    }

    /**
     * converts this object to a map
     * @return this object as a map
     */
    public Map<String, Object> toMap() {
        return toMap(obj);
    }

    /**
     * get the key names as a JSONArray
     * @return a JSONArray of keys
     */
    public JSONArray names() {
        return new JSONArray(keySet());
    }

    /**
     * creates an  array of the values for they keys you provide
     * @param names a list of keys you want an array for
     * @return a JSONArray of values or null of the array is null or empty
     * @throws JSONException if something goes wrong
     */
    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if(names == null || names.isEmpty()){
            return null;
        }
        JSONArray array = new JSONArray();
        for(Object name : names){
            array.put(opt(String.valueOf(name)));
        }
        return array;
    }

    private JsonElement getProperty(String key) {
        if (!obj.has(key)) {
            throw new JSONException("JSONObject[\"%s\"] not found.", key);
        }
        return obj.get(key);
    }

    private <T extends Number> T tryNumber(Supplier<T> supplier, String key) {
        try {
            return supplier.get();
        } catch (NumberFormatException e) {
            throw new JSONException("JSONObject[\"%s\"] is not a number.", key);
        }
    }

    private <T> T getOrDefault(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public boolean equals(Object other) {
        return this.similar(other);
    }

    @Override
    public int hashCode() {
        return obj.hashCode();
    }

    /**
     * optionally return the object or null if it doesn't exist
     * @param key the key
     * @return the object at the key or null
     */
    public Object opt(String key) {
        try{
            return get(key);
        }catch (JSONException e){
            return null;
        }
    }

    /**
     * @return boolean if the object is empty
     */
    public boolean isEmpty() {
        return obj.size() == 0;
    }

    /**
     * indicate if the key does not exist or its value is null
     * @param key the key
     * @return a boolean indicating null
     */
    public boolean isNull(String key) {
        return !has(key) || get(key) == null;
    }


    private static boolean isPrimitive(Object o){
        return (o instanceof String
                || o instanceof Number
                || o instanceof Boolean);
    }
}
