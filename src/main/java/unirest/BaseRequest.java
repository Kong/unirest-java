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

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class BaseRequest<R extends HttpRequest> implements HttpRequest<R> {

    private Optional<ObjectMapper> objectMapper = Optional.empty();
    protected Headers headers = new Headers();
    protected final Config config;
    protected HttpMethod method;
    protected Path url;

    BaseRequest(BaseRequest httpRequest) {
        this.config = httpRequest.config;
        this.method = httpRequest.method;
        this.url = httpRequest.url;
        this.headers.addAll(httpRequest.headers);
    }

    BaseRequest(Config config, HttpMethod method, String url) {
        this.config = config;
        this.method = method;
        this.url = new Path(url);
        headers.putAll(config.getDefaultHeaders());
    }

    @Override
    public R routeParam(String name, String value) {
        url.param(name, value);
        return (R)this;
    }

    @Override
    public R basicAuth(String username, String password) {
        header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
        return (R)this;
    }

    @Override
    public R accept(String value) {
        return header(HttpHeaders.ACCEPT, value);
    }

    @Override
    public R header(String name, String value) {
        this.headers.add(name.trim(), value);
        return (R)this;
    }

    @Override
    public R headerReplace(String name, String value) {
        if(this.headers.containsKey(name)) {
            List<String> repl = new ArrayList<>();
            repl.add(value);
            this.headers.replace(name.trim(), repl);
        } else {
            header(name, value);
        }
        return (R)this;
    }

    @Override
    public R headers(Map<String, String> headerMap) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
        }
        return (R)this;
    }

    @Override
    public R queryString(String name, Collection<?> value) {
        url.queryString(name, value);
        return (R)this;
    }

    @Override
    public R queryString(String name, Object value) {
        url.queryString(name, value);
        return (R)this;
    }

    @Override
    public R queryString(Map<String, Object> parameters) {
       url.queryString(parameters);
        return (R)this;
    }

    @Override
    public R withObjectMapper(ObjectMapper mapper) {
        Objects.requireNonNull(mapper, "ObjectMapper may not be null");
        this.objectMapper = Optional.of(mapper);
        return (R)this;
    }

    @Override
    public HttpResponse<String> asString() throws UnirestException {
        return request(StringResponse::new);
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync() {
        return requestAsync(StringResponse::new, new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback) {
        return requestAsync(StringResponse::new, CallbackFuture.wrap(callback));
    }

    @Override
    public HttpResponse<JsonNode> asJson() throws UnirestException {
        return request(JsonResponse::new);
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync() {
        return requestAsync(JsonResponse::new, new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback) {
        return requestAsync(JsonResponse::new, CallbackFuture.wrap(callback));
    }

    @Override
    public <T> HttpResponse<T> asObject(Class<? extends T> responseClass) throws UnirestException {
        return request(r -> new ObjectResponse<T>(getObjectMapper(), r, responseClass));
    }

    @Override
    public <T> HttpResponse<T> asObject(GenericType<T> genericType) throws UnirestException {
        return request(r -> new ObjectResponse<T>(getObjectMapper(), r, genericType));
    }

    @Override
    public <T> HttpResponse<T> asObject(Function<RawResponse, T> function) {
        return request(funcResponse(function));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Function<RawResponse, T> function) {
        return requestAsync(funcResponse(function), new CompletableFuture<>());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass) {
        return requestAsync(r -> new ObjectResponse<T>(getObjectMapper(), r, responseClass), new CompletableFuture<>());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass, Callback<T> callback) {
        return requestAsync(r -> new ObjectResponse<>(getObjectMapper(), r, responseClass), CallbackFuture.wrap(callback));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType) {
        return requestAsync(r -> new ObjectResponse<>(getObjectMapper(), r, genericType), new CompletableFuture<>());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType, Callback<T> callback) {
        return requestAsync(r -> new ObjectResponse<>(getObjectMapper(), r, genericType), CallbackFuture.wrap(callback));
    }

    private <T> Function<RawResponse, HttpResponse<T>> funcResponse(Function<RawResponse, T> function) {
        return r -> new BasicResponse<>(r, function.apply(r));
    }

    @Override
    public HttpResponse<InputStream> asBinary() throws UnirestException {
        return request(BinaryResponse::new);
    }

    @Override
    public CompletableFuture<HttpResponse<InputStream>> asBinaryAsync() {
        return requestAsync(BinaryResponse::new, new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<InputStream>> asBinaryAsync(Callback<InputStream> callback) {
        return requestAsync(BinaryResponse::new, CallbackFuture.wrap(callback));
    }

    @Override
    public void thenConsume(Consumer<RawResponse> consumer) {
        request(getConsumer(consumer));
    }

    @Override
    public void thenConsumeAsync(Consumer<RawResponse> consumer) {
        requestAsync(getConsumer(consumer), new CompletableFuture<>());
    }

    @Override
    public HttpResponse<File> asFile(String path) {
        return request(r -> new FileResponse(r, path));
    }

    @Override
    public CompletableFuture<HttpResponse<File>> asFileAsync(String path) {
        return requestAsync(r -> new FileResponse(r, path), new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<File>> asFileAsync(String path, Callback<File> callback) {
        return requestAsync(r -> new FileResponse(r, path), CallbackFuture.wrap(callback));
    }

    private Function<RawResponse, HttpResponse<Object>> getConsumer(Consumer<RawResponse> consumer) {
        return r -> {
            consumer.accept(r);
            return null;
        };
    }

    private <T> CompletableFuture<HttpResponse<T>> requestAsync(
            Function<RawResponse, HttpResponse<T>> transformer,
            CompletableFuture<HttpResponse<T>> callback) {

        Objects.requireNonNull(callback);

        HttpUriRequest requestObj = new RequestPrep(this, true).prepare();

        config.getAsyncHttpClient()
                .execute(requestObj, new FutureCallback<org.apache.http.HttpResponse>() {
                    @Override
                    public void completed(org.apache.http.HttpResponse httpResponse) {
                        callback.complete(transformer.apply(new ApacheResponse(httpResponse)));
                    }

                    @Override
                    public void failed(Exception e) {
                        callback.completeExceptionally(e);
                    }

                    @Override
                    public void cancelled() {
                        callback.completeExceptionally(new UnirestException("canceled"));
                    }
                });
        return callback;
    }


    @Override
    public HttpMethod getHttpMethod() {
        return method;
    }

    @Override
    public String getUrl() {
        return url.toString();
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public Body getBody() {
        return null;
    }

    private <T> HttpResponse<T> request(Function<RawResponse, HttpResponse<T>> transformer) {

        HttpRequestBase requestObj = new RequestPrep(this, false).prepare();
        HttpClient client = config.getClient();

        try {
            org.apache.http.HttpResponse execute = client.execute(requestObj);
            HttpResponse<T> httpResponse = transformer.apply(new ApacheResponse(execute));
            requestObj.releaseConnection();
            return httpResponse;
        } catch (Exception e) {
            throw new UnirestException(e);
        } finally {
            requestObj.releaseConnection();
        }
    }

    private ObjectMapper getObjectMapper() {
        return objectMapper.orElseGet(config::getObjectMapper);
    }
}
