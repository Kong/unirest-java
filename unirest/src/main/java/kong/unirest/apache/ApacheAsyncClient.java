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

import kong.unirest.*;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;


public class ApacheAsyncClient extends BaseApacheClient implements AsyncClient {
    private ApacheAsyncConfig apache;

    public ApacheAsyncClient(Config config) {
        this.apache = new ApacheAsyncConfig(config);
    }

    public ApacheAsyncClient(HttpAsyncClient client, Config config) {
        this.apache = new ApacheAsyncConfig(client, config);
    }

    @Deprecated
    public ApacheAsyncClient(HttpAsyncClient client,
                             Config config,
                             PoolingNHttpClientConnectionManager manager,
                             AsyncIdleConnectionMonitorThread monitor) {
        this.apache = new ApacheAsyncConfig(client, config, manager, monitor);
    }

    @Override
    public void registerShutdownHook() {
        this.apache.registerShutdownHook();
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> request(
            HttpRequest request,
            Function<RawResponse, HttpResponse<T>> transformer,
            CompletableFuture<HttpResponse<T>> callback) {

        Objects.requireNonNull(callback);
        apache.config.getUniInterceptor().onRequest(request, apache.config);
        HttpUriRequest requestObj = new RequestPrep(request, apache.config, true).prepare(configFactory);
        HttpRequestSummary reqSum = request.toSummary();
        MetricContext metric = apache.config.getMetric().begin(reqSum);
        HttpHost host = determineTarget(requestObj, request.getHeaders());
        apache.client.execute(new BasicAsyncRequestProducer(host, requestObj), new BasicAsyncResponseConsumer(), new FutureCallback<org.apache.http.HttpResponse>() {
            @Override
            public void completed(org.apache.http.HttpResponse httpResponse) {
                ApacheResponse t = new ApacheResponse(httpResponse, apache.config);
                metric.complete(t.toSummary(), null);
                HttpResponse<T> response = transformBody(transformer, t);
                apache.config.getUniInterceptor().onResponse(response, reqSum, apache.config);
                callback.complete(response);
            }

            @Override
            public void failed(Exception e) {
                metric.complete(null, e);
                try {
                    HttpResponse r = apache.config.getUniInterceptor().onFail(e, reqSum, apache.config);
                    callback.complete(r);
                } catch (Exception ee){
                    callback.completeExceptionally(e);
                }
            }

            @Override
            public void cancelled() {
                UnirestException canceled = new UnirestException("canceled");
                metric.complete(null, canceled);
                callback.completeExceptionally(canceled);
                apache.config.getUniInterceptor().onFail(canceled, reqSum, apache.config);
            }
        });
        return callback;
    }

    @Override
    public boolean isRunning() {
        return apache.isRunning();
    }

    @Override
    public HttpAsyncClient getClient() {
        return apache.getClient();
    }

    @Override
    public Stream<Exception> close() {
        return apache.close();
    }

    public static Builder builder(HttpAsyncClient client) {
        return new Builder(client);
    }

    public static class Builder implements Function<Config, AsyncClient> {
        private HttpAsyncClient asyncClient;
        private RequestConfigFactory cf;

        public Builder(HttpAsyncClient client) {
            this.asyncClient = client;
        }

        @Override
        public AsyncClient apply(Config config) {
            ApacheAsyncClient client = new ApacheAsyncClient(this.asyncClient, config);
            if (cf != null) {
                client.setConfigFactory(cf);
            }
            return client;
        }

        public Builder withRequestConfig(RequestConfigFactory factory) {
            Objects.requireNonNull(factory);
            this.cf = factory;
            return this;
        }
    }
}
