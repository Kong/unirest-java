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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodySubscribers.ofInputStream;
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
        var reqSum = request.toSummary();
        config.getUniInterceptor().onRequest(request, config);
        var requestObj = getRequest(request);
        var metric = config.getMetric().begin(reqSum);
        try {
            var execute = client.send(requestObj, r -> ofInputStream());
            var javaResponse = new JavaResponse(execute, config, reqSum);
            metric.complete(javaResponse.toSummary(), null);
            var httpResponse = transformBody(transformer, javaResponse);
            config.getUniInterceptor().onResponse(httpResponse, reqSum, config);
            return httpResponse;
        } catch (Exception e) {
            metric.complete(null, e);
            return (HttpResponse<T>) config.getUniInterceptor().onFail(e, reqSum, config);
        }
    }

    private java.net.http.HttpRequest getRequest(HttpRequest<?> request) {
        try {
            var url = URI.create(request.getUrl());
            var jreq = newBuilder(url)
                    .version(request.getVersion() != null ? request.getVersion() : config.getVersion())
                    .method(
                            request.getHttpMethod().name(),
                            new BodyBuilder(request).getBody()
                    );

            if(!Objects.isNull(request.getRequestTimeout())){
                jreq.timeout(Duration.ofMillis(request.getRequestTimeout()));
            }
            
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
        request.getHeaders().all().forEach(h -> jreq.header(h.getName(), validHeaderValue(h)));
        if (request.getBody().isPresent() && !request.getHeaders().containsKey(CONTENT_TYPE)) {
            var value = "text/plain";
            var charset = request.getBody().get().getCharset();
            if (charset != null) {
                value = value + "; charset=" + charset;
            }
            jreq.header(CONTENT_TYPE, value);
        }
        if(!request.getHeaders().containsKey(ACCEPT_ENCODING) && config.isRequestCompressionOn()){
            jreq.header(ACCEPT_ENCODING, "gzip");
        }
    }

    // The JDK rejects the same characters, but its IllegalArgumentException quotes the whole value,
    // and header values are often credentials (e.g. a token read from a file with its trailing newline).
    static String validHeaderValue(Header header) {
        String value = header.getValue();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c > '\u00ff' || c == '\u007f' || (c < ' ' && c != '\t')) {
                throw new IllegalArgumentException(String.format("invalid value for header \"%s\": illegal character 0x%02x at index %d",
                        header.getName(), (int) c, i));
            }
        }
        return value;
    }


    @Override
    public <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, CompletableFuture<HttpResponse<T>> callback, Class<?> resultType) {
        var reqSum = request.toSummary();
        config.getUniInterceptor().onRequest(request, config);
        var requestObj = getRequest(request);
        var metric = config.getMetric().begin(reqSum);

        var execute = client.sendAsync(requestObj,
                java.net.http.HttpResponse.BodyHandlers.ofInputStream());
        callback.whenComplete((response, error) -> {
            if (callback.isCancelled()) {
                execute.thenAccept(h -> closeCancelledBody(h.body(), error));
                execute.cancel(true);
            }
        });
        execute.thenAcceptAsync(h -> {
            var t = new JavaResponse(h, config, reqSum);
            metric.complete(t.toSummary(), null);
            if (callback.isCancelled()) {
                return;
            }
            var httpResponse = transformBody(transformer, t);
            if (callback.isCancelled()) {
                return;
            }
            config.getUniInterceptor().onResponse(httpResponse, reqSum, config);
            callback.complete(httpResponse);
        }).exceptionally(e -> handleFailure(e, reqSum, metric, callback))
                .whenComplete((ignored, failure) -> {
                    if (failure != null) {
                        callback.completeExceptionally(failure);
                    }
                });
        return callback;
    }

    private void closeCancelledBody(InputStream body, Throwable cancellation) {
        try {
            body.close();
        } catch (IOException failure) {
            cancellation.addSuppressed(failure);
        }
    }

    private <T> Void handleFailure(Throwable error, HttpRequestSummary request, MetricContext metric, CompletableFuture<HttpResponse<T>> callback) {
        var cause = error instanceof CompletionException ? error.getCause() : error;
        var ex = new UnirestException(error);
        metric.complete(null, ex);
        if (callback.isCancelled() || cause instanceof CancellationException) {
            callback.cancel(false);
            return null;
        }
        try {
            HttpResponse<T> response = (HttpResponse<T>) config.getUniInterceptor().onFail(ex, request, config);
            callback.complete(response);
        } catch (Exception failure) {
            callback.completeExceptionally(failure);
        }
        return null;
    }

    @Override
    public WebSocketResponse websocket(WebSocketRequest request, WebSocket.Listener listener) {
        var b = client.newWebSocketBuilder();
        request.getHeaders().all().forEach(h -> b.header(h.getName(), validHeaderValue(h)));
        return new WebSocketResponse(b.buildAsync(URI.create(request.getUrl()), listener), listener);
    }

    @Override
    public CompletableFuture<Void> sse(SseRequest request, SseHandler handler) {
        var r = SseRequestBuilder.getHttpRequest(request);

        return client.sendAsync(r, java.net.http.HttpResponse.BodyHandlers.ofLines())
                .thenApply(this::checkSseResponse)
                .thenAcceptAsync(new SseResponseHandler(config, handler));
    }

    @Override
    public Stream<Event> sse(SseRequest request) {

        try {
            var r = SseRequestBuilder.getHttpRequest(request);
            var stream = client.send(r, java.net.http.HttpResponse.BodyHandlers.ofLines());
            var handler = new SseResponseHandler(config, event -> {});
            return handler.map(checkSseResponse(stream).body());
        } catch (UnirestException e) {
            throw e;
        } catch (Exception e) {
            throw new UnirestException(e);
        }
    }

    private java.net.http.HttpResponse<Stream<String>> checkSseResponse(
            java.net.http.HttpResponse<Stream<String>> response) {
        if (response.statusCode() != 200) {
            response.body().close();
            throw new UnirestException("SSE connection failed with HTTP status " + response.statusCode());
        }
        return response;
    }

    protected <T> HttpResponse<T> transformBody(Function<RawResponse, HttpResponse<T>> transformer, RawResponse rr) {
        try {
            return transformer.apply(rr);
        }catch (UnrecoverableException ue){
            return new BasicResponse(rr, "", ue);
        }catch (RuntimeException e){
            var originalBody = recoverBody(rr);
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
