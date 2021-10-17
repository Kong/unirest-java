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

package kong.unirest;

import org.eclipse.jetty.server.CookieCutter;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CookieParsingTest {

    @Test
    void parseFull() {
        Cookie c = new Cookie("color=blue;Path=/get;Domain=localhost;Expires=Sun, 05-Jan-2020 15:00:20 GMT;Max-Age=42;HttpOnly");
        assertEquals("blue", c.getValue());
        assertEquals("localhost", c.getDomain());
        assertEquals("/get", c.getPath());
        assertEquals(ZonedDateTime.of(2020,1,5,15,0,20, 0, ZoneId.of("GMT")),
                c.getExpiration());
        assertTrue(c.isHttpOnly());
        assertFalse(c.isSecure());
        assertEquals(42, c.getMaxAge());
        assertNull(c.getSameSite());
    }

    @Test
    void alternateDate() {
        Cookie c = new Cookie("color=blue;Path=/get;Domain=localhost;Expires=Sun, 05 Jan 2020 15:00:20 GMT;Max-Age=42;HttpOnly");
        assertEquals(ZonedDateTime.of(2020,1,5,15,0,20, 0, ZoneId.of("GMT")),
                c.getExpiration());
    }

    @Test
    void parseBackOutToString() {
        String v = "color=blue;Path=/get;Domain=localhost;Expires=Sun, 05-Jan-2020 15:00:20 GMT;Max-Age=42;HttpOnly";
        Cookie c = new Cookie(v);
        assertEquals(v, c.toString());
    }

    @Test
    void sameSite() {
        String v = "color=blue;SameSite=Strict";
        Cookie c = new Cookie(v);
        assertEquals(Cookie.SameSite.Strict, c.getSameSite());
    }

    @Test
    void secure() {
        String v = "color=blue;Secure";
        Cookie c = new Cookie(v);
        assertTrue(c.isSecure());
        assertEquals("color=blue;Secure", c.toString());
    }


    @Test
    void matchJettyParsing() {
        String s = "_id=A528A0D64DA61CB01241EF6E18E4D675170DDB56CB430000AF58625E920CF940~pl1ZYwDDdoAnfY3RtqZ2Ti1MYOf7Q8jRrFQLneSGK7qQoUX1GHW/xDcqweGyclm5rm/g/YFCV3ohuHoz2oad5M0MX9Ru9V7bFr2s08d1lHxbn39gw71AI+ZVejq5FpMHKBzyjoBGG6NY6xYVTwP9NHo14SY0CXs60k2UTpJsOTNzAHIZaedg7o6R/8qyAQ8GF25K2o773pFLrYtjgKHohkk5ukz/yEGQitq8NgC5hiqX0=; expires=Fri, 06 Mar 2020 16:05:35 GMT; max-age=7200; path=/; domain=xxx.com; HttpOnly";
        CookieCutter cutter = new CookieCutter();
        cutter.addCookieField(s);
        javax.servlet.http.Cookie jetty = cutter.getCookies()[0];

        Cookie unirest = new Cookie(s);

        assertEquals(jetty.getValue(), unirest.getValue());
    }

    @Test
    void futurePropsDontBreak() {
        String v = "color=blue;TheFuture";
        Cookie c = new Cookie(v);
        assertEquals("color=blue", c.toString());
    }

    @Test
    void emptyValue() {
        String v = "SignOnDefault=; domain=.admin.virginia.edu; path=/; HttpOnly";
        Cookie c = new Cookie(v);
        assertEquals("", c.getValue());
        assertTrue(c.isHttpOnly());
        assertEquals(".admin.virginia.edu", c.getDomain());
    }

    @Test
    void theValieCanBeDoubleQuoted() {
        String v = "SignOnDefault=\" woh \";";
        Cookie c = new Cookie(v);
        assertEquals(" woh ", c.getValue());
    }

    @Test
    void justEmptyQuotes() {
        String v = "SignOnDefault=\"\";";
        Cookie c = new Cookie(v);
        assertEquals("", c.getValue());
    }

    @Test
    void valuesCanHaveEquals() {
        String v = "SignOnDefault====;";
        Cookie c = new Cookie(v);
        assertEquals("===", c.getValue());
    }

    @Test
    void justOneQuote() {
        String v = "SignOnDefault=\";";
        Cookie c = new Cookie(v);
        assertEquals("\"", c.getValue());
    }

    @Test
    void justOneSideOfquotes() {
        String v = "SignOnDefault=\"foo;";
        Cookie c = new Cookie(v);
        assertEquals("\"foo", c.getValue());
    }
}
