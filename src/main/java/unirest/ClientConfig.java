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

package unirest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.Closeable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ClientConfig {
    private final HttpClient client;
    private final PoolingHttpClientConnectionManager manager;
    private final SyncIdleConnectionMonitorThread syncMonitor;

    public ClientConfig(HttpClient client,
                        PoolingHttpClientConnectionManager manager,
                        SyncIdleConnectionMonitorThread syncMonitor) {
        Objects.requireNonNull(client, "Client may not be null");
        this.client = client;
        this.manager = manager;
        this.syncMonitor = syncMonitor;
    }

    public HttpClient getClient() {
        return client;
    }

    public PoolingHttpClientConnectionManager getManager() {
        return manager;
    }

    public SyncIdleConnectionMonitorThread getSyncMonitor() {
        return syncMonitor;
    }

    public Stream<Exception> close() {
        return Config.collectExceptions(Util.tryCast(client, CloseableHttpClient.class)
                        .map(c -> Util.tryDo(c, Closeable::close))
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                Util.tryDo(manager, m -> m.close()),
                Util.tryDo(syncMonitor, i -> i.interrupt())
        );

    }

}
