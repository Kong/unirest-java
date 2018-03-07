package io.github.openunirest.http;

import spark.Request;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class FormCapture {
    public Map<String,String> headers = new LinkedHashMap<>();
    public Map<String,String> query = new LinkedHashMap<>();

    public FormCapture(){}

    public FormCapture(Request req) {
        writeHeaders(req);
        writeQuery(req);
    }

    private void writeQuery(Request req) {
        req.queryParams().forEach(q -> query.put(q, req.queryParams(q)));
    }

    private void writeHeaders(Request req) {
        req.headers().forEach(h -> headers.put(h, req.headers(h)));
    }

    public void assertHeader(String key, String value) {
        assertEquals("Expected Header Failed", value, headers.get(key));
    }

    public void assertQuery(String key, String value) {
        assertEquals("Expected Query or Form value", value, query.get(key));
    }
}
