/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

import com.github.paweladamski.httpclientmock.HttpClientMock;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import unirest.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LifeCycleTest extends BddTest {

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

    @Test
    public void settingACustomClient() {
        HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost/getme").doReturn(202, "Howdy Ho!");
        Unirest.config().httpClient(httpClientMock);
        HttpResponse<String> result =  Unirest.get("http://localhost/getme").asString();
        assertEquals(202, result.getStatus());
        assertEquals("Howdy Ho!", result.getBody());
    }

    @Test
    public void settingClientAfterClientHasAlreadyBeenSet() {
        HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost/getme").doReturn(202, "Howdy Ho!");
        assertEquals(200, Unirest.get(MockServer.GET).asString().getStatus());
        Unirest.config().httpClient(httpClientMock);
        HttpResponse<String> result =  Unirest.get("http://localhost/getme").asString();
        assertEquals(202, result.getStatus());
        assertEquals("Howdy Ho!", result.getBody());
    }

    @Test
    public void testShutdown() throws IOException {
        when(asyncClient.isRunning()).thenReturn(true);

        Unirest.config()
                .httpClient(new Client(httpc, clientManager, connMonitor))
                .asyncClient(new ApacheAsyncClient(asyncClient, manager, asyncMonitor));

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
                .httpClient(new Client(httpc, clientManager, connMonitor))
                .asyncClient(new ApacheAsyncClient(asyncClient, manager, asyncMonitor));


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
                .httpClient(new Client(httpc, null, null))
                .asyncClient(new ApacheAsyncClient(asyncClient, null, null));

        Unirest.shutDown();

        verify(httpc).close();
        verify(asyncClient).close();
    }

    @Test
    public void willNotShutdownInactiveAsyncClient() throws IOException {
        CloseableHttpAsyncClient asyncClient = mock(CloseableHttpAsyncClient.class);
        when(asyncClient.isRunning()).thenReturn(false);

        Unirest.config().asyncClient(asyncClient);

        Unirest.shutDown();

        verify(asyncClient, never()).close();
    }

    @Test
    public void canDetectIfSystemIsRunning() {
        Unirest.get(MockServer.GET).asBinary();
        assertTrue(Unirest.isRunning());

        Unirest.shutDown();
        assertFalse(Unirest.isRunning());

        Unirest.get(MockServer.GET).asBinary();
        assertTrue(Unirest.isRunning());
    }

    @Test
    public void willReinitIfLibraryIsUsedAfterShutdown() {
        Unirest.shutDown();
        assertFalse(Unirest.isRunning());

        Unirest.get(MockServer.GET).asBinary();
        assertTrue(Unirest.isRunning());
    }

    @Test
    public void canGetTheCommonInstanceOfUnirest(){
        assertSame(Unirest.primaryInstance(), Unirest.primaryInstance());
        assertNotSame(Unirest.primaryInstance(), Unirest.spawnInstance());
        assertNotSame(Unirest.spawnInstance(), Unirest.spawnInstance());
    }

    @Test
    public void shouldReuseThreadPool() {
        int startingCount = ManagementFactory.getThreadMXBean().getThreadCount();
        IntStream.range(0,100).forEach(i -> {
            Unirest.config().reset().getClient();
            Unirest.config().getAsyncHttpClient();
        });
        assertThat(ManagementFactory.getThreadMXBean().getThreadCount(), is(lessThan(startingCount + 10)));
    }
}
