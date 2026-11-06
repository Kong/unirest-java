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

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestConfigException;
import kong.unirest.core.UnirestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SslConfigTest extends BddTest {
    private static final char[] PASSWORD = "badssl.com".toCharArray();

    private HttpsServer server;
    private String url;

    // The badssl.com client certificate is not issued by a CA the JDK trusts,
    // so serving it gives a local server with an untrusted certificate.
    @BeforeEach
    void startServer() throws Exception {
        KeyStore store = KeyStore.getInstance("PKCS12");
        try (InputStream in = getClass().getResourceAsStream("/certs/badssl.com-client.p12")) {
            store.load(in, PASSWORD);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(store, PASSWORD);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), null, null);

        server = HttpsServer.create(new InetSocketAddress("localhost", 0), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(context) {
            @Override
            public void configure(HttpsParameters params) {
                SSLParameters ssl = context.getDefaultSSLParameters();
                ssl.setProtocols(new String[]{"TLSv1.2"});
                params.setSSLParameters(ssl);
            }
        });
        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
        url = "https://localhost:" + server.getAddress().getPort() + "/";
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void verificationCannotBeTurnedBackOnWithoutAReset() {
        Unirest.config().verifySsl(false);
        assertThat(Unirest.get(url).asEmpty().getStatus()).isEqualTo(200);

        assertThatThrownBy(() -> Unirest.config().verifySsl(true))
                .isInstanceOf(UnirestConfigException.class);

        Unirest.config().reset().verifySsl(true);
        assertThatThrownBy(() -> Unirest.get(url).asEmpty())
                .isInstanceOf(UnirestException.class)
                .hasCauseInstanceOf(SSLHandshakeException.class);
    }

    @Test
    void protocolsCannotBeChangedWithoutAReset() {
        Unirest.config().verifySsl(false);
        assertThat(Unirest.get(url).asEmpty().getStatus()).isEqualTo(200);

        assertThatThrownBy(() -> Unirest.config().protocols("TLSv1.3"))
                .isInstanceOf(UnirestConfigException.class);

        Unirest.config().reset().protocols("TLSv1.3");
        assertThatThrownBy(() -> Unirest.get(url).asEmpty())
                .isInstanceOf(UnirestException.class)
                .hasCauseInstanceOf(SSLHandshakeException.class);
    }
}
