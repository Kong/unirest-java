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

package kong.unirest.java;

import kong.unirest.*;
import kong.unirest.Proxy;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.*;

import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static kong.unirest.HeaderNames.*;


public class JavaClient implements Client, AsyncClient {

    private final Config config;
    private final HttpClient client;
    private boolean hookset = false;

    public JavaClient(Config config) {
        this.config = config;
        HttpClient.Builder builder = HttpClient.newBuilder()
                .followRedirects(redirectPolicy(config));
        SSLParameters params = new SSLParameters();
        if(!config.isVerifySsl()){
            builder.sslContext(NeverUseInProdTrustManager.create());
        } else if (config.getKeystore() != null){
            builder.sslContext(getSslContext(config.getKeystore()));
        } else if(config.getSslContext() != null){
            builder.sslContext(config.getSslContext());
        }
        if(config.getProtocols() != null){
            params.setProtocols(config.getProtocols());
        }if(config.getCiphers() != null){
            params.setCipherSuites(config.getCiphers());
        }
        if(config.getEnabledCookieManagement()){
            builder = builder.cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        }
        if(config.getProxy() != null){
            createProxy(builder, config.getProxy());
        }
        if(config.useSystemProperties()){
            builder.proxy(ProxySelector.getDefault());
        }
        builder.sslParameters(params);
        client = builder.build();
    }

    public JavaClient(Config config, HttpClient client){
        this.config = config;
        this.client = client;
    }

    private void createProxy(HttpClient.Builder builder, Proxy proxy) {
        InetSocketAddress address = InetSocketAddress.createUnresolved(proxy.getHost(), proxy.getPort());
        builder.proxy(ProxySelector.of(address));
        if(proxy.isAuthenticated()){
            builder.authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxy.getUsername(), proxy.getPassword().toCharArray());
                }
            });
        }
    }

    private SSLContext getSslContext(KeyStore ks) {
        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            char[] pass = Optional.ofNullable(config.getKeyStorePassword())
                    .map(String::toCharArray)
                    .orElse(null);
            return SSLContextBuilder.create()
                    .loadKeyMaterial(config.getKeystore(), pass)
                    .build();
        }catch (Exception e){
            throw new UnirestConfigException(e);
        }
    }

    private HttpClient.Redirect redirectPolicy(Config config) {
        if(config.getFollowRedirects()){
            return HttpClient.Redirect.NORMAL;
        }
        return HttpClient.Redirect.NEVER;
    }

    @Override
    public Object getClient() {
        return client;
    }

    @Override
    public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer) {
        HttpRequestSummary reqSum = request.toSummary();
        config.getUniInterceptor().onRequest(request, config);
        java.net.http.HttpRequest requestObj = getRequest(request);
        MetricContext metric = config.getMetric().begin(reqSum);
        try {
            //HttpHost host = determineTarget(requestObj, request.getHeaders());
            java.net.http.HttpResponse<InputStream> execute = client.send(requestObj,
                    responseInfo -> java.net.http.HttpResponse.BodySubscribers.ofInputStream());
            JavaResponse t = new JavaResponse(execute, config);
            metric.complete(t.toSummary(), null);
            HttpResponse<T> httpResponse = transformBody(transformer, t);
            //requestObj.releaseConnection();
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
                    .method(request.getHttpMethod().name(), new BodyBuilder(config, request).getBody());
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

            return jreq.build();
        }catch (RuntimeException e){
            if (e instanceof UnirestException){
                throw e;
            } else {
                throw new UnirestException(e);
            }
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
            JavaResponse t = new JavaResponse(h, config);
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
    public Stream<Exception> close() {
        return Stream.of();
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void registerShutdownHook() {
        if(!hookset) {
            hookset = true;
            Runtime.getRuntime().addShutdownHook(new Thread(this::close, "Unirest Client Shutdown Hook"));
        }
    }



    protected <T> HttpResponse<T> transformBody(Function<RawResponse, HttpResponse<T>> transformer, RawResponse rr) {
        try {
            return transformer.apply(rr);
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
