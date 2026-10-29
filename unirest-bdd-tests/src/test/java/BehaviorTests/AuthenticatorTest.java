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

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.junit.jupiter.api.Test;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthenticatorTest extends BddTest {
    @Test
    void getBlockedWithoutAuth(){
        assertThat(Unirest.get(MockServer.BASICAUTH).asEmpty())
                .returns(401, HttpResponse::getStatus)
                .returns("Basic realm=\"User Visible Realm\", charset=\"UTF-8\"", r -> r.getHeaders().getFirst("WWW-Authenticate"));
    }

    @Test
    void setAuthenticator(){
        Unirest.config().authenticator(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("Fred", "Flintstone".toCharArray());
            }
        });

        Unirest.get(MockServer.BASICAUTH)
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("Fred", "Flintstone");

        MockServer.assertRequestCount(2);
    }

    @Test
    void setAuthenticatorPerHostAndPort(){
        Unirest.config().authenticator(new Authenticator() {
            @Override
            public PasswordAuthentication requestPasswordAuthenticationInstance(String host, InetAddress addr, int port,
                                                                                String protocol, String prompt,
                                                                                String scheme, URL url,
                                                                                RequestorType reqType) {
                if(host.contains("localhost")){
                    return new PasswordAuthentication("Barney", "Rubble".toCharArray());
                } else {
                    return new PasswordAuthentication("Fred", "Flintstone".toCharArray());
                }
            }
        });

        Unirest.get(MockServer.BASICAUTH)
                .queryString("barney", "true")
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("Barney", "Rubble");

        // The Authenticator will cache for different ports and hosts.
        Unirest.get(MockServer.BASICAUTH.replace("localhost", "127.0.0.1"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("Fred", "Flintstone");


    }

    // MockServer accepts the absolute-form requests a client sends to an HTTP proxy, so it can play the proxy here
    @Test
    void proxyCredentialsAnswerTheProxyChallenge(){
        Unirest.config().proxy("localhost", MockServer.PORT, "proxyuser", "proxypass");

        Unirest.get(MockServer.PROXYAUTH)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Proxy-Authorization", "Basic " + Base64.getEncoder().encodeToString("proxyuser:proxypass".getBytes()))
                .assertNoHeader("Authorization");

        MockServer.assertRequestCount(2);
    }

    @Test
    void proxyCredentialsAreNotSentToTheServer(){
        Unirest.config().proxy("localhost", MockServer.PORT, "proxyuser", "proxypass");

        assertThatThrownBy(() -> Unirest.get(MockServer.BASICAUTH).asEmpty())
                .isInstanceOf(UnirestException.class);

        MockServer.lastRequest().assertNoHeader("Authorization");
        MockServer.assertRequestCount(1);
    }
}
