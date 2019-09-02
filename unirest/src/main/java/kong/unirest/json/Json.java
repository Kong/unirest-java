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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

public class Json {
    private static final Gson GSON = new Gson();
    private static final Gson PRETTY_GSON = new Gson().newBuilder().setPrettyPrinting().create();

    public static JsonObject toJsonObject(Map map){
        return fromJson(Json.toJson(map), JsonObject.class);
    }
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static String toJson(Object collection) {
        return GSON.toJson(collection);
    }

    public static void write(JsonElement obj, StringWriter sw) {
        GSON.toJson(obj, sw);
    }

    public static void writePretty(JsonElement obj, StringWriter sw) {
        PRETTY_GSON.toJson(obj, sw);
    }

    public static JsonArray toJsonArray(Collection collection) {
        return fromJson(Json.toJson(collection), JsonArray.class);
    }

    public static JsonArray toJsonArray(Object collection) {
        return fromJson(Json.toJson(collection), JsonArray.class);
    }

    public static String toPrettyJson(JsonElement obj) {
        return PRETTY_GSON.toJson(obj);
    }
}
