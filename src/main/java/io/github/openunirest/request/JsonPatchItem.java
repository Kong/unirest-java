package io.github.openunirest.request;

import org.json.JSONObject;

import java.util.Objects;

public class JsonPatchItem {
    private final JsonPatchOperation op;
    private final String path;
    private final Object value;

    public JsonPatchItem(JsonPatchOperation op, String path, Object value){
        this.op = op;
        this.path = path;
        this.value = value;
    }

    public JsonPatchItem(JsonPatchOperation remove, String path) {
        this(remove, path, null);
    }

    public JsonPatchItem(JSONObject row) {
        this.op = JsonPatchOperation.valueOf(row.getString("op"));
        this.path = row.getString("path");
        if(row.has(op.getOperationtype())) {
            this.value = row.get(op.getOperationtype());
        } else {
            this.value = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        JsonPatchItem that = (JsonPatchItem) o;
        return op == that.op &&
                Objects.equals(path, that.path) &&
                Objects.equals(toString(), that.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, path, value);
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject()
                .put("op", op)
                .put("path", path);

        if(Objects.nonNull(value)){
            json.put(op.getOperationtype(), value);
        }

        return json.toString();
    }

    public JsonPatchOperation getOp() {
        return op;
    }

    public String getPath() {
        return path;
    }

    public Object getValue() {
        return value;
    }
}