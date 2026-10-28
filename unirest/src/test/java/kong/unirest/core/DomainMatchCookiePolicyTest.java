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

package kong.unirest.core;

import org.junit.jupiter.api.Test;

import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DomainMatchCookiePolicyTest {

    private final CookieManager cookies = new CookieManager(null, new DomainMatchCookiePolicy());

    @Test
    void doesNotStoreCookiesSetForAnotherDomain() throws Exception {
        cookies.put(new URI("http://evil.example/"), Map.of("Set-Cookie", List.of("session=attacker; Domain=bank.example")));

        assertThat(cookiesSentTo("http://bank.example/")).isEmpty();
        assertThat(cookiesSentTo("http://evil.example/")).isEmpty();
    }

    @Test
    void storesCookiesForTheRequestHost() throws Exception {
        cookies.put(new URI("http://localhost:8080/get"), Map.of("Set-Cookie", List.of("color=blue; Domain=localhost", "JSESSIONID=ABC123")));

        assertThat(cookiesSentTo("http://localhost:8080/get")).containsExactlyInAnyOrder("color=blue", "JSESSIONID=ABC123");
    }

    @Test
    void storesCookiesForAParentDomainOfTheRequestHost() throws Exception {
        cookies.put(new URI("https://api.example.com/"), Map.of("Set-Cookie", List.of("sub=1; Domain=example.com", "dot=1; Domain=.example.com")));

        assertThat(cookiesSentTo("https://www.example.com/")).containsExactlyInAnyOrder("sub=1", "dot=1");
    }

    private List<String> cookiesSentTo(String uri) throws Exception {
        return cookies.get(new URI(uri), Map.of()).get("Cookie");
    }
}
