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

package BehaviorTests;

import kong.unirest.Cookie;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.http.client.config.CookieSpecs;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CookieTest extends BddTest {

    @Test
    void canSetCookie(){
        Unirest.get(MockServer.GET)
                .cookie("flavor","snickerdoodle")
                .cookie("size", "large")
                .asObject(RequestCapture.class)
                .getBody()
                .assertCookie("flavor", "snickerdoodle")
                .assertCookie("size", "large");
    }

    @Test
    void canSetAsCookie(){
        Unirest.get(MockServer.GET)
                .cookie(new Cookie("flavor", "snickerdoodle"))
                .cookie(new Cookie("size", "large"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertCookie("flavor", "snickerdoodle")
                .assertCookie("size", "large");
    }

    @Test
    void canSetAsCookieCollection(){
        Unirest.get(MockServer.GET)
                .cookie(Arrays.asList(
                        new Cookie("flavor", "snickerdoodle"),
                        new Cookie("size", "large"))
                )
                .asObject(RequestCapture.class)
                .getBody()
                .assertCookie("flavor", "snickerdoodle")
                .assertCookie("size", "large");
    }


    @Test
    void willManageCookiesByDefault() {
        MockServer.expectCookie("JSESSIONID", "ABC123");
        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertCookie("JSESSIONID", "ABC123");
    }

    @Test
    void canGetCookiesFromTheResponse() {
        MockServer.expectCookie("JSESSIONID", "ABC123");
        MockServer.expectCookie("color", "ruby");

        HttpResponse response = Unirest.get(MockServer.GET).asEmpty();

        assertEquals("ABC123", response.getCookies().getNamed("JSESSIONID").getValue());
        assertEquals("ruby", response.getCookies().getNamed("color").getValue());
    }

    @Test
    void canGetUnicode() {
        MockServer.expectCookie("nepali", "फनकी");

        HttpResponse response = Unirest.get(MockServer.GET).asEmpty();

        assertEquals("फनकी", response.getCookies().getNamed("nepali").getUrlDecodedValue());
    }

    @Test
    void canGetValuesWithBadCharacters() {
        MockServer.expectCookie("odd", "1=2;3=4");

        HttpResponse response = Unirest.get(MockServer.GET).asEmpty();

        assertEquals("1=2;3=4", response.getCookies().getNamed("odd").getUrlDecodedValue());
    }

    @Test
    void complicatedCookies(){
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("color", "blue");
        cookie.setDomain("localhost");
        cookie.setPath("/get");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(42);
        MockServer.expectCookie(cookie);

        HttpResponse response = Unirest.get(MockServer.GET).asEmpty();

        Cookie back = response.getCookies().getNamed("color");
        assertEquals("blue", back.getValue());
        assertEquals("localhost", back.getDomain());
        assertEquals("/get", back.getPath());
        assertTrue(back.isHttpOnly());
        assertFalse(back.isSecure());
        assertEquals(42, back.getMaxAge());

    }

    @Test
    void canTurnOffCookieManagement() {
        Unirest.config().enableCookieManagement(false);
        MockServer.expectCookie("JSESSIONID", "ABC123");
        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoCookie("JSESSIONID");
    }

    @Test
    void canSetCookieSpec() {
        Unirest.config().cookieSpec(CookieSpecs.IGNORE_COOKIES);

        MockServer.expectCookie("JSESSIONID", "ABC123");
        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoCookie("JSESSIONID");
    }

    @Test
    void doubleQuotedValues() {
        MockServer.expectCookie(new javax.servlet.http.Cookie("foo", "\"bar\""));

        HttpResponse<RequestCapture> res = Unirest.get(MockServer.GET)
                .cookie("baz", "\"  wut  \"")
                .asObject(RequestCapture.class);

        res.getBody().assertCookie("baz", "  wut  ");

        Cookie cookie = res
                .getCookies()
                .getNamed("foo");

        assertEquals("bar", cookie.getValue());
    }

    @Test
    void canSetDefaultCookie() {
        Unirest.config().addDefaultCookie("flavor","snickerdoodle");

        Unirest.get(MockServer.GET)
                .cookie("size", "large")
                .asObject(RequestCapture.class)
                .getBody()
                .assertCookie("flavor", "snickerdoodle")
                .assertCookie("size", "large");
    }

    @Test
    void canSetDefaultCookieAsFullCookieObj() {
        Unirest.config().addDefaultCookie(new Cookie("flavor","snickerdoodle"));

        Unirest.get(MockServer.GET)
                .cookie("size", "large")
                .asObject(RequestCapture.class)
                .getBody()
                .assertCookie("flavor", "snickerdoodle")
                .assertCookie("size", "large");
    }
}
