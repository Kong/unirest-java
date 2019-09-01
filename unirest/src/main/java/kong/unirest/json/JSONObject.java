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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class JSONObject {
    private transient final JsonObject obj;

    JSONObject(JsonElement jsonElement) {
        this.obj = jsonElement.getAsJsonObject();
    }

    public JSONObject(String string) {
        this(Json.fromJson(string, JsonObject.class));
    }

    public JSONObject(Map<String, Object> map) {
        obj = Json.fromJson(Json.toJson(map), JsonObject.class);
    }

    public String getString(String key) {
        return getElement(key).getAsString();
    }

    public int getInt(String key) {
        return getElement(key).getAsInt();
    }

    public Object get(String key){
        return get(key);
    }

    private JsonElement getElement(String key) {
        return obj.get(key);
    }

    JsonElement asElement() {
        return obj;
    }

    @Override
    public String toString() {
        return Json.toJson(obj);
    }

    public boolean similar(Object o) {
        if(!(o instanceof JSONObject)){
            return false;
        }
        JSONObject cst = (JSONObject)o;
        return this.obj.equals(cst.obj);
    }
}
