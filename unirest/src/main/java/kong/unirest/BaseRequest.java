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

package kong.unirest;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class BaseRequest<R extends HttpRequest> implements HttpRequest<R> {

    private Optional<ObjectMapper> objectMapper = Optional.empty();
    private String responseEncoding;
    protected Headers headers = new Headers();
    protected final Config config;
    protected HttpMethod method;
    protected Path url;
    private Integer socketTimeout;
    private Integer connectTimeout;
    private Proxy proxy;

    BaseRequest(BaseRequest httpRequest) {
        this.config = httpRequest.config;
        this.method = httpRequest.method;
        this.url = httpRequest.url;
        this.headers.putAll(httpRequest.headers);
        this.socketTimeout = httpRequest.socketTimeout;
        this.connectTimeout = httpRequest.connectTimeout;
        this.proxy = httpRequest.proxy;
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
    public R routeParam(Map<String, Object> params) {
        url.param(params);
        return (R)this;
    }

    @Override
    public R basicAuth(String username, String password) {
        this.headers.replace("Authorization", Util.toBasicAuthValue(username, password));
        return (R)this;
    }

    @Override
    public R accept(String value) {
        return header(HeaderNames.ACCEPT, value);
    }

    @Override
    public R responseEncoding(String encoding) {
        this.responseEncoding = encoding;
        return (R)this;
    }

    @Override
    public R header(String name, String value) {
        this.headers.add(name.trim(), value);
        return (R)this;
    }

    @Override
    public R headerReplace(String name, String value) {
        this.headers.replace(name, value);
        return (R)this;
    }

    @Override
    public R headers(Map<String, String> headerMap) {
        if (headerMap != null) {
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
    public R socketTimeout(int millies) {
        this.socketTimeout = millies;
        return (R)this;
    }

    @Override
    public R connectTimeout(int millies) {
        this.connectTimeout = millies;
        return (R)this;
    }

    @Override
    public R proxy(String host, int port) {
        this.proxy = new Proxy(host, port);
        return (R)this;
    }

    @Override
    public R withObjectMapper(ObjectMapper mapper) {
        Objects.requireNonNull(mapper, "ObjectMapper may not be null");
        this.objectMapper = Optional.of(mapper);
        return (R)this;
    }

    @Override
    public HttpResponse asEmpty() {
        return config.getClient().request(this, EmptyResponse::new);
    }

    @Override
    public CompletableFuture<HttpResponse<Empty>> asEmptyAsync() {
        return config.getAsyncClient()
                .request(this, EmptyResponse::new, new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<Empty>> asEmptyAsync(Callback<Empty> callback) {
        return config.getAsyncClient()
                .request(this, EmptyResponse::new, CallbackFuture.wrap(callback));
    }

    @Override
    public HttpResponse<String> asString() throws UnirestException {
        return config.getClient().request(this, r -> new StringResponse(r, responseEncoding));
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync() {
        return config.getAsyncClient()
                .request(this, r -> new StringResponse(r, responseEncoding), new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback) {
        return config.getAsyncClient()
                .request(this, r -> new StringResponse(r, responseEncoding), CallbackFuture.wrap(callback));
    }

    @Override
    public HttpResponse<JsonNode> asJson() throws UnirestException {
        return config.getClient().request(this, JsonResponse::new);
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync() {

        return config.getAsyncClient().request(this, JsonResponse::new, new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback) {

        return config.getAsyncClient().request(this, JsonResponse::new, CallbackFuture.wrap(callback));
    }

    @Override
    public <T> HttpResponse<T> asObject(Class<? extends T> responseClass) throws UnirestException {
        return config.getClient().request(this, r -> new ObjectResponse<T>(getObjectMapper(), r, responseClass));
    }

    @Override
    public <T> HttpResponse<T> asObject(GenericType<T> genericType) throws UnirestException {
        return config.getClient().request(this, r -> new ObjectResponse<T>(getObjectMapper(), r, genericType));
    }

    @Override
    public <T> HttpResponse<T> asObject(Function<RawResponse, T> function) {
        return config.getClient().request(this, funcResponse(function));
    }

    @Override
    public <T, E> HttpEither<T, E> asObject(Class<? extends T> responseClass, Class<? extends E> errorClass) {
        ObjectMapper om = getObjectMapper();
        HttpResponse<T> response = config.getClient().request(this, r -> new ObjectResponse<T>(om, r, responseClass));
        return new Either(
                (BaseResponse) response,
                om, errorClass);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Function<RawResponse, T> function) {

        return config.getAsyncClient().request(this, funcResponse(function), new CompletableFuture<>());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass) {

        return config.getAsyncClient().request(this, r -> new ObjectResponse<T>(getObjectMapper(), r, responseClass), new CompletableFuture<>());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass, Callback<T> callback) {

        return config.getAsyncClient().request(this, r -> new ObjectResponse<>(getObjectMapper(), r, responseClass), CallbackFuture.wrap(callback));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType) {

        return config.getAsyncClient().request(this, r -> new ObjectResponse<>(getObjectMapper(), r, genericType), new CompletableFuture<>());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType, Callback<T> callback) {

        return config.getAsyncClient().request(this, r -> new ObjectResponse<>(getObjectMapper(), r, genericType), CallbackFuture.wrap(callback));
    }

    private <T> Function<RawResponse, HttpResponse<T>> funcResponse(Function<RawResponse, T> function) {
        return r -> new BasicResponse<>(r, function.apply(r));
    }

    @Override
    public void thenConsume(Consumer<RawResponse> consumer) {
        config.getClient().request(this, getConsumer(consumer));
    }

    @Override
    public void thenConsumeAsync(Consumer<RawResponse> consumer) {
        config.getAsyncClient().request(this, getConsumer(consumer), new CompletableFuture<>());
    }

    @Override
    public HttpResponse<File> asFile(String path) {
        return config.getClient().request(this, r -> new FileResponse(r, path));
    }

    @Override
    public CompletableFuture<HttpResponse<File>> asFileAsync(String path) {
        return config.getAsyncClient().request(this, r -> new FileResponse(r, path), new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<File>> asFileAsync(String path, Callback<File> callback) {
        return config.getAsyncClient().request(this, r -> new FileResponse(r, path), CallbackFuture.wrap(callback));
    }

    @Override
    public <T> PagedList<T> asPaged(Function<HttpRequest, HttpResponse> mappingFunction, Function<HttpResponse<T>, String> linkExtractor) {
        PagedList<T> all = new PagedList<>();
        String nextLink = this.getUrl();
        do {
            this.url = new Path(nextLink);
            HttpResponse<T> next = mappingFunction.apply(this);
            all.add(next);
            nextLink = linkExtractor.apply(next);
        }while (!Util.isNullOrEmpty(nextLink));
        return all;
    }



    private Function<RawResponse, HttpResponse<Object>> getConsumer(Consumer<RawResponse> consumer) {
        return r -> {
            consumer.accept(r);
            return null;
        };
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

    private ObjectMapper getObjectMapper() {
        return objectMapper.orElseGet(config::getObjectMapper);
    }

    @Override
    public int getSocketTimeout() {
        return valueOr(socketTimeout, config::getSocketTimeout);
    }

    @Override
    public int getConnectTimeout() {
        return valueOr(connectTimeout, config::getConnectionTimeout);
    }

    @Override
    public Proxy getProxy() {
        return valueOr(proxy, config::getProxy);
    }

    private <T> T valueOr(T x, Supplier<T> o){
        if(x != null){
            return x;
        }
        return o.get();
    }
}
