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

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * https://json.org/
 * https://tools.ietf.org/html/rfc7159#section-4
 * represents a JSON Object
 */
public class JSONObject {
    private static transient final ToObjectMapper MAPPER = new ToObjectMapper();
    private transient final JsonObject obj;


    /**
     * https://tools.ietf.org/html/rfc7159#section-4
     * @param string a json object string
     */
    public JSONObject(String string) {
        this(Json.fromJson(string, JsonObject.class));
    }

    /**
     * construct using a map
     * @param map a map representing the elements of a JSON Object
     */
    public JSONObject(Map<String, Object> map) {
        obj = Json.fromJson(Json.toJson(map), JsonObject.class);
    }

    /**
     * an empty JSON object
     */
    public JSONObject() {
        this.obj = new JsonObject();
    }

    JSONObject(JsonElement jsonElement) {
        this.obj = jsonElement.getAsJsonObject();
    }


    JsonElement asElement() {
        return obj;
    }

    /**
     * @return the object as a JSON string with no formatting
     */
    @Override
    public String toString() {
        return Json.toJson(obj);
    }

    /**
     * render the  object as a JSON String
     * @param i (ignored due to limitations  in gson which uses a hardcoded indentation)
     * @return a JSON  String
     */
    public String toString(int i) {
        return Json.toPrettyJson(obj);
    }

    /**
     * indicates if a JSONObject has the same elements as another JSONObject
     * @param o
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
     */
    public Object get(String key) {
        return MAPPER.apply(getProperty(key));
    }

    /**
     * get the element as a JSONObject
     * @param key the key element to operate on
     * @return the element as a JSONObject
     * @throws JSONException  if it is not a object or the key does not exist
     */
    public JSONObject getJSONObject(String key) {
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
    public JSONArray getJSONArray(String key) {
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
    public String getString(String key) {
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
    public double getDouble(String key) {
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
     * @param defaultValue
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
    public float getFloat(String key) {
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
     * @param defaultValue
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
    public long getLong(String key) {
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
     * @param defaultValue
     * @return return value as long or a default value if value is not viable
     */
    public long optLong(String key, long defaultValue) {
        return getOrDefault(() -> getLong(key), defaultValue);
    }

    /**
     * get an element property as a int
     * @param key the key element to operate on
     * @return the element as a int if it can be cast to one.
     * @throws JSONException  if it is not a number or the key does not exist
     */
    public int getInt(String key) {
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
     * get the  value as a long or default value
     * @param key the key element to operate on
     * @param defaultValue
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
    public BigInteger getBigInteger(String key) {
        return tryNumber(() -> getProperty(key).getAsBigInteger(), key);
    }

    /**
     * get the  value as a BigInteger or default value
     * @param key the key element to operate on
     * @param defaultValue
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
    public BigDecimal getBigDecimal(String key) {
        return tryNumber(() -> getProperty(key).getAsBigDecimal(), key);
    }

    /**
     * get the  value as a BigDecimal or default value
     * @param key the key element to operate on
     * @param defaultValue
     * @return return value as BigDecimal or a default value if value is not viable
     */
    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        return getOrDefault(() -> getBigDecimal(key), defaultValue);
    }

    /**
     * get element as a enum value
     * @param enumClass a enum class
     * @param key the key element to operate on
     * @param <T> the type of enum you want
     * @return the value as a enum of T
     * @throws JSONException  if it does not map to a enum of T or the key does not exist
     */
    public <T extends Enum<T>> T getEnum(Class<T> enumClass, String key) {
        try {
            String v = getProperty(key).getAsString();
            return Enum.valueOf(enumClass, v);
        } catch (IllegalArgumentException e) {
            throw new JSONException("JSONObject[\"%s\"] is not an enum of type \"%s\".", key, enumClass.getSimpleName());
        }
    }

    /**
     * get element as a enum value or null if the value cannot be mapped
     * @param enumClass a enum class
     * @param key the key element to operate on
     * @param <T> the type of enum you want
     * @return the value as a enum of T
     */
    public <T extends Enum<T>> T optEnum(Class<T> enumClass, String key) {
        return optEnum(enumClass, key, null);
    }

    /**
     * get element as a enum value or a default value if the value cannot be mapped
     * @param enumClass a enum class
     * @param key the key element to operate on
     * @param <T> the type of enum you want
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
    public JSONObject put(String key, JSONObject object) {
        obj.add(key, object.obj);
        return this;
    }

    /**
     * put a JSONArray at a particular key
     * @param key the key element to operate on
     * @param array JSONArray
     * @return this JSONObject
     */
    public JSONObject put(String key, JSONArray array) {
        obj.add(key, array.getArray());
        return this;
    }

    /**
     * put a boolean at a particular key
     * @param key the key element to operate on
     * @param value
     * @return this JSONObject
     */
    public JSONObject put(String key, Boolean value){
        obj.addProperty(key, value);
        return this;
    }

    /**
     * put a Number at a particular key
     * @param key the key element to operate on
     * @param value Number
     * @return this JSONObject
     */
    public JSONObject put(String key, Number value) {
        this.obj.addProperty(key, value);
        return this;
    }

    /**
     * put a String at a particular key
     * @param key the key element to operate on
     * @param value Number
     * @return this JSONObject
     */
    public JSONObject put(String key, String value) {
        this.obj.addProperty(key, value);
        return this;
    }

    /**
     * put a enum at a particular key. The enum will be stored as a string by name
     * @param key the key element to operate on
     * @param enumvalue a enum
     * @return this JSONObject
     */
    public <T extends Enum<T>> JSONObject put(String key, T enumvalue) {
        obj.add(key, enumvalue == null ? JsonNull.INSTANCE : new JsonPrimitive(enumvalue.name()));
        return this;
    }

    /**
     * Write the JSON to a Writer
     * @param sw the writer
     */
    public void write(Writer sw) {
        Json.write(obj, sw);
    }

    /**
     * Write the JSON to a Writer with a pretty format
     * due to limitations in GSON the index and indent are currently ignored
     * @param sw the writer
     */
    public void write(Writer sw, int indentFactor, int indent) {
        Json.writePretty(obj, sw);
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
    public JSONObject accumulate(String key, Object additionalValue) {
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
     * @param value
     * @throws JSONException if the value exists and is not an array
     * @return this JSONObject
     */
    public JSONObject append(String key, Object value) {
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
     */
    public JSONObject increment(String key) {
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
     * @param value
     * @throws JSONException if the key exists.
     * @return this JSONObject
     */
    public JSONObject putOnce(String key, Object value) {
        if(has(key)){
            throw new JSONException("Duplicate key \"foo\"");
        }
        return put(key, value);
    }

    /**
     * put an object to a key.
     * the value must be a JSON type
     * @param key the key element to operate on
     * @param value
     * @return this JSONObject
     */
    public JSONObject put(String key, Object value){
        if(value == null){
            put(key, (String) value);
        } else if (value instanceof Number){
            put(key, (Number)value);
        } else if (value instanceof Boolean){
            put(key, (Boolean)value);
        } else if (value instanceof JSONArray) {
            put(key, (JSONArray) value);
        } else if (value instanceof JSONObject) {
            put(key, (JSONObject) value);
        } else {
            put(key, String.valueOf(value));
        }
        return this;
    }

    /**
     * optional put a value at a key as long as both they key and value are not null
     * otherwise it does nothing
     * @param key the key element to operate on
     * @param value
     * @return this JSONObject
     */
    public JSONObject putOpt(String key, Object value) {
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
     * get object as a map
     */
    public Map<String, Object> toMap() {
        return Json.toMap(obj);
    }

    /**
     * get the key names as a JSONArray
     * @return a JSONArray of keys
     */
    public JSONArray names() {
        return new JSONArray(keySet());
    }

    /**
     * query the object graph using JSONPointer
     * https://tools.ietf.org/html/rfc6901
     *
     * @return the thing you asked for
     */
    public Object query(String query) {
        JSONPointer pointer = JSONPointer.compile(query);
        return pointer.queryFrom(this);
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
}
