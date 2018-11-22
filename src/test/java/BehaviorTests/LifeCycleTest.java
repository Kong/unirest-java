package BehaviorTests;

import com.github.paweladamski.httpclientmock.HttpClientMock;
import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.Unirest;
import io.github.openunirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import io.github.openunirest.http.options.Options;
import io.github.openunirest.http.utils.SyncIdleConnectionMonitorThread;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static io.github.openunirest.http.Unirest.get;
import static io.github.openunirest.http.options.Option.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LifeCycleTest extends BddTest {

    @Test
    public void settingACustomClient() throws ExecutionException, InterruptedException {
        HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost/getme").doReturn(202, "Howdy Ho!");

        Unirest.setHttpClient(httpClientMock);
        HttpResponse<String> result =  Unirest.get("http://localhost/getme").asString();

        assertEquals(202, result.getStatus());
        assertEquals("Howdy Ho!", result.getBody());

        Unirest.get(MockServer.GET)
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertOk();
    }

    @Test
    public void settingClientAfterClientHasAlreadyBeenSet() {
        HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost/getme").doReturn(202, "Howdy Ho!");

        assertEquals(200, Unirest.get(MockServer.GET).asString().getStatus());

        Unirest.setHttpClient(httpClientMock);
        HttpResponse<String> result =  Unirest.get("http://localhost/getme").asString();

        assertEquals(202, result.getStatus());
        assertEquals("Howdy Ho!", result.getBody());
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
        get(MockServer.GET).asBinary();
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

        get(MockServer.GET).asBinary();
        assertTrue(Unirest.isRunning());
    }
}
