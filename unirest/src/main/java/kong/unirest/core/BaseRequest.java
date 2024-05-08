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

package kong.unirest.core;

import java.io.File;
import java.net.http.HttpClient;
import java.nio.file.CopyOption;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static kong.unirest.core.CallbackFuture.wrap;

abstract class BaseRequest<R extends HttpRequest> implements HttpRequest<R> {

    private Instant creation = Util.now();
    private int callCount = 0;
    private Optional<ObjectMapper> objectMapper = Optional.empty();
    private String responseEncoding;
    protected Headers headers = new Headers();
    protected final Config config;
    protected HttpMethod method;
    protected Path url;
    private Integer connectTimeout;
    private ProgressMonitor downloadMonitor;
    private HttpClient.Version version;

    BaseRequest(BaseRequest httpRequest) {
        this.config = httpRequest.config;
        this.method = httpRequest.method;
        this.url = httpRequest.url;
        this.headers.putAll(httpRequest.headers);
        this.connectTimeout = httpRequest.connectTimeout;
        this.objectMapper = httpRequest.objectMapper;
        this.version = httpRequest.version;
        this.downloadMonitor = httpRequest.downloadMonitor;
    }

    BaseRequest(Config config, HttpMethod method, String url) {
        this.config = config;
        this.method = method;
        this.url = new Path(url, config.getDefaultBaseUrl());
        headers.putAll(config.getDefaultHeaders());
    }

    @Override
    public R routeParam(String name, String value) {
        url.param(name, value);
        return (R) this;
    }

    @Override
    public R routeParam(Map<String, Object> params) {
        url.param(params);
        return (R) this;
    }

    @Override
    public R basicAuth(String username, String password) {
        this.headers.setBasicAuth(username, password);
        return (R) this;
    }

    @Override
    public R accept(String value) {
        this.headers.accepts(value);
        return (R) this;
    }

    @Override
    public R header(String name, String value) {
        this.headers.add(name.trim(), value);
        return (R) this;
    }

    @Override
    public R headerReplace(String name, String value) {
        this.headers.replace(name, value);
        return (R) this;
    }

    @Override
    public R headers(Map<String, String> headerMap) {
        this.headers.add(headerMap);
        return (R) this;
    }

    @Override
    public R headersReplace(Map<String, String> headerMap) {
        this.headers.replace(headerMap);
        return (R) this;
    }

    @Override
    public R responseEncoding(String encoding) {
        this.responseEncoding = encoding;
        return (R) this;
    }

    @Override
    public R cookie(String name, String value) {
        this.headers.cookie(new Cookie(name, value));
        return (R) this;
    }

    @Override
    public R cookie(Cookie cookie) {
        this.headers.cookie(cookie);
        return (R) this;
    }

    @Override
    public R cookie(Collection<Cookie> cookies) {
        this.headers.cookie(cookies);
        return (R)this;
    }

    @Override
    public R queryString(String name, Collection<?> value) {
        url.queryString(name, value);
        return (R) this;
    }

    @Override
    public R queryString(String name, Object value) {
        url.queryString(name, value);
        return (R) this;
    }

    @Override
    public R queryString(Map<String, Object> parameters) {
        url.queryString(parameters);
        return (R) this;
    }

    @Override
    public R requestTimeout(int millies) {
        this.connectTimeout = millies;
        return (R) this;
    }

    @Override
    public R withObjectMapper(ObjectMapper mapper) {
        Objects.requireNonNull(mapper, "ObjectMapper may not be null");
        this.objectMapper = Optional.of(mapper);
        return (R) this;
    }

    @Override
    public R downloadMonitor(ProgressMonitor monitor) {
        this.downloadMonitor = monitor;
        return (R) this;
    }

    public ProgressMonitor getDownloadMonitor(){
        return this.downloadMonitor;
    }

    @Override
    public R version(HttpClient.Version value) {
        this.version = value;
        return (R) this;
    }

    @Override
    public HttpResponse<Empty> asEmpty() {
        return request(BasicResponse::new, Empty.class);
    }

    @Override
    public CompletableFuture<HttpResponse<Empty>> asEmptyAsync() {
        return requestAsync(this, BasicResponse::new, new CompletableFuture<>(), Empty.class);
    }

    @Override
    public CompletableFuture<HttpResponse<Empty>> asEmptyAsync(Callback<Empty> callback) {
        return requestAsync(this, BasicResponse::new, wrap(callback), Empty.class);
    }

    @Override
    public HttpResponse<String> asString() throws UnirestException {
        return request(r -> new StringResponse(r, responseEncoding), String.class);
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync() {
        return requestAsync(this, r -> new StringResponse(r, responseEncoding), new CompletableFuture<>(), String.class);
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback) {
        return requestAsync(this, r -> new StringResponse(r, responseEncoding), wrap(callback), String.class);
    }

    @Override
    public HttpResponse<byte[]> asBytes() {
        return request(r -> new ByteResponse(r, downloadMonitor), byte[].class);
    }

    @Override
    public CompletableFuture<HttpResponse<byte[]>> asBytesAsync() {
        return requestAsync(this, (RawResponse r) -> new ByteResponse(r, downloadMonitor), new CompletableFuture<>(), byte[].class);
    }

    @Override
    public CompletableFuture<HttpResponse<byte[]>> asBytesAsync(Callback<byte[]> callback) {
        return requestAsync(this, (RawResponse r) -> new ByteResponse(r, downloadMonitor), wrap(callback), byte[].class);
    }

    @Override
    public HttpResponse<JsonNode> asJson() throws UnirestException {
        return request(JsonResponse::new, JsonNode.class);
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync() {
        return requestAsync(this, JsonResponse::new, new CompletableFuture<>(), JsonNode.class);
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback) {
        return requestAsync(this, JsonResponse::new, wrap(callback), JsonNode.class);
    }

    @Override
    public <T> HttpResponse<T> asObject(Class<? extends T> responseClass) throws UnirestException {
        return request(r -> new ObjectResponse<T>(getObjectMapper(), r, responseClass), responseClass);
    }

    @Override
    public <T> HttpResponse<T> asObject(GenericType<T> genericType) throws UnirestException {
        return request(r -> new ObjectResponse<T>(getObjectMapper(), r, genericType), genericType.getTypeClass());
    }

    @Override
    public <T> HttpResponse<T> asObject(Function<RawResponse, T> function) {
        return request(funcResponse(function), Object.class);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Function<RawResponse, T> function) {
        return requestAsync(this, funcResponse(function), new CompletableFuture<>(), JsonNode.class);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass) {
        return requestAsync(this,
                r -> new ObjectResponse<T>(getObjectMapper(), r, responseClass),
                new CompletableFuture<>(),
                responseClass);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass, Callback<T> callback) {
        return requestAsync(this,
                r -> new ObjectResponse<>(getObjectMapper(), r, responseClass),
                wrap(callback),
                responseClass);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType) {
        return requestAsync(this,
                r -> new ObjectResponse<>(getObjectMapper(), r, genericType),
                new CompletableFuture<>(),
                genericType.getTypeClass());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType, Callback<T> callback) {
        return requestAsync(this,
                r -> new ObjectResponse<>(getObjectMapper(), r, genericType),
                wrap(callback),
                genericType.getTypeClass());
    }

    private <T> Function<RawResponse, HttpResponse<T>> funcResponse(Function<RawResponse, T> function) {
        return r -> new BasicResponse<>(r, function.apply(r));
    }

    @Override
    public void thenConsume(Consumer<RawResponse> consumer) {
        request(getConsumer(consumer), Object.class);
    }

    @Override
    public void thenConsumeAsync(Consumer<RawResponse> consumer) {
        requestAsync(this, getConsumer(consumer), new CompletableFuture<>(), Object.class);
    }

    @Override
    public HttpResponse<File> asFile(String path, CopyOption... copyOptions) {
        return request(r -> new FileResponse(r, path, downloadMonitor, copyOptions), File.class);
    }

    @Override
    public CompletableFuture<HttpResponse<File>> asFileAsync(String path, CopyOption... copyOptions) {
        return requestAsync(this,
                r -> new FileResponse(r, path, downloadMonitor, copyOptions),
                new CompletableFuture<>(),
                File.class);
    }

    @Override
    public CompletableFuture<HttpResponse<File>> asFileAsync(String path, Callback<File> callback, CopyOption... copyOptions) {
        return requestAsync(this,
                r -> new FileResponse(r, path, downloadMonitor, copyOptions),
                wrap(callback),
                File.class);
    }

    @Override
    public <T> PagedList<T> asPaged(Function<HttpRequest, HttpResponse> mappingFunction, Function<HttpResponse<T>, String> linkExtractor) {
        PagedList<T> all = new PagedList<>();
        String nextLink = this.getUrl();
        do {
            this.url = new Path(nextLink, config.getDefaultBaseUrl());
            BaseRequest<R> t = RequestFactory.copy(this);
            HttpResponse<T> next = mappingFunction.apply(t);
            all.add(next);
            nextLink = linkExtractor.apply(next);
        } while (!Util.isNullOrEmpty(nextLink));
        return all;
    }


    private <E> HttpResponse<E> request(Function<RawResponse, HttpResponse<E>> transformer, Class<?> resultType){
        HttpResponse<E> response = config.getClient().request(this, transformer, resultType);

        if(config.isAutomaticRetryAfter()) {
            callCount++;
            var retryAfter = config.getRetryStrategy();
            if(retryAfter.isRetryable(response) && callCount < config.maxRetries()) {
                long waitTime = retryAfter.getWaitTime(response);
                if (waitTime > 0) {
                    retryAfter.waitFor(waitTime);
                    return request(transformer, resultType);
                }
            }
        }
        return response;
    }

    private <T> CompletableFuture<HttpResponse<T>> requestAsync(HttpRequest request,
                                                           Function<RawResponse, HttpResponse<T>> transformer,
                                                           CompletableFuture<HttpResponse<T>> callback,
                                                           Class<?> resultType){
        var asyncR = config.getClient().request(request, transformer, callback, resultType);
        if(config.isAutomaticRetryAfter()){
            return asyncR.thenApplyAsync(response -> {
                callCount++;
                var retryAfter = config.getRetryStrategy();
                if(retryAfter.isRetryable(response) && callCount < config.maxRetries()) {
                    long waitTime = retryAfter.getWaitTime(response);
                    if (waitTime > 0) {
                        retryAfter.waitFor(waitTime);
                        try {
                            return requestAsync(this, transformer, callback, resultType).get();
                        } catch (Exception e) {
                            throw new UnirestException(e);
                        }
                    }
                }
                return response;
            });
        }
        return asyncR;
    }


    private Function<RawResponse, HttpResponse<Object>> getConsumer(Consumer<RawResponse> consumer) {
        return r -> {
            consumer.accept(r);
            return new BasicResponse<>(r);
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

    protected ObjectMapper getObjectMapper() {
        return objectMapper.orElseGet(config::getObjectMapper);
    }

    @Override
    public Integer getRequestTimeout() {
        return valueOr(connectTimeout, config::getRequestTimeout);
    }

    @Override
    public HttpRequestSummary toSummary() {
        return new RequestSummary(this);
    }

    @Override
    public Instant getCreationTime() {
        return creation;
    }

    @Override
    public HttpClient.Version getVersion() {
        return version;
    }

    private <T> T valueOr(T x, Supplier<T> o) {
        if (x != null) {
            return x;
        }
        return o.get();
    }

    Path getPath() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseRequest<?> that = (BaseRequest<?>) o;
        return Objects.equals(headers, that.headers) &&
                Objects.equals(method, that.method) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, method, url);
    }

}
