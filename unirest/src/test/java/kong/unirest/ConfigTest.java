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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyStore;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    @InjectMocks
    private Config config;

    @Test
    void shouldKeepConnectionTimeOutDefault(){
        assertEquals(Config.DEFAULT_CONNECT_TIMEOUT, config.getConnectionTimeout());
    }

    @Test
    void shouldKeepMaxTotalDefault(){
        assertEquals(Config.DEFAULT_MAX_CONNECTIONS, config.getMaxConnections());
    }

    @Test
    void shouldKeepMaxPerRouteDefault(){
        assertEquals(Config.DEFAULT_MAX_PER_ROUTE, config.getMaxPerRoutes());
    }



    @Test
    void willRebuildIfEmpty() {
        assertSame(config.getAsyncClient(), config.getAsyncClient());
    }

    @Test
    void settingTTl() {
        assertEquals(-1, config.getTTL());

        assertEquals(42, config.connectionTTL(42, TimeUnit.MILLISECONDS).getTTL());
        assertEquals(2520000, config.connectionTTL(42, TimeUnit.MINUTES).getTTL());

        assertEquals(43, config.connectionTTL(Duration.ofMillis(43)).getTTL());
        assertEquals(2580000, config.connectionTTL(Duration.ofMinutes(43)).getTTL());
    }

    @Test
    void ifTheNextAsyncClientThatIsReturnedIsAlsoOffThrowAnException(){
        AsyncClient c = mock(AsyncClient.class);
        when(c.isRunning()).thenReturn(false);
        config.asyncClient(g -> c);


        TestUtil.assertException(() -> config.getAsyncClient(),
                UnirestConfigException.class,
                "Attempted to get a new async client but it was not started. Please ensure it is");
    }

    @Test
    void provideYourOwnClientBuilder() {
        Client cli = mock(Client.class);

        config.httpClient(c -> cli);

        assertSame(cli, config.getClient());
    }

    @Test
    void canSignalForShutdownHook() {
        assertFalse(config.shouldAddShutdownHook());
        config.addShutdownHook(true);
        assertTrue(config.shouldAddShutdownHook());
    }

    @Test
    void canDisableGZipencoding() {
        assertTrue(config.isRequestCompressionOn());
        config.requestCompression(false);
        assertFalse(config.isRequestCompressionOn());

    }

    @Test
    void canDisableAuthRetry() {
        assertTrue(config.isAutomaticRetries());
        config.automaticRetries(false);
        assertFalse(config.isAutomaticRetries());
    }

    @Test
    void provideYourOwnAsyncClientBuilder() {
        AsyncClient cli = mock(AsyncClient.class);
        when(cli.isRunning()).thenReturn(true);

        config.asyncClient(c -> cli);

        assertSame(cli, config.getAsyncClient());
    }

    @Test
    void canSetProxyViaSetter() {
        config.proxy(new Proxy("localhost", 8080, "ryan", "password"));
        assertProxy("localhost", 8080, "ryan", "password");

        config.proxy("local2", 8888);
        assertProxy("local2", 8888, null, null);

        config.proxy("local3", 7777, "barb", "12345");
        assertProxy("local3", 7777, "barb", "12345");
    }

    @Test
    void cannotConfigASslContextIfAKeystoreIsPresent() {
        KeyStore store = mock(KeyStore.class);
        SSLContext context = mock(SSLContext.class);

        config.clientCertificateStore(store, "foo");

        TestUtil.assertException(() -> config.sslContext(context),
                UnirestConfigException.class,
                "You may only configure a SSLContext OR a Keystore, but not both");
    }

    @Test
    void cannotConfigAKeyStoreIfASSLContextIsPresent() {
        KeyStore store = mock(KeyStore.class);
        SSLContext context = mock(SSLContext.class);

        config.sslContext(context);

        TestUtil.assertException(() -> config.clientCertificateStore(store, "foo"),
                UnirestConfigException.class,
                "You may only configure a SSLContext OR a Keystore, but not both");

        TestUtil.assertException(() -> config.clientCertificateStore("/a/path/file.pk12", "foo"),
                UnirestConfigException.class,
                "You may only configure a SSLContext OR a Keystore, but not both");
    }

    @Test
    void isRunningIfStandardClientIsRunning() {
        assertFalse(config.isRunning());
        config.getClient();
        assertTrue(config.isRunning());
    }

    @Test
    void isRunningIfAsyncClientIsRunning() {
        assertFalse(config.isRunning());
        config.getAsyncClient();
        assertTrue(config.isRunning());
    }

    private void assertProxy(String host, Integer port, String username, String password) {
        assertEquals(host, config.getProxy().getHost());
        assertEquals(port, config.getProxy().getPort());
        assertEquals(username, config.getProxy().getUsername());
        assertEquals(password, config.getProxy().getPassword());
    }
}