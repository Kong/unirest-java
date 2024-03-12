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

package kong.unirest.mappers.jackson;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kong.unirest.core.json.JsonEngine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class JacksonObject extends JacksonElement<ObjectNode> implements JsonEngine.Object {

    public  JacksonObject(ObjectNode element) {
        super(element);
    }

    @Override
    public int size() {
        return element.size();
    }

    @Override
    public boolean has(String key) {
        return element.has(key);
    }

    @Override
    public JsonEngine.Element get(String key) {
        return wrap(element.get(key));
    }

    @Override
    public void add(String key, JsonEngine.Element value) {
        element.set(key, value.getEngineElement());
    }

    @Override
    public void addProperty(String key, Boolean value) {
        element.put(key, value);
    }

    @Override
    public void addProperty(String key, String value) {
        element.put(key, value);
    }

    @Override
    public void addProperty(String key, Number number) {
        if(number instanceof Integer){
            element.put(key, (Integer) number);
        } else if (number instanceof Double){
            element.put(key, (Double)number);
        } else if (number instanceof BigInteger) {
            element.put(key, (BigInteger) number);
        } else if (number instanceof Float){
            element.put(key, (Float)number);
        } else if(number instanceof BigDecimal) {
            element.put(key, (BigDecimal) number);
        } else if (number instanceof Long) {
            element.put(key, (Long)number);
        }
    }

    @Override
    public void addProperty(String key, JsonEngine.Element value) {
        element.set(key, value.getEngineElement());
    }

    @Override
    public void remove(String key) {
        element.remove(key);
    }

    @Override
    public <E extends Enum> void add(String key, E enumValue) {
        if(enumValue == null){
            element.set(key, NullNode.getInstance());
        } else {
            element.put(key, enumValue.name());
        }
    }

    @Override
    public Set<String> keySet() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        element.fieldNames(),
                        Spliterator.ORDERED)
                , false)
                .collect(Collectors.toSet());

    }
}
