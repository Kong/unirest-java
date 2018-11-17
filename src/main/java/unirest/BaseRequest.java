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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static unirest.BodyData.from;

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
    public HttpRequest getHttpRequest() {
        return this;
    }

    @Override
    public HttpResponse<String> asString() throws UnirestException {
        return request(this::asString);
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync() {
        return requestAsync(this::asString, new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback) {
        return requestAsync(this::asString, CallbackFuture.wrap(callback));
    }

    @Override
    public HttpResponse<JsonNode> asJson() throws UnirestException {
        return request(this::asJson);
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync() {
        return requestAsync(this::asJson, new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback) {
        return requestAsync(this::asJson, CallbackFuture.wrap(callback));
    }

    @Override
    public <T> HttpResponse<T> asObject(Class<? extends T> responseClass) throws UnirestException {
        return request(r -> this.asObject(r, responseClass));
    }

    @Override
    public <T> HttpResponse<T> asObject(GenericType<T> genericType) throws UnirestException {
        return request(r -> this.asObject(r, genericType));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass) {
        return requestAsync(r -> this.asObject(r, responseClass), new CompletableFuture<>());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass, Callback<T> callback) {
        return requestAsync(r -> this.asObject(r, responseClass), CallbackFuture.wrap(callback));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType) {
        return requestAsync(r -> this.asObject(r, genericType), new CompletableFuture<>());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType, Callback<T> callback) {
        return requestAsync(r -> this.asObject(r, genericType), CallbackFuture.wrap(callback));
    }

    @Override
    public HttpResponse<InputStream> asBinary() throws UnirestException {
        return request(this::asBinary);
    }

    @Override
    public CompletableFuture<HttpResponse<InputStream>> asBinaryAsync() {
        return requestAsync(this::asBinary, new CompletableFuture<>());
    }

    @Override
    public CompletableFuture<HttpResponse<InputStream>> asBinaryAsync(Callback<InputStream> callback) {
        return requestAsync(this::asBinary, CallbackFuture.wrap(callback));
    }

    private <T> HttpResponse<T> request(Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer) {

        HttpRequestBase requestObj = new RequestPrep(this, false).prepare();
        HttpClient client = config.getClient();

        try {
            org.apache.http.HttpResponse execute = client.execute(requestObj);
            HttpResponse<T> httpResponse = transformer.apply(execute);
            requestObj.releaseConnection();
            return httpResponse;
        } catch (Exception e) {
            throw new UnirestException(e);
        } finally {
            requestObj.releaseConnection();
        }
    }

    private <T> CompletableFuture<HttpResponse<T>> requestAsync(
            Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer,
            CompletableFuture<HttpResponse<T>> callback) {

        Objects.requireNonNull(callback);

        HttpUriRequest requestObj = new RequestPrep(this, true).prepare();

        config.getAsyncHttpClient()
                .execute(requestObj, new FutureCallback<org.apache.http.HttpResponse>() {
                    @Override
                    public void completed(org.apache.http.HttpResponse httpResponse) {
                        callback.complete(transformer.apply(httpResponse));
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

    private HttpResponse<JsonNode> asJson(org.apache.http.HttpResponse response) {
        return new HttpResponseImpl<>(response, from(response.getEntity(), b -> toJson(b)));
    }

    private HttpResponse<InputStream> asBinary(org.apache.http.HttpResponse response){
        return new HttpResponseImpl<>(response, from(response.getEntity(), BodyData::getRawInput));
    }

    private <T> HttpResponse<T> asObject(org.apache.http.HttpResponse response, Class<? extends T> aClass) {
        return new HttpResponseImpl<>(response, from(response.getEntity(), b -> toObject(b, aClass)));
    }

    private <T> HttpResponse<T> asObject(org.apache.http.HttpResponse response, GenericType<T> genericType) {
        return new HttpResponseImpl<>(response, from(response.getEntity(), b -> toObject(b, genericType)));
    }

    private HttpResponse<String> asString(org.apache.http.HttpResponse response) {
        return new HttpResponseImpl<>(response, from(response.getEntity(), this::toString));
    }

    private <T> T toObject(BodyData<T> b, GenericType<T> genericType) {
        ObjectMapper o = getObjectMapper();
        return o.readValue(toString(b), genericType);
    }

    private <T> T toObject(BodyData<T> b, Class<? extends T> aClass) {
        ObjectMapper o = getObjectMapper();
        return o.readValue(toString(b), aClass);
    }

    private String toString(BodyData b) {
        try {
            return new String(b.getRawBytes(), b.getCharset());
        } catch (UnsupportedEncodingException e) {
            throw new UnirestException(e);
        }
    }

    private JsonNode toJson(BodyData<JsonNode> b) {
        String jsonString = toString(b);
        return new JsonNode(jsonString);
    }

    private ObjectMapper getObjectMapper() {
        return objectMapper.orElseGet(config::getObjectMapper);
    }
}
