package BehaviorTests;

import io.github.openunirest.http.Unirest;
import io.github.openunirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import io.github.openunirest.http.options.Options;
import io.github.openunirest.http.utils.SyncIdleConnectionMonitorThread;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static io.github.openunirest.http.options.Option.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LifeCycleTest extends BddTest {
    @After
    public void tearDown() throws Exception {
        Options.reset();
    }

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
}
