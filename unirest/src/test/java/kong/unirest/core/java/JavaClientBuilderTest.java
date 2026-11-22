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

import java.io.IOException;
import java.net.*;
import java.net.Authenticator.RequestorType;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JavaClientBuilderTest {

    @Test
    void canAcceptsAllCookies() throws Exception {
        var cookies = cookieHandler(getConfig().cookiePolicy(CookiePolicy.ACCEPT_ALL));

        plantCrossDomainCookie(cookies);

        assertThat(cookiesSentTo(cookies, "http://bank.example/")).containsExactly("session=attacker");
    }

    @Test
    void usesTheConfiguredCookiePolicy() throws Exception {
        var cookies = cookieHandler(getConfig().cookiePolicy(new DomainMatchCookiePolicy()));

        plantCrossDomainCookie(cookies);

        assertThat(cookiesSentTo(cookies, "http://bank.example/")).isEmpty();
    }

    @Test
    void canUseACustomCookiePolicy() throws Exception {
        var cookies = cookieHandler(getConfig().cookiePolicy(CookiePolicy.ACCEPT_NONE));

        setCookies(cookies, "http://bank.example/", "session=abc");

        assertThat(cookiesSentTo(cookies, "http://bank.example/")).isEmpty();
    }

    @Test
    void cookiesAreNotSentToAHostThatOnlySharesASuffix() throws Exception {
        for (String cookie : List.of(
                "session=abc",
                "session=abc; Domain=bank.example",
                "session=abc; Max-Age=60",
                "session=abc; Domain=bank.example; Max-Age=60")) {
            CookieHandler cookies = cookieHandler(getConfig());

            setCookies(cookies, "http://bank.example/", cookie);

            assertThat(cookiesSentTo(cookies, "http://notbank.example/")).as(cookie).isEmpty();
        }
    }

    @Test
    void cookiesFromAnIpAreNotSentToAnotherIpThatEndsTheSame() throws Exception {
        var cookies = cookieHandler(getConfig());

        setCookies(cookies, "http://10.0.0.1/", "session=abc");

        assertThat(cookiesSentTo(cookies, "http://110.0.0.1/")).isEmpty();
    }

    @Test
    void cookiesAreStillSentToTheirDomainAndSubdomains() throws Exception {
        for (String cookie : List.of("session=abc; Domain=bank.example", "session=abc; Domain=.bank.example")) {
            var cookies = cookieHandler(getConfig());

            setCookies(cookies, "http://bank.example/", cookie);

            assertThat(cookiesSentTo(cookies, "http://bank.example/")).as(cookie).containsExactly("session=abc");
            assertThat(cookiesSentTo(cookies, "http://BANK.example/")).as(cookie).containsExactly("session=abc");
            assertThat(cookiesSentTo(cookies, "http://www.bank.example/")).as(cookie).containsExactly("session=abc");
        }
    }

    @Test
    void secureCookiesAreNotAcceptedFromAnInsecureResponse() throws Exception {
        var cookies = cookieHandler(getConfig());

        setCookies(cookies, "http://bank.example/", "session=attacker; Secure");

        assertThat(cookiesSentTo(cookies, "https://bank.example/")).isEmpty();
    }

    private static void setCookies(CookieHandler cookies, String url, String e1) throws IOException, URISyntaxException {
        cookies.put(new URI(url), Map.of("Set-Cookie", List.of(e1)));
    }

    @Test
    void anInsecureResponseCannotReplaceASecureCookie() throws Exception {
        for (String cookie : List.of(
                "session=attacker",
                "session=attacker; Path=/account",
                "session=attacker; Domain=bank.example",
                "session=attacker; Domain=www.bank.example")) {

            var cookies = cookieHandler(getConfig());

            setCookies(cookies, "https://www.bank.example/", "session=abc; Domain=bank.example; Secure");
            setCookies(cookies, "http://www.bank.example/account", cookie);

            assertThat(cookiesSentTo(cookies, "https://www.bank.example/account")).as(cookie).containsExactly("session=abc");
        }
    }

    @Test
    void secureCookiesStillWorkOverHttps() throws Exception {
        var cookies = cookieHandler(getConfig());

        setCookies(cookies, "https://bank.example/", "session=abc; Secure");
        setCookies(cookies, "http://bank.example/", "lang=en");
        setCookies(cookies, "https://bank.example/", "session=def; Secure");

        assertThat(cookiesSentTo(cookies, "https://bank.example/")).containsExactlyInAnyOrder("session=def", "lang=en");
        assertThat(cookiesSentTo(cookies, "http://bank.example/")).containsExactly("lang=en");
    }

    @Test
    void proxyCredentialsAnswerProxyChallenges() throws Exception {
        var auth = proxyAuthenticator();

        var creds = challenge(auth, "proxy.example", RequestorType.PROXY);

        assertThat(creds.getUserName()).isEqualTo("proxyuser");
        assertThat(creds.getPassword()).isEqualTo("proxypass".toCharArray());
    }

    @Test
    void proxyCredentialsAreNotGivenToServers() throws Exception {
        var auth = proxyAuthenticator();

        assertThat(challenge(auth, "evil.example", RequestorType.SERVER)).isNull();
    }

    private Authenticator proxyAuthenticator() {
        Config config = getConfig().proxy("proxy.example", 3128, "proxyuser", "proxypass");
        return new JavaClientBuilder().apply(config).authenticator().get();
    }

    private PasswordAuthentication challenge(Authenticator auth, String host, RequestorType type) throws Exception {
        return auth.requestPasswordAuthenticationInstance(host, null, 80, "http", "realm", "Basic",
                URI.create("http://" + host + "/").toURL(), type);
    }

    private static Config getConfig() {
        return new Config().enableCookieManagement(true);
    }

    private void plantCrossDomainCookie(CookieHandler cookies) throws Exception {
        setCookies(cookies, "http://evil.example/", "session=attacker; Domain=bank.example");
    }

    private CookieHandler cookieHandler(Config config) {
        return new JavaClientBuilder().apply(config).cookieHandler().get();
    }

    private List<String> cookiesSentTo(CookieHandler cookies, String uri) throws Exception {
        return cookies.get(new URI(uri), Map.of()).get("Cookie");
    }
}
