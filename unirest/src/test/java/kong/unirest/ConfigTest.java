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

import kong.unirest.apache.ApacheAsyncClient;
import kong.unirest.apache.ApacheClient;
import kong.unirest.apache.AsyncIdleConnectionMonitorThread;
import kong.unirest.apache.SyncIdleConnectionMonitorThread;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.client.HttpAsyncClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

    @Mock
    private CloseableHttpClient httpc;
    @Mock
    private PoolingHttpClientConnectionManager clientManager;
    @Mock
    private SyncIdleConnectionMonitorThread connMonitor;
    @Mock
    private CloseableHttpAsyncClient asyncClient;
    @Mock
    private AsyncIdleConnectionMonitorThread asyncMonitor;
    @Mock
    private PoolingNHttpClientConnectionManager manager;

    @InjectMocks
    private Config config;

    @Test
    public void shouldKeepConnectionTimeOutDefault(){
        assertEquals(Config.DEFAULT_CONNECT_TIMEOUT, config.getConnectionTimeout());
    }

    @Test
    public void shouldKeepSocketTimeoutDefault(){
        assertEquals(Config.DEFAULT_SOCKET_TIMEOUT, config.getSocketTimeout());
    }

    @Test
    public void shouldKeepMaxTotalDefault(){
        assertEquals(Config.DEFAULT_MAX_CONNECTIONS, config.getMaxConnections());
    }

    @Test
    public void shouldKeepMaxPerRouteDefault(){
        assertEquals(Config.DEFAULT_MAX_PER_ROUTE, config.getMaxPerRoutes());
    }

    @Test
    public void onceTheConfigIsRunningYouCannotChangeConfig(){
        config.httpClient(mock(HttpClient.class));
        config.asyncClient(mock(HttpAsyncClient.class));

        TestUtil.assertException(() -> config.socketTimeout(533),
                UnirestConfigException.class,
                "Http Clients are already built in order to build a new config execute Unirest.config().reset() " +
                        "before changing settings. \n" +
                        "This should be done rarely.");

        Unirest.shutDown();
    }

    @Test
    public void willNotRebuildIfNotClosableAsyncClient() {
        HttpAsyncClient c = mock(HttpAsyncClient.class);
        config.asyncClient(c);

        assertSame(c, config.getAsyncClient().getClient());
        assertSame(c, config.getAsyncClient().getClient());
    }

    @Test
    public void willRebuildIfEmpty() {
        assertSame(config.getAsyncClient(), config.getAsyncClient());
    }

    @Test
    public void willRebuildIfClosableAndStopped() {
        CloseableHttpAsyncClient c = mock(CloseableHttpAsyncClient.class);
        when(c.isRunning()).thenReturn(false);

        config.asyncClient(c);

        assertNotSame(c, config.getAsyncClient());
    }

    @Test
    public void testShutdown() throws IOException {
        when(asyncClient.isRunning()).thenReturn(true);

        Unirest.config()
                .httpClient(new ApacheClient(httpc, null, clientManager, connMonitor))
                .asyncClient(new ApacheAsyncClient(asyncClient, null, manager, asyncMonitor));

        Unirest.shutDown();

        verify(httpc).close();
        verify(clientManager).close();
        verify(connMonitor).interrupt();
        verify(asyncClient).close();
        verify(asyncMonitor).interrupt();
    }

    @Test
    public void willPowerThroughErrors() throws IOException {
        when(asyncClient.isRunning()).thenReturn(true);
        doThrow(new IOException("1")).when(httpc).close();
        doThrow(new RuntimeException("2")).when(clientManager).close();
        doThrow(new RuntimeException("3")).when(connMonitor).interrupt();
        doThrow(new IOException("4")).when(asyncClient).close();
        doThrow(new RuntimeException("5")).when(asyncMonitor).interrupt();

        Unirest.config()
                .httpClient(new ApacheClient(httpc, null, clientManager, connMonitor))
                .asyncClient(new ApacheAsyncClient(asyncClient,null, manager, asyncMonitor));


        TestUtil.assertException(Unirest::shutDown,
                UnirestException.class,
                "java.io.IOException 1\n" +
                        "java.lang.RuntimeException 2\n" +
                        "java.lang.RuntimeException 3\n" +
                        "java.io.IOException 4\n" +
                        "java.lang.RuntimeException 5");

        verify(httpc).close();
        verify(clientManager).close();
        verify(connMonitor).interrupt();
        verify(asyncClient).close();
        verify(asyncMonitor).interrupt();
    }

    @Test
    public void doesNotBombOnNullOptions() throws IOException {
        when(asyncClient.isRunning()).thenReturn(true);

        Unirest.config()
                .httpClient(new ApacheClient(httpc, null, null, null))
                .asyncClient(new ApacheAsyncClient(asyncClient, null, null, null));

        Unirest.shutDown();

        verify(httpc).close();
        verify(asyncClient).close();
    }

    @Test
    public void ifTheNextAsyncClientThatIsReturnedIsAlsoOffThrowAnException(){
        AsyncClient c = mock(AsyncClient.class);
        when(c.isRunning()).thenReturn(false);
        config.asyncClient(g -> c);


        TestUtil.assertException(() -> config.getAsyncClient(),
                UnirestConfigException.class,
                "Attempted to get a new async client but it was not started. Please ensure it is");
    }

    @Test
    public void willNotRebuildIfRunning() {
        CloseableHttpAsyncClient c = mock(CloseableHttpAsyncClient.class);
        when(c.isRunning()).thenReturn(true);

        config.asyncClient(c);

        assertSame(c, config.getAsyncClient().getClient());
    }

    @Test
    public void provideYourOwnClientBuilder() {
        Client cli = mock(Client.class);

        config.httpClient(c -> cli);

        assertSame(cli, config.getClient());
    }

    @Test
    public void canSignalForShutdownHook() {
        assertFalse(config.shouldAddShutdownHook());
        config.addShutdownHook(true);
        assertTrue(config.shouldAddShutdownHook());
    }

    @Test
    public void canDisableGZipencoding() {
        assertTrue(config.isRequestCompressionOn());
        config.requestCompression(false);
        assertFalse(config.isRequestCompressionOn());

    }

    @Test
    public void canDisableAuthRetry() {
        assertTrue(config.isAutomaticRetries());
        config.automaticRetries(false);
        assertFalse(config.isAutomaticRetries());
    }

    @Test
    public void provideYourOwnAsyncClientBuilder() {
        AsyncClient cli = mock(AsyncClient.class);
        when(cli.isRunning()).thenReturn(true);

        config.asyncClient(c -> cli);

        assertSame(cli, config.getAsyncClient());
    }

    @Test
    public void canSetProxyViaSetter() {
        config.proxy(new Proxy("localhost", 8080, "ryan", "password"));
        assertProxy("localhost", 8080, "ryan", "password");

        config.proxy("local2", 8888);
        assertProxy("local2", 8888, null, null);

        config.proxy("local3", 7777, "barb", "12345");
        assertProxy("local3", 7777, "barb", "12345");
    }

    private void assertProxy(String host, Integer port, String username, String password) {
        assertEquals(host, config.getProxy().getHost());
        assertEquals(port, config.getProxy().getPort());
        assertEquals(username, config.getProxy().getUsername());
        assertEquals(password, config.getProxy().getPassword());
    }
}