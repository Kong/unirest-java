package io.github.openunirest.http;

import java.io.IOException;

public class TestUtils {
    private static final JacksonObjectMapper om = new JacksonObjectMapper();


    public static void debugApache() {
        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
    }

    public static <T> T read(String o, Class<T> as){
        return om.readValue(o, as);
    }

    public static <T> T read(HttpResponse r, Class<T> as) {
        return om.readValue(r.getRawBody(), as);
    }
}
