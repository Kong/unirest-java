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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import kong.unirest.json.JsonEngine;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class GsonArray extends GsonElement<JsonArray> implements JsonEngine.Array {
    GsonArray(JsonArray jsonElement) {
        super(jsonElement);
    }

    GsonArray() {
        this(new JsonArray());
    }

    @Override
    public int size() {
        return element.size();
    }

    @Override
    public JsonEngine.Element get(int index) {
        return GsonEngine.toElement(element.get(index));
    }

    @Override
    public void add(JsonEngine.Element obj) {
        if(obj == null){
            element.add(JsonNull.INSTANCE);
        } else {
            element.add((JsonElement) obj.getEngineElement());
        }
    }

    @Override
    public void set(int index, JsonEngine.Element o) {
        element.set(index, o == null ? JsonNull.INSTANCE : o.getEngineElement());
    }

    @Override
    public void add(Number num) {
        element.add(num);
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

    @Override
    public JsonEngine.Array put(int index, Number number) {
        element.set(index, new JsonPrimitive(number));
        return this;
    }

    @Override
    public JsonEngine.Element put(int index, String str) {
        element.set(index, new JsonPrimitive(str));
        return this;
    }

    @Override
    public JsonEngine.Element put(int index, Boolean number) {
        element.set(index, new JsonPrimitive(number));
        return this;
    }


    @Override
    public JsonEngine.Element remove(int index) {
        return GsonEngine.toElement(element.remove(index));
    }



}
