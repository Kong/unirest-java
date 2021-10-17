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

import kong.unirest.ObjectMapper;

import java.io.Writer;
import java.util.Collection;

public interface JsonEngine {
    String toPrettyJson(Object obj);
    String toJson(Object obj);
    void toJson(EngineElement obj, Writer sw);
    void toPrettyJson(EngineElement engineElement, Writer sw);

    EngineElement toJsonTree(Object obj);

    EngineObject newEngineObject();

    EngineObject newEngineObject(String string) throws JSONException;

    EngineArray newJsonArray(String jsonString) throws JSONException;

    EngineArray newJsonArray(Collection<?> collection);

    EngineArray newEngineArray();

    <T> T fromJson(EngineElement obj, Class<T> mapClass);

    <T extends Enum> EnginePrimitive newJsonPrimitive(T enumValue);

    EnginePrimitive newJsonPrimitive(String string);
    EnginePrimitive newJsonPrimitive(Number number);
    EnginePrimitive newJsonPrimitive(Boolean bool);

    ObjectMapper getObjectMapper();
}
