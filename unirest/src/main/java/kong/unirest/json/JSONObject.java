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

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class JSONObject {
    private transient final JsonObject obj;

    JSONObject(JsonElement jsonElement) {
        this.obj = jsonElement.getAsJsonObject();
    }

    public JSONObject(String string) {
        this(Json.fromJson(string, JsonObject.class));
    }

    public JSONObject(Map<String, Object> map) {
        obj = Json.fromJson(Json.toJson(map), JsonObject.class);
    }

    public JSONObject() {
        this.obj = new JsonObject();
    }

    public String getString(String key) {
        return getProperty(key).getAsString();
    }

    public int getInt(String key) {
        return tryNumber(() -> getProperty(key).getAsInt(), key);
    }

    private static transient final ToObjectMapper MAPPER = new ToObjectMapper();

    public Object get(String key) {
        return MAPPER.apply(getProperty(key));
    }


    JsonElement asElement() {
        return obj;
    }

    @Override
    public String toString() {
        return Json.toJson(obj);
    }

    public boolean similar(Object o) {
        if (!(o instanceof JSONObject)) {
            return false;
        }
        JSONObject cst = (JSONObject) o;
        return this.obj.equals(cst.obj);
    }

    public boolean has(String key) {
        return this.obj.has(key);
    }

    public int length() {
        return this.obj.size();
    }

    public JSONObject getJSONObject(String key) {
        try {
            return new JSONObject(getProperty(key).getAsJsonObject());
        } catch (IllegalStateException e) {
            throw new JSONException("JSONObject[\"%s\"] is not a JSONObject.", key);
        }
    }

    public void put(String key, Number value) {
        this.obj.addProperty(key, value);
    }

    public void put(String key, String value) {
        this.obj.addProperty(key, value);
    }

    public double getDouble(String key) {
        return tryNumber(() -> getProperty(key).getAsDouble(), key);
    }

    public double optDouble(String key) {
        return optDouble(key, Double.NaN);
    }

    public double optDouble(String key, double defaultValue) {
        return getOrDefault(() -> getDouble(key), defaultValue);
    }

    private <T> T getOrDefault(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return defaultValue;
        }
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

    public float getFloat(String key) {
        return tryNumber(() -> getProperty(key).getAsFloat(), key);
    }

    public float optFloat(String key) {
        return optFloat(key, Float.NaN);
    }

    public float optFloat(String key, float defaultValue) {
        return getOrDefault(() -> getFloat(key), defaultValue);
    }

    public long getLong(String key) {
        return tryNumber(() -> getProperty(key).getAsLong(), key);
    }

    public long optLong(String key) {
        return optLong(key, 0L);
    }

    public long optLong(String key, long defaultValue) {
        return getOrDefault(() -> getLong(key), defaultValue);
    }

    public int optInt(String key, int defaultValue) {
        return getOrDefault(() -> getInt(key), defaultValue);
    }

    public int optInt(String key) {
        return optInt(key, 0);
    }

    public BigInteger getBigInteger(String key) {
        return tryNumber(() -> getProperty(key).getAsBigInteger(), key);
    }

    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        return getOrDefault(() -> getBigInteger(key), defaultValue);
    }

    public BigDecimal getBigDecimal(String key) {
        return tryNumber(() -> getProperty(key).getAsBigDecimal(), key);
    }

    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        return getOrDefault(() -> getBigDecimal(key), defaultValue);
    }

    public String optString(String key, String defaultValue) {
        return getOrDefault(() -> getString(key), defaultValue);
    }

    public String optString(String key) {
        return optString(key, "");
    }

    public void put(String key, JSONObject object) {
        obj.add(key, object.obj);
    }

    public JSONObject optJSONObject(String key) {
        return getOrDefault(() -> getJSONObject(key), null);
    }

    public void put(String key, JSONArray array) {
        obj.add(key, array.getArray());
    }

    public JSONArray getJSONArray(String key) {
        try {
            return new JSONArray(getProperty(key).getAsJsonArray());
        } catch (IllegalStateException e) {
            throw new JSONException("JSONObject[\"%s\"] is not a JSONArray.", key);
        }
    }

    public JSONArray optJSONArray(String key) {
        return getOrDefault(() -> getJSONArray(key), null);
    }

    public <T extends Enum<T>> void put(String key, T enumvalue) {
        obj.add(key, enumvalue == null ? JsonNull.INSTANCE : new JsonPrimitive(enumvalue.name()));
    }

    public <T extends Enum<T>> T getEnum(Class<T> enumClass, String key) {
        try {
            String v = getProperty(key).getAsString();
            return Enum.valueOf(enumClass, v);
        } catch (IllegalArgumentException e) {
            throw new JSONException("JSONObject[\"%s\"] is not an enum of type \"%s\".", key, enumClass.getSimpleName());
        }
    }

    public <T extends Enum<T>> T optEnum(Class<T> enumClass, String key, T defaultValue) {
        return getOrDefault(() -> getEnum(enumClass, key), defaultValue);
    }

    public <T extends Enum<T>> T optEnum(Class<T> enumClass, String key) {
        return optEnum(enumClass, key, null);
    }

    public String toString(int i) {
        return Json.toPrettyJson(obj);
    }

    public void write(StringWriter sw) {
        Json.write(obj, sw);
    }

    public void write(StringWriter sw, int index, int i1) {
        Json.writePretty(obj, sw);
    }

    public void remove(String key) {
        obj.remove(key);
    }

    public void accumulate(String key, Object additionalValue) {
        requireNonNull(key, "Null key.");
        if (!obj.has(key)) {
            return;
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
    }

    public void append(String key, Object value) {
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
    }

    public void increment(String key) {
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
    }

    public void putOnce(String key, Object value) {
        if(has(key)){
            throw new JSONException("Duplicate key \"foo\"");
        }
        put(key, value);
    }

    public void put(String key, Object value){
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

    }

    public void put(String key, Boolean value){
        obj.addProperty(key, value);
    }

    public void putOpt(String key, Object value) {
        if(key == null || value == null){
            return;
        }
        put(key, value);
    }

    public Set<String> keySet() {
        return obj.keySet();
    }

    public Iterator<String> keys() {
        return obj.keySet().iterator();
    }

    public Map<String, Object> toMap() {
        return Json.toMap(obj);
    }

    public JSONArray names() {
        return new JSONArray(keySet());
    }

    public Object query(String query) {
        JSONPointer pointer = JSONPointer.compile(query);
        return pointer.queryFrom(this);
    }
}
