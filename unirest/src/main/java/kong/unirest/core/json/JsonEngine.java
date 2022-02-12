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

import kong.unirest.core.ObjectMapper;

import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

public interface JsonEngine {
    String toPrettyJson(Element obj);
    String toJson(Element obj);
    void toJson(Element obj, Writer sw);
    void toPrettyJson(Element engineElement, Writer sw);
    Element toJsonTree(java.lang.Object obj);
    Object newEngineObject();
    Object newEngineObject(String string) throws JSONException;
    Array newJsonArray(String jsonString) throws JSONException;
    Array newJsonArray(Collection<?> collection);
    Array newEngineArray();
    <T> T fromJson(Element obj, Class<T> mapClass);
    <T extends Enum> Primitive newJsonPrimitive(T enumValue);
    Primitive newJsonPrimitive(String string);
    Primitive newJsonPrimitive(Number number);
    Primitive newJsonPrimitive(Boolean bool);
    ObjectMapper getObjectMapper();
    String quote(java.lang.Object s);

    interface Element {
        Object getAsJsonObject();
        boolean isJsonNull();
        Primitive getAsJsonPrimitive();
        Array getAsJsonArray();
        float getAsFloat();
        double getAsDouble();
        String getAsString();
        long getAsLong();
        int getAsInt();
        boolean getAsBoolean();
        BigInteger getAsBigInteger();
        BigDecimal getAsBigDecimal();
        Primitive getAsPrimitive();
        boolean isJsonArray();
        boolean isJsonPrimitive();
        boolean isJsonObject();
        <T> T getEngineElement();
    }

    interface Array extends Element {
        int size();
        Element get(int index);
        Element remove(int index);
        Element put(int index, Number number);
        Element put(int index, String number);
        Element put(int index, Boolean number);
        void add(Element obj);
        void set(int index, Element o);
        void add(Number num);
        void add(String str);
        void add(Boolean bool);
        String join(String token);
    }

    interface Object extends Element {
        int size();
        boolean has(String key);
        Element get(String key);
        void add(String key, Element value);
        void addProperty(String key, Boolean value);
        void addProperty(String key, String value);
        void addProperty(String key, Number value);
        void addProperty(String key, Element value);
        void remove(String key);
        <E extends Enum> void add(String key, E enumValue);
        Set<String> keySet();
    }

    interface Primitive extends Element {
        boolean isBoolean();
        boolean isNumber();
    }
}
