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

package kong.unirest.apache;

import BehaviorTests.BddTest;
import BehaviorTests.MockServer;
import com.google.common.collect.Sets;
import kong.unirest.*;
import kong.unirest.apache.AsyncIdleConnectionMonitorThread;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LifeCycleTest extends BddTest {

    @Mock
    private CloseableHttpClient httpc;
    @Mock
    private PoolingHttpClientConnectionManager clientManager;
    @Mock
    private CloseableHttpAsyncClient asyncClient;
    @Mock
    private AsyncIdleConnectionMonitorThread asyncMonitor;
    @Mock
    private PoolingNHttpClientConnectionManager manager;

    @Test
    void willNotShutdownInactiveAsyncClient() throws IOException {
        CloseableHttpAsyncClient asyncClient = mock(CloseableHttpAsyncClient.class);
        when(asyncClient.isRunning()).thenReturn(false);

        Unirest.config().asyncClient(asyncClient);

        Unirest.shutDown();

        verify(asyncClient, never()).close();
    }

    @Test
    void canDetectIfSystemIsRunning() {
        Unirest.get(MockServer.GET).asEmpty();
        assertTrue(Unirest.isRunning());

        Unirest.shutDown();
        assertFalse(Unirest.isRunning());

        Unirest.get(MockServer.GET).asEmpty();
        assertTrue(Unirest.isRunning());
    }

    @Test
    void willReinitIfLibraryIsUsedAfterShutdown() {
        Unirest.shutDown();
        assertFalse(Unirest.isRunning());

        Unirest.get(MockServer.GET).asEmpty();
        assertTrue(Unirest.isRunning());
    }

    @Test
    void canGetTheCommonInstanceOfUnirest(){
        assertSame(Unirest.primaryInstance(), Unirest.primaryInstance());
        assertNotSame(Unirest.primaryInstance(), Unirest.spawnInstance());
        assertNotSame(Unirest.spawnInstance(), Unirest.spawnInstance());
    }

    @Test
    void shouldReuseThreadPool() {
        int startingCount = ManagementFactory.getThreadMXBean().getThreadCount();
        IntStream.range(0,100).forEach(i -> {
            Unirest.config().reset().getClient();
            Unirest.config().getAsyncClient();
        });
        assertTrue(ManagementFactory.getThreadMXBean().getThreadCount() <= startingCount + 10);
    }

    @Test
    void testUnirestInstanceIsShutdownWhenClosed() {
        UnirestInstance reference;
        try (UnirestInstance instance = new UnirestInstance(new Config().setDefaultHeader("foo", "bar"))) {
            reference = instance;
            assertEquals(1, reference.config().getDefaultHeaders().size());
            assertEquals("bar", reference.config().getDefaultHeaders().get("foo").get(0));
        }
        assertEquals(0, reference.config().getDefaultHeaders().size());
    }
}
