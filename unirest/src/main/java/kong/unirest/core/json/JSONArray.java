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

package kong.unirest.core.json;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Supplier;

/**
 * https://json.org/
 * https://tools.ietf.org/html/rfc7159#section-4
 * Represents a JSON Array
 */
public class JSONArray extends JSONElement implements Iterable<Object> {
    private transient final JsonEngine.Array obj;

    /**
     * construct a empty JSONArray
     */
    public JSONArray() {
        this(CoreFactory.getCore().newEngineArray());
    }

    /**
     * construct a JSONArray from a String
     * @param jsonString a JSON String
     */
    public JSONArray(String jsonString) {
        this(CoreFactory.getCore().newJsonArray(jsonString));
    }

    /**
     * Construct a JSONArray from a collection.
     * @param collection a collection which contains json types
     */
    public JSONArray(Collection<?> collection) {
        this(CoreFactory.getCore().newJsonArray(collection));
    }

    /**
     * Construct a JSONArray from a typed  array (int[]).
     * @param array an array type which may be typed (e.g. Object[], String[], JSONObject[])
     */
    public JSONArray(Object array) {
        this(toJsonArray(array));
    }

    private static JsonEngine.Array toJsonArray(Object thing) {
        if(thing instanceof Collection){
            return CoreFactory.getCore().newJsonArray((Collection)thing);
        }
        if (thing != null && thing.getClass().isArray()) {
            Collection pre = new ArrayList();
            for (Object o : (Object[]) thing) {
                pre.add(o);
            }
            return CoreFactory.getCore().newJsonArray(pre);
        }

        throw new JSONException("JSONArray initial value should be a string or collection or array.");
    }

    JSONArray(JsonEngine.Array array) {
        super(array);
        obj = array;
    }

    JSONArray(JsonEngine.Element jsonElement) {
        this(jsonElement.getAsJsonArray());
    }

    /**
     * @return The length of the array
     */
    public int length() {
        return obj.size();
    }

    /**
     * append a JSONObject to the end of the array
     * @param object a JSONObject
     * @return this JSONArray
     */
    public JSONArray put(JSONObject object) {
        obj.add(object.asElement());
        return this;
    }

    /**
     * append a JSONArray as an element to the end of the array
     * @param array a JSONArray
     * @return this JSONArray
     */
    public JSONArray put(JSONArray array) {
        obj.add(array.obj);
        return this;
    }

    /**
     * add a double to the array
     * @param num a double
     * @return this JSONArray
     */
    public JSONArray put(double num) throws JSONException {
        obj.add(num);
        return this;
    }

    /**
     * add a int to the array
     * @param num a int
     * @return this JSONArray
     */
    public JSONArray put(int num) {
        obj.add(num);
        return this;
    }

    /**
     * add a long to the array
     * @param num a long
     * @return this JSONArray
     */
    public JSONArray put(long num) {
        obj.add(num);
        return this;
    }

    /**
     * add a float to the array
     * @param num a float
     * @return this JSONArray
     */
    public JSONArray put(float num) throws JSONException {
        obj.add(num);
        return this;
    }

    /**
     * add a Number to the array
     * @param num a Number
     * @return this JSONArray
     */
    public JSONArray put(Number num) {
        obj.add(num);
        return this;
    }

    /**
     * add a Boolean to the array
     * @param bool a Boolean
     * @return this JSONArray
     */
    public JSONArray put(boolean bool) {
        obj.add(bool);
        return this;
    }

    /**
     * add a String to the array
     * @param str a String
     * @return this JSONArray
     */
    public JSONArray put(String str) {
        obj.add(str);
        return this;
    }

    /**
     * add a JSONObject to the array as a map
     * @param map a Map which should contain String keys and JSON types for values
     * @return this JSONArray
     */
    public JSONArray put(Map map) {
        obj.add(toJsonObject(map));
        return this;
    }

    /**
     * add a JSONArray to the array
     * @param collection a Collection of JSON Types
     * @return this JSONArray
     */
    public JSONArray put(Collection collection) {
        obj.add(toJsonArray(collection));
        return this;
    }

    /**
     * put a enum which will be put as the string name
     * @param enumValue a enum
     * @param <T> a enum type
     * @return this JSONArray
     */
    public <T extends Enum> JSONArray put(T enumValue) {
        return put(enumValue.name());
    }

    /**
     * put a long at a specific instance
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index the index position to put to
     * @param number a long
     * @return this JSONArray
     */
    public JSONArray put(int index, long number) throws JSONException {
        put(index, CoreFactory.getCore().newJsonPrimitive(number));
        return this;
    }

    /**
     * put a double at a specific instance
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index the index position to put to
     * @param number a double
     * @return this JSONArray
     */
    public JSONArray put(int index, double number) throws JSONException {
        put(index, CoreFactory.getCore().newJsonPrimitive(number));
        return this;
    }

    /**
     * put a boolean at a specific index
     * @param index the index position to put to
     * @param bool a bool value
     * @return this JSONArray
     */
    public JSONArray put(int index, boolean bool) throws JSONException {
        put(index, CoreFactory.getCore().newJsonPrimitive(bool));
        return this;
    }

    /**
     * put a object at a specific instance
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index the index position to put to
     * @param object a long
     * @return this JSONArray
     * @throws JSONException if something goes wrong
     */
    public JSONArray put(int index, Object object) throws JSONException {
        if (object == null) {
            put(index, (JsonEngine.Element) null);
        } else if (object instanceof Number) {
            put(index, (Number) object);
        } else if (object instanceof Boolean) {
            put(index, (boolean) object);
        } else if (object instanceof JSONObject) {
            put(index, ((JSONObject) object).getElement());
        } else if (object instanceof JSONArray) {
            put(index, ((JSONArray) object).getElement());
        } else {
            put(index, String.valueOf(object));
        }
        return this;
    }

    /**
     * put a float at a specific instance
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index the index position to put to
     * @param number a Number
     * @return this JSONArray
     */
    public JSONArray put(int index, float number) throws JSONException {
        put(index, CoreFactory.getCore().newJsonPrimitive(number));
        return this;
    }

    /**
     * put a int at a specific instance
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index the index position to put to
     * @param number a int
     * @return this JSONArray
     */
    public JSONArray put(int index, int number) throws JSONException {
        put(index, CoreFactory.getCore().newJsonPrimitive(number));
        return this;
    }

    /**
     * put a Number at a specific instance
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index the index position to put to
     * @param number a Number
     * @return this JSONArray
     */
    public JSONArray put(int index, Number number) {
        put(index, CoreFactory.getCore().newJsonPrimitive(number));
        return this;
    }

    /**
     * put a String at a specific index
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index the index position to put to
     * @param string a String
     * @return this JSONArray
     */
    public JSONArray put(int index, String string) {
        put(index, CoreFactory.getCore().newJsonPrimitive(string));
        return this;
    }

    /**
     * put a JSONObject as a map at a specific index
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index index of the element to replace
     * @param map a Map of String keys and values of JSON Types
     * @return this JSONArray
     */
    public JSONArray put(int index, Map map) throws JSONException {
        return put(index, toJsonObject(map));
    }

    /**
     * put a JSONArray at a specific index as a Collection
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param index the index position to put to
     * @param collection  a Collection of JSON types
     * @return this JSONArray
     */
    public JSONArray put(int index, Collection collection) throws JSONException {
        return put(index, toJsonArray(collection));
    }

    /**
     * put a Enum name at a specific index as a string
     * if the index is beyond the currently length the array will be buffered with nulls
     * @param <T> a type of enum
     * @param index the index position to put to
     * @param enumValue a enum value to put
     * @return this JSONArray
     */
    public <T extends Enum> JSONArray put(int index, T enumValue) {
        return put(index, CoreFactory.getCore().newJsonPrimitive(enumValue));
    }

    private JSONArray put(int index, JsonEngine.Element o) {
        while (obj.size() < index + 1) {
            obj.add((JsonEngine.Element) null);
        }
        if (index < obj.size()) {
            obj.set(index, o);
        } else if (index == obj.size()) {
            obj.add(o);
        }
        return this;
    }

    /**
     * add a Object to the array
     * Must be a valid JSON type or else it will be turned into a string
     * @param object the JSON Typed object
     * @return this JSONArray
     */
    public JSONArray put(Object object) {
        if (object == null) {
            obj.add((JsonEngine.Element) null);
        } else if (object instanceof Number) {
            put((Number) object);
        } else if (object instanceof Boolean) {
            put((boolean) object);
        } else if (object instanceof JSONObject) {
            put((JSONObject) object);
        } else if (object instanceof JSONArray) {
            put((JSONArray) object);
        } else {
            put(String.valueOf(object));
        }
        return this;
    }

    /**
     * Removes the element at the specified position in this array. Shifts any subsequent elements
     * to the left (subtracts one from their indices). Returns the element that was removed from
     * the array.
     * @param index index the index of the element to be removed
     * @return the element previously at the specified position or null if the index did not exist.
     */
    public Object remove(int index) {
        try {
            JsonEngine.Element remove = obj.remove(index);
            return MAPPER.apply(remove);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * get a boolean at a specified index
     * @param index the array index position
     * @return a boolean
     * @throws JSONException if the element is not a boolean or index is out of bounds
     */
    public boolean getBoolean(int index) throws JSONException {
        JsonEngine.Element e = getElement(index);
        if (!e.isJsonPrimitive() || !e.getAsPrimitive().isBoolean()) {
            throw new JSONException("JSONArray[%s] is not a boolean.", index);
        }
        return e.getAsBoolean();
    }

    /**
     * get a boolean at a specified index
     * @param index the array index position
     * @return a boolean
     */
    public boolean optBoolean(int index) {
        return optBoolean(index, false);
    }

    /**
     * get a boolean at a specified index
     * @param index the array index position
     * @param defaultValue a default value if the index position does not exist or is not a boolean
     * @return a boolean
     */
    public boolean optBoolean(int index, boolean defaultValue) {
        return getOrDefault(() -> getElement(index).getAsBoolean(), defaultValue);
    }

    /**
     * get a JSONObject at a specified index
     * @param index the array index position
     * @return a JSONObject
     * @throws JSONException if the element is not a JSONObject or index is out of bounds
     */
    public JSONObject getJSONObject(int index) throws JSONException {
        try {
            return new JSONObject(getElement(index));
        } catch (IllegalStateException e) {
            throw new JSONException("JSONArray[%s] is not a JSONObject.", index);
        }
    }

    /**
     * get a JSONObject at a specified index or null if it does not exist
     * or is not a valid JSONObject
     * @param index the array index position
     * @return a JSONObject
     */
    public JSONObject optJSONObject(int index) {
        return getOrDefault(() -> new JSONObject(getElement(index).getAsJsonObject()), null);
    }

    /**
     * get a Double at a specified index
     * @param index the array index position
     * @return a Double
     * @throws JSONException if the element is not a Double or index is out of bounds
     */
    public double getDouble(int index) throws JSONException {
        return tryNumber(() -> getElement(index).getAsDouble(), index);
    }

    /**
     * get a Double at a specified index
     * @param index the array index position
     * @return a Double
     */
    public double optDouble(int index) {
        return optDouble(index, Double.NaN);
    }

    /**
     * get a Double at a specified index, or a default value
     * if the value does not exist or is not a double
     * @param index the array index position
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return a Double
     */
    public double optDouble(int index, double defaultValue) {
        return getOrDefault(() -> getDouble(index), defaultValue);
    }

    /**
     * get a Float at a specified index
     * @param index the array index position
     * @return a Float
     * @throws JSONException if the element is not a Float or index is out of bounds
     */
    public float getFloat(int index) throws JSONException {
        return tryNumber(() -> getElement(index).getAsFloat(), index);
    }

    /**
     * get a Float at a specified index, or a NaN value
     * if the value does not exist or is not a Float
     * @param index the array index position
     * @return a Float
     */
    public float optFloat(int index) {
        return optFloat(index, Float.NaN);
    }

    /**
     * get a Float at a specified index, or a default value
     * if the value does not exist or is not a Float
     * @param index the array index position
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return a Float
     */
    public float optFloat(int index, float defaultValue) {
        return getOrDefault(() -> getFloat(index), defaultValue);
    }

    /**
     * get a long at a specified index
     * @param index the array index position
     * @return a long
     * @throws JSONException if the element is not a long or index is out of bounds
     */
    public long getLong(int index) throws JSONException {
        return tryNumber(() -> getElement(index).getAsLong(), index);
    }

    /**
     * get a long at a specified index, or 0
     * if the value does not exist or is not a long
     * @param index the array index position
     * @return a long
     */
    public long optLong(int index) {
        return optLong(index, 0L);
    }

    /**
     * get a long at a specified index, or a default value
     * if the value does not exist or is not a long
     * @param index the array index position
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return a long
     */
    public long optLong(int index, long defaultValue) {
        return getOrDefault(() -> getLong(index), defaultValue);
    }

    /**
     * get a Number at a specified index
     * @param index the array index position
     * @return a Number
     * @throws JSONException if the element is not a Number or index is out of bounds
     */
    public Number getNumber(int index) throws JSONException {
        return tryNumber(() -> getElement(index).getAsInt(), index);
    }

    /**
     * get a Number at a specified index
     * @param index the array index position
     * @return a int
     */
    public Number optNumber(int index) {
        return getOrDefault(() -> getNumber(index), null);
    }

    /**
     * get a Number at a specified index
     * @param index the array index position
     * @param defaultValue the default value if the index does not exist or is not a number
     * @return a Number
     */
    public Number optNumber(int index, Number defaultValue) {
        return getOrDefault(() -> getNumber(index), defaultValue);
    }

    /**
     * get a int at a specified index
     * @param index the array index position
     * @return a int
     * @throws JSONException if the element is not a int or index is out of bounds
     */
    public int getInt(int index) throws JSONException {
        return tryNumber(() -> getElement(index).getAsInt(), index);
    }

    /**
     * get a int at a specified index, or 0
     * if the value does not exist or is not a int
     * @param index the array index position
     * @return a int
     */
    public int optInt(int index) {
        return optInt(index, 0);
    }

    /**
     * get a int at a specified index, or a default value
     * if the value does not exist or is not a int
     * @param index the array index position
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return a int
     */
    public int optInt(int index, int defaultValue) {
        return getOrDefault(() -> getInt(index), defaultValue);
    }

    /**
     * get a BigInteger at a specified index
     * @param index the array index position
     * @return a BigInteger
     * @throws JSONException if the element is not a BigInteger or index is out of bounds
     */
    public BigInteger getBigInteger(int index) throws JSONException {
        return tryNumber(() -> getElement(index).getAsBigInteger(), index);
    }

    /**
     * get a BigInteger at a specified index, or a default value
     * if the value does not exist or is not a BigInteger
     * @param index the array index position
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return a BigInteger
     */
    public BigInteger optBigInteger(int index, BigInteger defaultValue) {
        return getOrDefault(() -> getBigInteger(index), defaultValue);
    }

    /**
     * get a BigDecimal at a specified index
     * @param index the array index position
     * @return a BigDecimal
     * @throws JSONException if the element is not a BigDecimal or index is out of bounds
     */
    public BigDecimal getBigDecimal(int index) throws JSONException {
        return tryNumber(() -> getElement(index).getAsBigDecimal(), index);
    }

    /**
     * get a BigDecimal at a specified index, or a default value
     * if the value does not exist or is not a BigDecimal
     * @param index the array index position
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return a BigDecimal
     */
    public BigDecimal optBigDecimal(int index, BigDecimal defaultValue) {
        return getOrDefault(() -> getBigDecimal(index), defaultValue);
    }

    /**
     * get a String at a specified index
     * @param index the array index position
     * @return a String
     * @throws JSONException if the element is not a String or index is out of bounds
     */
    public String getString(int index) throws JSONException {
        return getElement(index).getAsString();
    }

    /**
     * get a String at a specified index, or an empty string
     * if the value does not exist or is not a String
     * @param index the array index position
     * @return a String
     */
    public String optString(int index) {
        return optString(index, "");
    }

    /**
     * get a String at a specified index, or a default value
     * if the value does not exist or is not a String
     * @param index the array index position
     * @param defaultValue the default value to return if the index or value type are not valid
     * @return a String
     */
    public String optString(int index, String defaultValue) {
        return getOrDefault(() -> getString(index), defaultValue);
    }

    /**
     * get a JSONArray at a specified index
     * @param index the array index position
     * @return a JSONArray
     * @throws JSONException if the element is not a JSONArray or index is out of bounds
     */
    public JSONArray getJSONArray(int index) throws JSONException {
        try {
            return new JSONArray(getElement(index).getAsJsonArray());
        } catch (IllegalStateException e) {
            throw new JSONException("JSONArray[%s] is not a JSONArray.", index);
        }
    }

    /**
     * get a String at a specified index, or null
     * if the value does not exist or is not a JSONArray
     * @param index the array index position
     * @return a JSONArray
     */
    public JSONArray optJSONArray(int index) {
        return getOrDefault(() -> getJSONArray(index), null);
    }

    /**
     * get a enum value based on name from a specific index
     * @param enumClass the enum type
     * @param index the index
     * @param <T> the type of enum
     * @return a enum value
     * @throws JSONException if the index is out of bounds or the value cannot be converted to the enum type
     */
    public <T extends Enum<T>> T getEnum(Class<T> enumClass, int index) throws JSONException {

        String raw = getElement(index).getAsString();
        try {
            return Enum.valueOf(enumClass, raw);
        } catch (IllegalArgumentException e) {
            throw new JSONException("JSONArray[%s] is not an enum of type \"%s\".", index, enumClass.getSimpleName());
        }
    }

    public <T extends Enum<T>> T optEnum(Class<T> enumClass, int index) {
        return optEnum(enumClass, index, null);
    }

    public <T extends Enum<T>> T optEnum(Class<T> enumClass, int index, T defaultValue) {
        return getOrDefault(() -> getEnum(enumClass, index), defaultValue);
    }

    /**
     * get the element at the index
     * @param index the index number
     * @return the object at that position
     * @throws JSONException if index is out of bounds
     */
    public Object get(int index) throws JSONException {
        return MAPPER.apply(obj.get(index));
    }

    /**
     * get the element at the index
     * @param index the index number
     * @return the object at that position
     */
    public Object opt(int index) {
        try {
            return get(index);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * returns the String representation of the JSONArray
     * @return string json
     */
    @Override
    public String toString() {
        return toJson(obj);
    }

    /**
     * returns the String representation of the JSONArray
     * @param indent  currently ignored until this is addressed by GSON
     * @return string json
     * @throws JSONException if something goes wrong
     */
    public String toString(int indent) throws JSONException {
        return toPrettyJson(obj);
    }

    /**
     * return the array as a string delimited by a specific token
     * @param token the delimitation token
     * @return a token delimited array
     * @throws JSONException if something goes wrong
     */
    public String join(String token) throws JSONException {
        return obj.join(token);
    }

    /**
     * @return the iterator for the array
     */
    @Override
    public Iterator<Object> iterator() {
        return (Iterator) toList().iterator();
    }


    /**
     * indicates if a JSONArray has the same elements as another JSONArray
     * @param o the other object
     * @return a bool
     */
    public boolean similar(Object o) {
        if (!(o instanceof JSONArray)) {
            return false;
        }
        JSONArray cst = (JSONArray) o;
        return this.equals(cst);
    }

    /**
     * Converts the JSONArray to a List
     * @return a List
     */
    public List toList() {
        List list = new ArrayList();
        for (int i = 0; i < obj.size(); i++) {
            list.add(get(i));
        }
        return list;
    }

    /**
     * Indicates if the index does not exist or it's contents are null
     * @param index the index poisition to test
     * @return boolean if the index exists
     */
    public boolean isNull(int index) {
        return index >= obj.size() || obj.get(index).isJsonNull();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof JsonEngine.Array) {
            return this.obj.equals(o);
        } else if (o instanceof JSONArray) {
            return this.obj.equals(((JSONArray) o).obj);
        }
        return false;
    }

    public int hashCode() {
        return this.obj.hashCode();
    }

    JsonEngine.Array getArray() {
        return obj;
    }


    private JsonEngine.Element getElement(int index) {
        try {
            return obj.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new JSONException("JSONArray[%s] not found.", index);
        }
    }

    private <T> T getOrDefault(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private <T extends Number> T tryNumber(Supplier<T> supplier, int index) {
        try {
            return supplier.get();
        } catch (NumberFormatException e) {
            throw new JSONException("JSONArray[%s] is not a number.", index);
        }
    }

    /**
     * Produce a JSONObject by combining a JSONArray of names with the values of
     * this JSONArray.
     *
     * @param names A JSONArray containing a list of key strings. These will be paired with the values.
     * @return A JSONObject, or null if there are no names or if this JSONArray has no values.
     * @throws JSONException If any of the names are null.
     */
    public JSONObject toJSONObject(JSONArray names) throws JSONException {
        if (names == null || names.isEmpty() || isEmpty()) {
            return null;
        }
        JSONObject object = new JSONObject();
        int index = 0;
        for (Object key : names) {
            if (index >= length()) {
                break;
            }
            if (key == null) {
                throw new JSONException("JSONArray[%s] not a string.", index);
            }
            object.put(String.valueOf(key), get(index));
            index++;
        }
        return object;
    }

    /**
     * returns if the array is empty
     * @return bool if it be empty
     */
    public boolean isEmpty() {
        return obj.size() == 0;
    }
}
