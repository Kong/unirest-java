package io.github.openunirest.http.utils;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResponseUtilsTest {

    @Test
    public void getCharsetDefaults() {
        assertEquals("UTF-8", ResponseUtils.getCharSet(entity(null)));
        assertEquals("UTF-8", ResponseUtils.getCharSet(entity("")));
        assertEquals("UTF-8", ResponseUtils.getCharSet(entity("         ")));
        assertEquals("UTF-8", ResponseUtils.getCharSet(new BasicHttpEntity()));
        assertEquals("UTF-8", ResponseUtils.getCharSet(entity("Content-Type: text/html;")));
        assertEquals("UTF-8", ResponseUtils.getCharSet(entity("Content-Type: text/html; charset=")));
    }

    @Test
    public void contentTypeWhenYouGotIt() {
        assertEquals("LATIN-1", ResponseUtils.getCharSet(entity("Content-Type: text/html; charset=latin-1")));
    }

    private HttpEntity entity(String charset) {
        BasicHttpEntity e = new BasicHttpEntity();
        e.setContentType(charset);
        return e;
    }
}