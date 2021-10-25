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

package kong.unirest.gson;

import com.google.gson.*;
import kong.unirest.json.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

class GsonElement<T extends JsonElement> implements JsonEngine.Element {
    protected T element;

    GsonElement(T element) {
        this.element = element;
    }

    @Override
    public JsonEngine.Object getAsJsonObject() {
        return new GsonObject(element.getAsJsonObject());
    }

    @Override
    public boolean isJsonNull() {
        return element.isJsonNull();
    }

    @Override
    public JsonEngine.Primitive getAsJsonPrimitive() {
        return new GsonPrimitive(element.getAsJsonPrimitive());
    }

    @Override
    public JsonEngine.Array getAsJsonArray() {
        return new GsonArray(element.getAsJsonArray());
    }

    @Override
    public float getAsFloat() {
        return element.getAsFloat();
    }

    @Override
    public double getAsDouble() {
        return element.getAsDouble();
    }

    @Override
    public String getAsString() {
        return element.getAsString();
    }

    @Override
    public long getAsLong() {
        return element.getAsLong();
    }

    @Override
    public int getAsInt() {
        return element.getAsInt();
    }

    @Override
    public boolean getAsBoolean() {
        return element.getAsBoolean();
    }

    @Override
    public BigInteger getAsBigInteger() {
        return element.getAsBigInteger();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return element.getAsBigDecimal();
    }

    @Override
    public boolean isJsonPrimitive() {
        return element.isJsonPrimitive();
    }

    @Override
    public JsonEngine.Primitive getAsPrimitive() {
        return new GsonPrimitive(element.getAsJsonPrimitive());
    }

    @Override
    public boolean isJsonArray() {
        return element.isJsonArray();
    }

    @Override
    public boolean isJsonObject() {
        return element.isJsonObject();
    }

    @Override
    public <T> T getEngineElement() {
        return (T)element;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        GsonElement<?> that = (GsonElement<?>) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }
}
