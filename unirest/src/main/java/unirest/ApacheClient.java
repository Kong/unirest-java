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

package unirest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.Closeable;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

class ApacheClient extends BaseApacheClient implements Client {
    private final HttpClient client;
    private final Config config;
    private final PoolingHttpClientConnectionManager manager;
    private final SyncIdleConnectionMonitorThread syncMonitor;

    ApacheClient(Config config) {
        this.config = config;
        manager = new PoolingHttpClientConnectionManager();
        syncMonitor = new SyncIdleConnectionMonitorThread(manager);
        syncMonitor.start();

        HttpClientBuilder cb = HttpClientBuilder.create()
                .setDefaultRequestConfig(getRequestConfig(config))
                .setDefaultCredentialsProvider(config.getProxyCreds())
                .setConnectionManager(manager)
                .useSystemProperties();

        if(config.useSystemProperties()){
            cb.useSystemProperties();
        }
        if (!config.getFollowRedirects()) {
            cb.disableRedirectHandling();
        }
        if (!config.getEnabledCookieManagement()) {
            cb.disableCookieManagement();
        }
        config.getInterceptors().stream().forEach(cb::addInterceptorFirst);
        client = cb.build();
    }

    ApacheClient(HttpClient httpClient, Config config, PoolingHttpClientConnectionManager clientManager, SyncIdleConnectionMonitorThread connMonitor) {
        this.client = httpClient;
        this.config = config;
        this.manager = clientManager;
        this.syncMonitor = connMonitor;
    }


    @Override
    public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer) {

        HttpRequestBase requestObj = new RequestPrep(request, false).prepare();

        try {
            org.apache.http.HttpResponse execute = client.execute(requestObj);
            HttpResponse<T> httpResponse = transformer.apply(new ApacheResponse(execute, config));
            requestObj.releaseConnection();
            return httpResponse;
        } catch (Exception e) {
            throw new UnirestException(e);
        } finally {
            requestObj.releaseConnection();
        }
    }

    @Override
    public HttpClient getClient() {
        return client;
    }

    public PoolingHttpClientConnectionManager getManager() {
        return manager;
    }

    public SyncIdleConnectionMonitorThread getSyncMonitor() {
        return syncMonitor;
    }

    @Override
    public Stream<Exception> close() {
        return Util.collectExceptions(Util.tryCast(client, CloseableHttpClient.class)
                        .map(c -> Util.tryDo(c, Closeable::close))
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                Util.tryDo(manager, m -> m.close()),
                Util.tryDo(syncMonitor, i -> i.interrupt())
        );
    }

}
