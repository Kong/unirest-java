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

package kong.unirest.core.java;

import kong.unirest.core.*;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static kong.unirest.core.HeaderNames.*;


public class JavaClient implements Client {

    private final Config config;
    private final HttpClient client;

    public JavaClient(Config config) {
        this.config = config;
        this.client = new JavaClientBuilder().apply(config);
    }

    public JavaClient(Config config, HttpClient client){
        this.config = config;
        this.client = client;
    }

    @Override
    public HttpClient getClient() {
        return client;
    }

    @Override
    public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, Class<?> resultType) {
        HttpRequestSummary reqSum = request.toSummary();
        config.getUniInterceptor().onRequest(request, config);
        java.net.http.HttpRequest requestObj = getRequest(request);
        MetricContext metric = config.getMetric().begin(reqSum);
        try {
            java.net.http.HttpResponse<InputStream> execute = client.send(requestObj,
                    responseInfo -> java.net.http.HttpResponse.BodySubscribers.ofInputStream());
            JavaResponse t = new JavaResponse(execute, config, reqSum);
            metric.complete(t.toSummary(), null);
            HttpResponse<T> httpResponse = transformBody(transformer, t);
            config.getUniInterceptor().onResponse(httpResponse, reqSum, config);
            return httpResponse;
        } catch (Exception e) {
            metric.complete(null, e);
            return (HttpResponse<T>) config.getUniInterceptor().onFail(e, reqSum, config);
        }
    }

    private java.net.http.HttpRequest getRequest(HttpRequest<?> request) {
        try {
            URI url = URI.create(request.getUrl());
            java.net.http.HttpRequest.Builder jreq = java.net.http.HttpRequest.newBuilder(url)
                    .version(HttpClient.Version.HTTP_2)
                    .method(
                            request.getHttpMethod().name(),
                            new BodyBuilder(request).getBody()
                    ).timeout(Duration.ofMillis(request.getConnectTimeout()));
            
            setHeaders(request, jreq);

            return jreq.build();
        }catch (RuntimeException e){
            if (e instanceof UnirestException){
                throw e;
            } else {
                throw new UnirestException(e);
            }
        }
    }

    private void setHeaders(HttpRequest<?> request, java.net.http.HttpRequest.Builder jreq) {
        request.getHeaders().all().forEach(h -> jreq.header(h.getName(), h.getValue()));
        if (request.getBody().isPresent() && !request.getHeaders().containsKey(CONTENT_TYPE)) {
            String value = "text/plain";
            Charset charset = request.getBody().get().getCharset();
            if (charset != null) {
                value = value + "; charset=" + charset.toString();
            }
            jreq.header(CONTENT_TYPE, value);
        }
        if(!request.getHeaders().containsKey(CONTENT_ENCODING) && config.isRequestCompressionOn()){
            jreq.header(ACCEPT_ENCODING, "gzip");
        }
    }


    @Override
    public <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, CompletableFuture<HttpResponse<T>> callback, Class<?> resultType) {
        HttpRequestSummary reqSum = request.toSummary();
        config.getUniInterceptor().onRequest(request, config);
        java.net.http.HttpRequest requestObj = getRequest(request);
        MetricContext metric = config.getMetric().begin(reqSum);

        CompletableFuture<java.net.http.HttpResponse<InputStream>> execute = client.sendAsync(requestObj,
                java.net.http.HttpResponse.BodyHandlers.ofInputStream());

        return execute.thenApplyAsync(h -> {
            JavaResponse t = new JavaResponse(h, config, reqSum);
            metric.complete(t.toSummary(), null);
            HttpResponse<T> httpResponse = transformBody(transformer, t);
            config.getUniInterceptor().onResponse(httpResponse, reqSum, config);
            callback.complete(httpResponse);
            return httpResponse;
        }).exceptionally(e -> {
            UnirestException ex = new UnirestException(e);
            metric.complete(null, ex);
            try {
                HttpResponse r = config.getUniInterceptor().onFail(ex, reqSum, config);
                callback.complete(r);
                return r;
            } catch (Exception ee){
                callback.completeExceptionally(e);
            }

            return new FailedResponse(ex);
        });
    }

    @Override
    public WebSocketResponse websocket(WebSocketRequest request, WebSocket.Listener listener) {
        WebSocket.Builder b = client.newWebSocketBuilder();
        request.getHeaders().all().forEach(h -> b.header(h.getName(), h.getValue()));
        return new WebSocketResponse(b.buildAsync(URI.create(request.getUrl()), listener), listener);
    }

    protected <T> HttpResponse<T> transformBody(Function<RawResponse, HttpResponse<T>> transformer, RawResponse rr) {
        try {
            return transformer.apply(rr);
        }catch (UnrecoverableException ue){
            return new BasicResponse(rr, "", ue);
        }catch (RuntimeException e){
            String originalBody = recoverBody(rr);
            return new BasicResponse(rr, originalBody, e);
        }
    }

    private String recoverBody(RawResponse rr){
        try {
            return rr.getContentAsString();
        }catch (Exception e){
            return null;
        }
    }
}
