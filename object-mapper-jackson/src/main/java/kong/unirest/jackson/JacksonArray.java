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
import kong.unirest.core.json.JsonEngine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class JacksonArray extends JacksonElement<ArrayNode> implements JsonEngine.Array {
    JacksonArray(ArrayNode element) {
        super(element);
    }

    @Override
    public int size() {
        return element.size();
    }

    @Override
    public JsonEngine.Element get(int index) {
        validateIndex(index);
        return wrap(element.get(index));
    }

    private void validateIndex(int index) {
        if(element.size() < index +1){
            throw new IndexOutOfBoundsException();
        }
    }


    @Override
    public JsonEngine.Element remove(int index) {
        return wrap(element.remove(index));
    }

    @Override
    public JsonEngine.Element put(int index, Number number) {
        if(number instanceof Integer){
            element.insert(index, (Integer) number);
        } else if (number instanceof Double){
            element.insert(index, (Double)number);
        } else if (number instanceof BigInteger) {
            element.insert(index, (BigInteger) number);
        } else if (number instanceof Float){
            element.insert(index, (Float)number);
        } else if(number instanceof BigDecimal) {
            element.insert(index, (BigDecimal) number);
        }
        return this;
    }

    @Override
    public JsonEngine.Element put(int index, String value) {
        element.insert(index, value);
        return this;
    }

    @Override
    public JsonEngine.Element put(int index, Boolean value) {
        element.insert(index, value);
        return this;
    }

    @Override
    public void add(JsonEngine.Element obj) {
        if(obj == null){
            element.add(NullNode.getInstance());
            return;
        }
        element.add((JsonNode) obj.getEngineElement());
    }

    @Override
    public void set(int index, JsonEngine.Element o) {
        if(o == null){
            element.set(index, NullNode.getInstance());
        } else {
            element.set(index, (JsonNode)o.getEngineElement());
        }
    }

    @Override
    public void add(Number number) {
        if(number instanceof Integer){
            element.add((Integer) number);
        } else if (number instanceof Double){
            element.add((Double)number);
        } else if (number instanceof Long){
            element.add((Long)number);
        } else if (number instanceof BigInteger) {
            element.add((BigInteger) number);
        } else if (number instanceof Float){
            element.add((Float)number);
        } else if(number instanceof BigDecimal) {
            element.add((BigDecimal) number);
        }
    }

    @Override
    public void add(String str) {
        element.add(str);
    }

    @Override
    public void add(Boolean bool) {
        element.add(bool);
    }

    @Override
    public String join(String token) {
        return StreamSupport.stream(element.spliterator(), false)
                .map(String::valueOf)
                .collect(Collectors.joining(token));
    }
}
