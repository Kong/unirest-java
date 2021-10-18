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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

class ToObjectMapper {
    private static Map<Predicate<EngineElement>, Function<EngineElement, Object>> mappers;

    static {
        mappers = new HashMap<>();
        mappers.put(j -> j == null || j.isJsonNull(), j -> null);
        mappers.put(EngineElement::isJsonArray, JSONArray::new);
        mappers.put(EngineElement::isJsonObject, JSONObject::new);
        mappers.put(EngineElement::isJsonPrimitive, j -> mapPrimative(j.getAsJsonPrimitive()));
    }

    private static Object mapPrimative(EnginePrimitive e) {
        if(e.isBoolean()){
            return e.getAsBoolean();
        } else if (e.isNumber()){
            String s = e.getAsString();
            if(s.contains(".")){
                return e.getAsDouble();
            } else if (e.getAsInt() == e.getAsLong()){
                return e.getAsInt();
            }
            return e.getAsLong();
        } else if (e.isJsonNull()){
            return null;
        }
        return e.getAsString();
    }

    public Object apply(EngineElement e){
        return mappers.entrySet()
                .stream()
                .filter(r -> r.getKey().test(e))
                .map(r -> r.getValue().apply(e))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
