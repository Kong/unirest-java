package io.github.openunirest.request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.openunirest.request.JsonPatchOperation.*;

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