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

import unirest.Unirest;
import unirest.AsyncIdleConnectionMonitorThread;
import unirest.Options;
import unirest.SyncIdleConnectionMonitorThread;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.Test;

import java.io.IOException;

import static unirest.Option.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class LifeCycleTest extends BddTest {

    @Test
    public void testShutdown() throws IOException {
        CloseableHttpClient httpc = mock(CloseableHttpClient.class);
        SyncIdleConnectionMonitorThread connMonitor = mock(SyncIdleConnectionMonitorThread.class);
        CloseableHttpAsyncClient asyncClient = mock(CloseableHttpAsyncClient.class);
        when(asyncClient.isRunning()).thenReturn(true);
        AsyncIdleConnectionMonitorThread asyncMonitor = mock(AsyncIdleConnectionMonitorThread.class);

        Options.setOption(HTTPCLIENT, httpc);
        Options.setOption(SYNC_MONITOR, connMonitor);
        Options.setOption(ASYNCHTTPCLIENT, asyncClient);
        Options.setOption(ASYNC_MONITOR, asyncMonitor);

        Unirest.shutdown();

        verify(httpc).close();
        verify(connMonitor).interrupt();
        verify(asyncClient).close();
        verify(asyncMonitor).interrupt();
    }

    @Test
    public void DoesNotBombOnNullOptions() throws IOException {
        Options.setOption(HTTPCLIENT, null);
        Options.setOption(SYNC_MONITOR, null);
        Options.setOption(ASYNCHTTPCLIENT, null);
        Options.setOption(ASYNC_MONITOR, null);

        Unirest.shutdown();
    }

    @Test
    public void willNotShutdownInactiveAsyncClient() throws IOException {
        CloseableHttpAsyncClient asyncClient = mock(CloseableHttpAsyncClient.class);
        when(asyncClient.isRunning()).thenReturn(false);

        Options.setOption(ASYNCHTTPCLIENT, asyncClient);

        Unirest.shutdown();

        verify(asyncClient, never()).close();
    }

    @Test
    public void canDetectIfSystemIsRunning() {
        Unirest.get(MockServer.GET).asBinary();
        assertTrue(Unirest.isRunning());

        Unirest.shutdown();
        assertFalse(Unirest.isRunning());

        Options.init();
        assertTrue(Unirest.isRunning());
    }

    @Test
    public void willReinitIfLibraryIsUsedAfterShutdown() {
        Unirest.shutdown();
        assertFalse(Unirest.isRunning());

        Unirest.get(MockServer.GET).asBinary();
        assertTrue(Unirest.isRunning());
    }
}
