/*
The MIT License

Copyright OpenUnirest (c) 2018.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package unirest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static unirest.JsonPatchOperation.*;

public class JsonPatch {

    private List<JsonPatchItem> items = new ArrayList<>();

    public JsonPatch(){}

    public JsonPatch(String fromString){
        for (Object row : new JSONArray(fromString)) {
            if(row instanceof JSONObject){
                items.add(new JsonPatchItem((JSONObject)row));
            }
        }
    }

    public void add(String path, Object value) {
        items.add(new JsonPatchItem(add, path, value));
    }

    public void remove(String path) {
        items.add(new JsonPatchItem(remove, path));
    }

    public void replace(String path, Object value) {
        items.add(new JsonPatchItem(replace, path, value));
    }

    public void test(String path, Object value) {
        items.add(new JsonPatchItem(test, path, value));
    }

    public void move(String from, String path) {
        items.add(new JsonPatchItem(move, path, from));
    }

    public void copy(String from, String path) {
        items.add(new JsonPatchItem(copy, path, from));
    }

    @Override
    public String toString() {
        JSONArray a = new JSONArray();
        items.forEach(i -> a.put(new JSONObject(i.toString())));
        return a.toString();
    }

    public Iterable<JsonPatchItem> getOperations(){
        return Collections.unmodifiableList(items);
    }
}