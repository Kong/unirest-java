package io.github.openunirest.request;

import io.github.openunirest.http.HttpMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriFormatterTest {
    private UriFormatter helper = new UriFormatter();

    @Test
    public void testMangler_encoding() {
        assertLinkSurvives("http://localhost/test%2Fthis");
    }

    @Test
    public void testMangler_fragment() {
        assertLinkSurvives("http://localhost/test?a=b#fragment");
    }

    @Test
    public void basicBoringUri() {
        assertLinkSurvives("http://localhost/test?a=b");
    }

    @Test
    public void semicolonsAsParam() {
        assertLinkSurvives("http://localhost/test?a=b;foo=bar");
    }

    @Test
    public void utf8Chars(){
        assertLinkSurvives("http://localhost/test?foo=こんにちは");
    }

    private void assertLinkSurvives(String s) {
        assertEquals(s, helper.apply(new HttpRequest(HttpMethod.GET, s)));
    }

}