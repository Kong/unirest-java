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

import com.google.gson.*;

import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class JSONElement {
    protected static transient final ToObjectMapper MAPPER = new ToObjectMapper();
    private static transient final Gson GSON = CoreFactory.getCore().create();
    private static transient final Gson PRETTY_GSON = CoreFactory.getCore().setPrettyPrinting().create();

    private final JsonElement element;

    protected JSONElement(JsonElement e){
        this.element = e;
    }

    /**
     * Write the JSON to a Writer
     * @param sw the writer
     * @return the same Writer
     * @throws JSONException for IO problems
     */
    public Writer write(Writer sw) throws JSONException {
        write(element, sw);
        return sw;
    }

    /**
     * Write the JSON to a Writer with a pretty format
     * due to limitations in GSON the index and indent are currently ignored
     * @param sw the writer
     * @param indentFactor currently ignored
     * @param indent currently ignored
     * @return the same Writer
     * @throws JSONException for IO problems
     */
    public Writer write(Writer sw, int indentFactor, int indent) throws JSONException {
        writePretty(element, sw);
        return sw;
    }

    /**
     * query the object graph using JSONPointer
     * https://tools.ietf.org/html/rfc6901
     *
     * @param query the pointer to get
     * @return the thing you asked for
     */
    public Object query(String query) {
        return query(JSONPointer.compile(query));
    }

    /**
     * query the object graph using JSONPointer
     * https://tools.ietf.org/html/rfc6901
     *
     * @param query the pointer to get
     * @return the thing you asked for
     */
    public Object query(JSONPointer query) {
        return query.queryFrom(this);
    }

    public Object optQuery(String query){
        try{
            return query(query);
        } catch (Exception e){
            return null;
        }
    }

    public Object optQuery(JSONPointer query){
        try{
            return query.queryFrom(this);
        } catch (Exception e){
            return null;
        }
    }

    JsonElement getElement() {
        return element;
    }

    static JsonObject toJsonObject(Map map){
        return JSONElement.toTree(map).getAsJsonObject();
    }

    static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return GSON.fromJson(json, classOfT);
        }catch (JsonSyntaxException e){
            throw new JSONException("Invalid JSON");
        }
    }

    static String toJson(Object collection) {
        return GSON.toJson(collection);
    }

    static JsonElement toTree(Object obj){
        return GSON.toJsonTree(obj);
    }

    static void write(JsonElement obj, Writer sw) {
        GSON.toJson(obj, sw);
    }

    static Object unwrap(Object o) {
        if(o instanceof Iterable){
            return StreamSupport.stream(((Iterable)o).spliterator(), false)
                    .map(JSONElement::unwrapObject)
                    .collect(Collectors.toList());
        }
        return unwrapObject(o);
    }

    static Object unwrapObject(Object o){
        if(o instanceof JSONElement){
            return ((JSONElement)o).getElement();
        }
        return o;
    }

    static void writePretty(JsonElement obj, Writer sw) {
        PRETTY_GSON.toJson(obj, sw);
    }

    static JsonArray toJsonArray(Collection collection) {
        return fromJson(toJson(collection), JsonArray.class);
    }

    static String toPrettyJson(JsonElement obj) {
        return PRETTY_GSON.toJson(obj);
    }

    static Map<String, Object> toMap(JsonObject obj) {
        return GSON.fromJson(obj, Map.class);
    }
}
