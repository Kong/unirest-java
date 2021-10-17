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

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import kong.unirest.json.EngineElement;
import kong.unirest.json.EngineObject;

import java.util.Set;

public class GsonObject extends GsonElement<JsonObject> implements EngineObject {
    GsonObject(JsonObject element) {
        super(element);
    }

    public GsonObject() {
        this(new JsonObject());
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
    public EngineElement get(String key) {
        return GsonEngine.toElement(element.get(key));
    }

    @Override
    public void add(String key, EngineElement value) {
        element.add(key, value.getEngineElement());
    }

    @Override
    public void addProperty(String key, Boolean value) {
        element.addProperty(key, value);
    }

    @Override
    public void addProperty(String key, String value) {
        element.addProperty(key, value);
    }

    @Override
    public void addProperty(String key, Number value) {
        element.addProperty(key, value);
    }

    @Override
    public void addProperty(String key, EngineElement value) {
//        element.addProperty(key, value.getEngineElement());
    }

    @Override
    public void remove(String key) {
        element.remove(key);
    }

    @Override
    public <E extends Enum> void add(String key, E enumvalue) {
        element.add(key, enumvalue == null ? JsonNull.INSTANCE : new JsonPrimitive(enumvalue.name()));
    }

    @Override
    public Set<String> keySet() {
        return element.keySet();
    }
}
