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

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JSONArray implements Iterable<Object> {

    private transient final JsonArray obj;

    public JSONArray(String jsonString) {
        obj = Json.fromJson(jsonString, JsonArray.class);
    }

    public JSONArray() {
        obj = new JsonArray();
    }

    JSONArray(JsonArray array) {
        obj = array;
    }

    JSONArray(JsonElement jsonElement) {
        obj = jsonElement.getAsJsonArray();
    }

    public JSONArray(Collection<?> collection) {
        Collection pre = collection.stream().map(this::wrap).collect(Collectors.toList());
        obj = Json.toJsonArray(pre);
    }

    public JSONArray(Object array) {
        if(array == null || !array.getClass().isArray()){
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
        }
        Collection pre = new ArrayList();
        for(Object o : (Object[])array){
            pre.add(wrap(o));
        }
        this.obj = Json.toJsonArray(pre);
    }

    private Object wrap(Object o) {
        if(o instanceof Iterable){
            return StreamSupport.stream(((Iterable)o).spliterator(), false)
                    .collect(Collectors.toList());
        }
        return o;
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

    public int length() {
        return obj.size();
    }

    public JSONObject getJSONObject(int index) {
        try {
            return new JSONObject(getElement(index));
        } catch (IllegalStateException e) {
            throw new JSONException("JSONArray[%s] is not a JSONObject.", index);
        }
    }

    public void put(Number num) {
        obj.add(num);
    }

    public void put(Boolean bool){
        obj.add(bool);
    }

    public void put(Object object) {
        if(object == null){
            obj.add(JsonNull.INSTANCE);
        } else if (object instanceof Number){
            put((Number)object);
        } else if (object instanceof Boolean) {
            put((Boolean) object);
        }else if (object instanceof JSONObject){
            put((JSONObject)object);
        } else if (object instanceof JSONArray){
            put((JSONArray)object);
        } else {
            put(String.valueOf(object));
        }

    }

    public void put(String str) {
        obj.add(str);
    }

    public void put(Map map){
        obj.add(Json.toJsonObject(map));
    }

    public void put(Collection collection){
        obj.add(Json.toJsonArray(collection));
    }

    private JsonElement getElement(int index) {
        try {
            return obj.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new JSONException("JSONArray[%s] not found.", index);
        }
    }

    public Double getDouble(int index) {
        return tryNumber(() -> getElement(index).getAsDouble(), index);
    }

    public Double optDouble(int index) {
        return optDouble(index, Double.NaN);
    }

    public double optDouble(int index, double defaultValue) {
        return getOrDefault(() -> getDouble(index), defaultValue);
    }

    public Float getFloat(int index) {
        return tryNumber(() -> getElement(index).getAsFloat(), index);
    }

    public Float optFloat(int index) {
        return optFloat(index, Float.NaN);
    }

    public Float optFloat(int index, float defaultValue) {
        return getOrDefault(() -> getFloat(index), defaultValue);
    }

    public long getLong(int index) {
        return tryNumber(() -> getElement(index).getAsLong(), index);
    }

    public long optLong(int index) {
        return optLong(index, 0L);
    }

    public long optLong(int index, long defaultValue) {
        return getOrDefault(() -> getLong(index), defaultValue);
    }

    public int getInt(int index) {
        return tryNumber(() -> getElement(index).getAsInt(), index);
    }

    public int optInt(int index, int defaultValue) {
        return getOrDefault(() -> getInt(index), defaultValue);
    }

    public int optInt(int index) {
        return optInt(index, 0);
    }

    public BigInteger getBigInteger(int index) {
        return tryNumber(() -> getElement(index).getAsBigInteger(), index);
    }

    public BigInteger optBigInteger(int index, BigInteger defaultValue) {
        return getOrDefault(() -> getBigInteger(index), defaultValue);
    }

    public BigDecimal getBigDecimal(int index) {
        return tryNumber(() -> getElement(index).getAsBigDecimal(), index);
    }

    public BigDecimal optBigDecimal(int index, BigDecimal defaultValue) {
        return getOrDefault(() -> getBigDecimal(index), defaultValue);
    }

    public String getString(int index) {
        return getElement(index).getAsString();
    }

    public String optString(int index, String defaultValue) {
        return getOrDefault(() -> getString(index), defaultValue);
    }

    public String optString(int index) {
        return optString(index, "");
    }

    public void put(JSONObject object) {
        obj.add(object.asElement());
    }

    public JSONObject optJSONObject(int index) {
        return getOrDefault(() -> new JSONObject(getElement(index).getAsJsonObject()), null);
    }

    public void put(JSONArray array) {
        obj.add(array.obj);
    }

    public JSONArray getJSONArray(int index) {
        try {
            return new JSONArray(getElement(index).getAsJsonArray());
        } catch (IllegalStateException e) {
            throw new JSONException("JSONArray[%s] is not a JSONArray.", index);
        }
    }

    public JSONArray optJSONArray(int index) {
        return getOrDefault(() -> getJSONArray(index), null);
    }

    @Override
    public String toString() {
        return Json.toJson(obj);
    }

    public String toString(int indent) {
        return Json.toPrettyJson(obj);
    }

    public <T extends Enum> void put(T enumValues) {
        obj.add(enumValues.name());
    }

    public <T extends Enum<T>> T getEnum(Class<T> enumClass, int index) {

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

    public String join(String token) {
        return StreamSupport.stream(obj.spliterator(), false)
                .map(String::valueOf)
                .collect(Collectors.joining(token));
    }

    @Override
    public Iterator<Object> iterator() {
        return (Iterator)toList().iterator();
    }

    public Object get(int index) {
        return  new ToElementMapper().apply(obj.get(index));
    }

    public boolean equals(Object o) {
        return o == this || o instanceof JsonArray && ((JSONArray)o).obj.equals(this.obj);
    }

    public int hashCode() {
        return this.obj.hashCode();
    }

    public void write(StringWriter sw) {
        Json.write(obj, sw);
    }

    public void write(StringWriter sw, int index, int i1) {
        Json.writePretty(obj, sw);
    }

    public void remove(int index) {
        obj.remove(index);
    }

    public void put(int index, Number number) {
        put(index, number == null ? JsonNull.INSTANCE : new JsonPrimitive(number));
    }

    public void put(int index, String string) {
        put(index, string == null ? JsonNull.INSTANCE : new JsonPrimitive(string));
    }

    public void put(int index, Map map) {
        put(index, Json.toJsonObject(map));
    }

    public void put(int index, Collection map) {
        put(index, Json.toJsonArray(map));
    }

    public <T extends Enum> void put(int index, T e) {
        put(index, e == null ? null : e.name());
    }

    private void put(int index, JsonElement o){
        while(obj.size() < index + 1){
            obj.add(JsonNull.INSTANCE);
        }
        if(index < obj.size()){
            obj.set(index, o);
        } else if (index == obj.size()){
            obj.add(o);
        }
    }

    public boolean similar(Object o) {
        if(!(o instanceof JSONArray)){
            return false;
        }
        JSONArray cst = (JSONArray)o;
        return this.obj.equals(cst.obj);
    }

    public Object query(String pattern) {
        return 0;
    }

    public List toList() {
        List list = new ArrayList();
        for(int i = 0; i < obj.size(); i++){
            list.add(get(i));
        }
        return list;
    }

    public boolean isNull(int index) {
        return index >= obj.size() || obj.get(index).isJsonNull();
    }

    JsonArray getArray(){
        return obj;
    }
}
