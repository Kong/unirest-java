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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.*;
import kong.unirest.ObjectMapper;
import kong.unirest.UnirestException;
import kong.unirest.json.*;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Collection;

public class JacksonEngine implements JsonEngine {
    private com.fasterxml.jackson.databind.ObjectMapper om;
    private ObjectMapper objm;

    public JacksonEngine(){
        objm = new JacksonObjectMapper();
        om = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
                .build();
    }

    @Override
    public String toPrettyJson(Element obj) {
        try {
            return om.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj.getEngineElement());
        } catch (JsonProcessingException e) {
            throw new UnirestException(e);
        }
    }

    @Override
    public String toJson(Element obj) {
        try {
            return om.writeValueAsString(obj.getEngineElement());
        } catch (JsonProcessingException e) {
            throw new UnirestException(e);
        }
    }

    @Override
    public void toJson(Element obj, Writer sw) {
        try {
            om.writeValue(sw, obj.getEngineElement());
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    @Override
    public void toPrettyJson(Element obj, Writer sw) {
        try {
            om.writerWithDefaultPrettyPrinter()
                    .writeValue(sw, obj.getEngineElement());
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    @Override
    public Element toJsonTree(java.lang.Object obj) {
        return JacksonElement.wrap(om.convertValue(obj, JsonNode.class));
    }

    @Override
    public Object newEngineObject() {
        return new JacksonObject(om.createObjectNode());
    }

    @Override
    public Object newEngineObject(String string) throws JSONException {
        try {
            return new JacksonObject(om.readValue(string, ObjectNode.class));
        } catch (JsonProcessingException e) {
            throw new JSONException("Invalid JSON");
        }
    }

    @Override
    public Array newJsonArray(String jsonString) throws JSONException {
        try {
            return new JacksonArray(om.readValue(jsonString, ArrayNode.class));
        } catch (JsonProcessingException e) {
            throw new JSONException("Invalid JSON");
        }
    }

    @Override
    public Array newJsonArray(Collection<?> collection) {
            JacksonArray a = new JacksonArray(om.createArrayNode());
            for(java.lang.Object o : collection){
                add(a, o);
            }
            return a;
    }

    private void add(JacksonArray a, java.lang.Object o) {
        if(o instanceof Number){
            a.add((Number) o);
        } else if (o instanceof String) {
            a.add((String) o);
        }else if (o instanceof Boolean) {
            a.add((Boolean) o);
        }else if(o instanceof JSONElement) {
            a.add(((JSONElement) o).getElement());
        } else if(o instanceof Element) {
            a.add((Element) o);
        } else {
            JsonNode tree = om.convertValue(o, JsonNode.class);
            a.add(JacksonElement.wrap(tree));
        }
    }

    @Override
    public Array newEngineArray() {
        return new JacksonArray(om.createArrayNode());
    }

    @Override
    public <T> T fromJson(Element obj, Class<T> mapClass) {
        return om.convertValue(obj.getEngineElement(), mapClass);
    }

    @Override
    public <T extends Enum> Primitive newJsonPrimitive(T enumValue) {
            if (enumValue == null){
                return new JacksonPrimitive(NullNode.getInstance());
            }
            return newJsonPrimitive(enumValue.name());
    }

    @Override
    public Primitive newJsonPrimitive(String string) {
        return convert(string, v -> new TextNode(v));
    }

    @Override
    public Primitive newJsonPrimitive(Number number) {
        if(number instanceof Integer) {
            return convert((Integer) number, IntNode::new);
        }else if (number instanceof Long){
            return convert((Long)number, LongNode::new);
        } else if (number instanceof Double){
            return convert((Double)number, DoubleNode::new);
        } else if (number instanceof BigInteger) {
            return convert((BigInteger)number, BigIntegerNode::new);
        } else if (number instanceof Float){
            return convert((Float)number, FloatNode::new);
        }
        return new JacksonPrimitive(NullNode.getInstance());
    }

    @Override
    public Primitive newJsonPrimitive(Boolean bool) {
        return convert(bool, v -> BooleanNode.valueOf(v));
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objm;
    }

    @Override
    public String quote(java.lang.Object s) {
        try {
            return om.writeValueAsString(s);
        } catch (JsonProcessingException e) {
            throw new JSONException(e);
        }
    }

    @FunctionalInterface
    private interface ValueSupplier<V> {
        ValueNode getIt(V value) throws JsonProcessingException;
    }

    private <T> Primitive convert(T value, ValueSupplier<T> supplier){
        try {
            if (value == null){
                return new JacksonPrimitive(NullNode.getInstance());
            }
            return new JacksonPrimitive(supplier.getIt(value));
        } catch (JsonProcessingException e) {
            throw new UnirestException(e);
        }
    }
}
