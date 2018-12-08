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

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

class ClientFactory {


    public AsyncClient buildAsyncClient(Config config) {

        try {
            PoolingNHttpClientConnectionManager manager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
            manager.setMaxTotal(config.getMaxConnections());
            manager.setDefaultMaxPerRoute(config.getMaxPerRoutes());

            HttpAsyncClientBuilder ab = HttpAsyncClientBuilder.create()
                    .setDefaultRequestConfig(getRequestConfig(config))
                    .setConnectionManager(manager)
                    .setDefaultCredentialsProvider(config.getProxyCreds())
                    .useSystemProperties();

            if(config.useSystemProperties()){
                ab.useSystemProperties();
            }
            if (!config.getFollowRedirects()) {
                ab.setRedirectStrategy(new NoRedirects());
            }
            if (!config.getEnabledCookieManagement()) {
                ab.disableCookieManagement();
            }
            config.getInterceptors().forEach(ab::addInterceptorFirst);

            CloseableHttpAsyncClient build = ab.build();
            build.start();
            AsyncIdleConnectionMonitorThread syncMonitor = new AsyncIdleConnectionMonitorThread(manager);
            syncMonitor.tryStart();
            return new AsyncClient(build, manager, syncMonitor);

        } catch (IOReactorException e) {
            throw new UnirestConfigException(e);
        }
    }

    public Client buildHttpClient(Config config) {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        SyncIdleConnectionMonitorThread syncMonitor = new SyncIdleConnectionMonitorThread(manager);
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

        return new Client(cb.build(), manager, syncMonitor);
    }

    private RequestConfig getRequestConfig(Config config) {
        Integer connectionTimeout = config.getConnectionTimeout();
        Integer socketTimeout = config.getSocketTimeout();
        HttpHost proxy = config.getProxy();
        return RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(socketTimeout)
                .setProxy(proxy)
                .build();
    }
}
