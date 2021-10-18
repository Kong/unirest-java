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

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import kong.unirest.ObjectMapper;
import kong.unirest.json.*;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GsonEngine implements JsonEngine {
    private static final TypeAdapter<Object> ADAPTER = new JavaTypeAdapter();
    private Gson gson;
    private Gson pretty;



    public GsonEngine() {
        GsonBuilder builder = new GsonBuilder()
                .disableHtmlEscaping()
                .serializeNulls()
                .registerTypeAdapter(Map.class, ADAPTER)
                .registerTypeAdapter(List.class, ADAPTER);
        gson = builder.create();
        pretty = builder.setPrettyPrinting().create();
    }

    static EngineElement toElement(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject){
            return new GsonObject((JsonObject) jsonElement);
        } else if (jsonElement instanceof JsonArray) {
            return new GsonArray((JsonArray) jsonElement);
        } else if (jsonElement instanceof JsonPrimitive) {
            return new GsonPrimitive((JsonPrimitive) jsonElement);
        }
        return new GsonNull();
    }

    @Override
    public void toJson(EngineElement obj, Writer sw) {
        gson.toJson(obj.getEngineElement(), sw);
    }

    @Override
    public void toPrettyJson(EngineElement obj, Writer sw) {
        pretty.toJson(obj.getEngineElement(), sw);
    }

    @Override
    public String toPrettyJson(EngineElement obj) {
        if(obj instanceof EngineElement){
            return pretty.toJson(((EngineElement)obj).getEngineElement());
        }
        return pretty.toJson(obj);
    }


    @Override
    public String toJson(EngineElement obj) {
        if(obj instanceof EngineElement){
            return gson.toJson(((EngineElement)obj).getEngineElement());
        }
        return gson.toJson(obj);
    }

    @Override
    public EngineElement toJsonTree(Object obj) {
        return toElement(gson.toJsonTree(obj));
    }


    @Override
    public EngineObject newEngineObject() {
        return new GsonObject();
    }

    @Override
    public EngineObject newEngineObject(String string) {
        try {
            JsonObject element = gson.fromJson(string, JsonObject.class);
            return new GsonObject(element);
        }catch (JsonSyntaxException e){
            throw new JSONException("Invalid JSON");
        }
    }

    @Override
    public EngineArray newEngineArray() {
        return new GsonArray();
    }

    @Override
    public <T> T fromJson(EngineElement obj, Class<T> mapClass) {
        return gson.fromJson((JsonElement) obj.getEngineElement(), mapClass);
    }

    @Override
    public EngineArray newJsonArray(String jsonString) {
        try {
            return new GsonArray(gson.fromJson(jsonString, JsonArray.class));
        }catch (JsonSyntaxException e){
            throw new JSONException("Invalid JSON");
        }
    }

    @Override
    public EngineArray newJsonArray(Collection<?> collection) {
        GsonArray a = new GsonArray();
        for(Object o : collection){
            add(a, o);
        }
        return a;
    }

    private void add(GsonArray a, Object o) {
        if(o instanceof Number){
            a.add((Number) o);
        } else if (o instanceof String) {
            a.add((String) o);
        }else if (o instanceof Boolean) {
            a.add((Boolean) o);
        }else if(o instanceof JSONElement) {
            a.add(((JSONElement) o).getElement());
        } else if(o instanceof EngineElement) {
            a.add((EngineElement) o);
        } else {
            JsonElement tree = gson.toJsonTree(o);
            a.add(toElement(tree));
        }
    }

    @Override
    public <T extends Enum> EnginePrimitive newJsonPrimitive(T enumValue) {
        if(enumValue == null){
            return new GsonNull();
        } else {
            return new GsonPrimitive(new JsonPrimitive(enumValue.name()));
        }
    }

    @Override
    public EnginePrimitive newJsonPrimitive(String string) {
        if(string == null){
            return new GsonNull();
        } else {
            return new GsonPrimitive(new JsonPrimitive(string));
        }
    }

    @Override
    public EnginePrimitive newJsonPrimitive(Number number) {
        if(number == null){
            return new GsonNull();
        } else {
            return new GsonPrimitive(new JsonPrimitive(number));
        }
    }

    @Override
    public EnginePrimitive newJsonPrimitive(Boolean bool) {
        if(bool == null){
            return new GsonNull();
        } else {
            return new GsonPrimitive(new JsonPrimitive(bool));
        }
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return new GsonObjectMapper();
    }

    @Override
    public String quote(Object s) {
        return gson.toJson(s);
    }

    static class JavaTypeAdapter extends TypeAdapter<Object> {

        private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            delegate.write(out, value);
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    String n = in.nextString();
                    if (n.indexOf('.') != -1) {
                        return Double.parseDouble(n);
                    }
                    long l = Long.parseLong(n);
                    if(l < Integer.MAX_VALUE){
                        return (int)l;
                    }
                    return l;

                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }
    }
}
