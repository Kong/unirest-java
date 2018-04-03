package io.github.openunirest.http;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonNodeTest {

    @Test
    public void canParseARegularObject() {
        String json = "{\"foo\":\"bar\"}";
        JsonNode node = new JsonNode(json);
        assertEquals(false, node.isArray());
        assertEquals("bar", node.getObject().getString("foo"));
        assertEquals("bar", node.getArray().getJSONObject(0).getString("foo"));
        assertEquals(json, node.toString());
    }

    @Test
    public void canParseArrayObject() {
        String json = "[{\"foo\":\"bar\"}]";
        JsonNode node = new JsonNode(json);
        assertEquals(true, node.isArray());
        assertEquals("bar", node.getArray().getJSONObject(0).getString("foo"));
        assertEquals(null, node.getObject());
        assertEquals(json, node.toString());
    }

    @Test
    public void nullAndEmptyObjectsResultInEmptyJson() {
        assertEquals("{}", new JsonNode("").toString());
        assertEquals("{}", new JsonNode(null).toString());
    }
}