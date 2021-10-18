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

import java.io.Writer;
import java.util.Map;

public abstract class JSONElement {
    protected static transient final ToObjectMapper MAPPER = new ToObjectMapper();
    private static transient final JsonEngine ENGINE = CoreFactory.getCore();

    private final EngineElement element;

    protected JSONElement(EngineElement e){
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
        ENGINE.toPrettyJson(element, sw);
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

    public EngineElement getElement() {
        return element;
    }

    static EngineObject toJsonObject(Map map){
        return JSONElement.toTree(map).getAsJsonObject();
    }

    static String toJson(EngineElement collection) {
        return ENGINE.toJson(collection);
    }

    static EngineElement toTree(Object obj){
        return ENGINE.toJsonTree(obj);
    }

    static void write(EngineElement obj, Writer sw) {
        ENGINE.toJson(obj, sw);
    }

    protected static String toPrettyJson(EngineElement obj) {
        return ENGINE.toPrettyJson(obj);
    }

    protected static Map<String, Object> toMap(EngineElement obj) {
        return ENGINE.fromJson(obj, Map.class);
    }
}
