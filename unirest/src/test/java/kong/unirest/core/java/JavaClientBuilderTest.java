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

package kong.unirest.core.java;

import kong.unirest.core.Config;
import kong.unirest.core.DomainMatchCookiePolicy;
import org.junit.jupiter.api.Test;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JavaClientBuilderTest {

    @Test
    void acceptsAllCookiesByDefault() throws Exception {
        CookieHandler cookies = cookieHandler(new Config());

        plantCrossDomainCookie(cookies);

        assertThat(cookiesSentTo(cookies, "http://bank.example/")).containsExactly("session=attacker");
    }

    @Test
    void usesTheConfiguredCookiePolicy() throws Exception {
        CookieHandler cookies = cookieHandler(new Config().cookiePolicy(new DomainMatchCookiePolicy()));

        plantCrossDomainCookie(cookies);

        assertThat(cookiesSentTo(cookies, "http://bank.example/")).isEmpty();
    }

    @Test
    void canUseACustomCookiePolicy() throws Exception {
        CookieHandler cookies = cookieHandler(new Config().cookiePolicy(CookiePolicy.ACCEPT_NONE));

        cookies.put(new URI("http://bank.example/"), Map.of("Set-Cookie", List.of("session=abc")));

        assertThat(cookiesSentTo(cookies, "http://bank.example/")).isEmpty();
    }

    private void plantCrossDomainCookie(CookieHandler cookies) throws Exception {
        cookies.put(new URI("http://evil.example/"), Map.of("Set-Cookie", List.of("session=attacker; Domain=bank.example")));
    }

    private CookieHandler cookieHandler(Config config) {
        return new JavaClientBuilder().apply(config).cookieHandler().get();
    }

    private List<String> cookiesSentTo(CookieHandler cookies, String uri) throws Exception {
        return cookies.get(new URI(uri), Map.of()).get("Cookie");
    }
}
