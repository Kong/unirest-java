package io.github.openunirest.request;


import io.github.openunirest.http.HttpMethod;
import io.github.openunirest.request.body.Body;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;

import java.io.ByteArrayInputStream;

public class JsonPatchRequest extends HttpRequest implements Body {
    public static final String CONTENT_TYPE = "application/json-patch+json";
    private JsonPatch items = new JsonPatch();

    public JsonPatchRequest(String url) {
        super(HttpMethod.PATCH, url);
        header("Content-Type", CONTENT_TYPE);
    }

    public JsonPatchRequest add(String path, Object value) {
        items.add(path, value);
        return this;
    }

    public JsonPatchRequest remove(String path) {
        items.remove(path);
        return this;
    }

    public JsonPatchRequest replace(String path, Object value) {
        items.replace(path, value);
        return this;
    }

    public JsonPatchRequest test(String path, Object value) {
        items.test(path, value);
        return this;
    }

    public JsonPatchRequest move(String from, String path) {
        items.move(from, path);
        return this;
    }

    public JsonPatchRequest copy(String from, String path) {
        items.copy(from, path);
        return this;
    }

    @Override
    public Body getBody() {
        return this;
    }

    @Override
    public HttpEntity getEntity() {
        BasicHttpEntity e = new BasicHttpEntity();
        e.setContent(new ByteArrayInputStream(items.toString().getBytes()));
        return e;
    }
}