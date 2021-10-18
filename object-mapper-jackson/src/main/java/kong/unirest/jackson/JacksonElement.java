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


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import kong.unirest.json.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

class JacksonElement<T extends JsonNode> implements EngineElement {
    protected T element;

    JacksonElement(T element){
        this.element = element;
    }

    static EngineElement wrap(JsonNode node) {
        if(node == null || node.isNull()){
            return new JacksonPrimitive(NullNode.getInstance());
        } else if(node.isArray()){
            return new JacksonArray((ArrayNode) node);
        } else if(node.isObject()){
            return new JacksonObject((ObjectNode)node);
        } else if (node.isValueNode()){
            return new JacksonPrimitive((ValueNode)node);
        }
        return new JacksonPrimitive(NullNode.getInstance());
    }

    @Override
    public EngineObject getAsJsonObject() {
        if(element.isObject()) {
            return new JacksonObject((ObjectNode) element);
        }
        throw new IllegalStateException("Not an object");
    }

    @Override
    public boolean isJsonNull() {
        return element instanceof NullNode;
    }

    @Override
    public EnginePrimitive getAsJsonPrimitive() {
        return new JacksonPrimitive((ValueNode) element);
    }

    @Override
    public EngineArray getAsJsonArray() {
        if(!element.isArray()){
            throw new IllegalStateException("Not an Array");
        }
        return new JacksonArray((ArrayNode)element);
    }

    @Override
    public float getAsFloat() {
        if(!element.isFloat()){
            throw new NumberFormatException("not a float");
        }
        return element.floatValue();
    }

    @Override
    public double getAsDouble() {
        if(!element.isNumber()){
            throw new NumberFormatException("not a double");
        }
        return element.asDouble();
    }

    @Override
    public String getAsString() {
        return element.asText();
    }

    @Override
    public long getAsLong() {
        if(!element.isLong() && !element.isIntegralNumber()){
            throw new NumberFormatException("not a long");
        }
        return element.asLong();
    }

    @Override
    public int getAsInt() {
        if(!element.isIntegralNumber()) {
            throw new NumberFormatException("Not a number");
        }
        return element.asInt();
    }

    @Override
    public boolean getAsBoolean() {
        return element.asBoolean();
    }

    @Override
    public BigInteger getAsBigInteger() {
        if(!element.isIntegralNumber()) {
            throw new NumberFormatException("Not a integer");
        }
        return element.bigIntegerValue();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        if(!element.isNumber()){
            throw new NumberFormatException("Not a decimal");
        }
        return element.decimalValue();
    }

    @Override
    public EnginePrimitive getAsPrimitive() {
        if(element.isValueNode()){
            return new JacksonPrimitive((ValueNode) element);
        }
        throw new JSONException("Not a value type");
    }

    @Override
    public boolean isJsonArray() {
        return element.isArray();
    }

    @Override
    public boolean isJsonPrimitive() {
        return element.isValueNode();
    }

    @Override
    public boolean isJsonObject() {
        return element.isObject();
    }

    @Override
    public <T> T getEngineElement() {
        return (T)element;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        JacksonElement<?> that = (JacksonElement<?>) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }
}
