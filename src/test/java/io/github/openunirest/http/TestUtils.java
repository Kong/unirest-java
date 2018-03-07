package io.github.openunirest.http;

import java.io.IOException;

public class TestUtils {
    private static final com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

    public static void debugApache() {
        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
    }

    public static <T> T read(String o, Class<T> as){
        try {
            return om.readValue(o, as);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(HttpResponse<JsonNode> r, Class<T> as) {
        try {
            return om.readValue(r.getRawBody(), as);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
