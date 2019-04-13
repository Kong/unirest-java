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

import kong.unirest.Unirest;
import org.apache.http.client.config.CookieSpecs;
import org.junit.Test;

public class CookieTest extends BddTest {
    @Test
    public void willManageCookiesByDefault() {
        MockServer.expectCookie("JSESSIONID", "ABC123");
        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertCookie("JSESSIONID", "ABC123");
    }

    @Test
    public void canTurnOffCookieManagement() {
        Unirest.config().enableCookieManagement(false);
        MockServer.expectCookie("JSESSIONID", "ABC123");
        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoCookie("JSESSIONID");
    }

    @Test
    public void canSetCookieSpec() {
        Unirest.config().cookieSpec(CookieSpecs.IGNORE_COOKIES);

        MockServer.expectCookie("JSESSIONID", "ABC123");
        Unirest.get(MockServer.GET).asEmpty();
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoCookie("JSESSIONID");
    }
}
