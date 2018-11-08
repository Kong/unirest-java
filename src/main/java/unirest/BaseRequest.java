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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class BaseRequest {

    private final ResponseBuilder builder;
    private final Config config;
    protected HttpRequest httpRequest;

    protected BaseRequest(Config config, HttpRequest httpRequest) {
        this.config = config;
        this.httpRequest = httpRequest;
        this.builder = new ResponseBuilder(config);
    }

    protected BaseRequest(Config config) {
        this.config = config;
        this.builder = new ResponseBuilder(config);
    }

    public HttpRequest getHttpRequest() {
        return this.httpRequest;
    }

    public HttpResponse<String> asString() throws UnirestException {
        return request(httpRequest, builder::asString);
    }

    public CompletableFuture<HttpResponse<String>> asStringAsync() {
        return requestAsync(httpRequest, builder::asString);
    }

    public CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback) {
        return requestAsync(httpRequest, builder::asString, callback);
    }

    public HttpResponse<JsonNode> asJson() throws UnirestException {
        return request(httpRequest, builder::asJson);
    }

    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync() {
        return requestAsync(httpRequest, builder::asJson);
    }

    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback) {
        return requestAsync(httpRequest, builder::asJson, callback);
    }

    public <T> HttpResponse<T> asObject(Class<? extends T> responseClass) throws UnirestException {
        return request(httpRequest, r -> builder.asObject(r, responseClass));
    }

    public <T> HttpResponse<T> asObject(GenericType<T> genericType) throws UnirestException {
        return request(httpRequest, r -> builder.asObject(r, genericType));
    }

    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass) {
        return requestAsync(httpRequest, r -> builder.asObject(r, responseClass));
    }

    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass, Callback<T> callback) {
        return requestAsync(httpRequest, r -> builder.asObject(r, responseClass), callback);
    }

    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType) {
        return requestAsync(httpRequest, r -> builder.asObject(r, genericType));
    }

    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType, Callback<T> callback) {
        return requestAsync(httpRequest, r -> builder.asObject(r, genericType), callback);
    }

    public HttpResponse<InputStream> asBinary() throws UnirestException {
        return request(httpRequest, builder::asBinary);
    }

    public CompletableFuture<HttpResponse<InputStream>> asBinaryAsync() {
        return requestAsync(httpRequest, builder::asBinary);
    }

    public CompletableFuture<HttpResponse<InputStream>> asBinaryAsync(Callback<InputStream> callback) {
        return requestAsync(httpRequest, builder::asBinary, callback);
    }


    private <T> HttpResponse<T> request(HttpRequest request,
                                        Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer) {

        HttpRequestBase requestObj = RequestPrep.prepareRequest(request, false);
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
            HttpRequest request,
            Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer) {
        return requestAsync(request, transformer, new CompletableFuture<>());
    }

    private <T> CompletableFuture<HttpResponse<T>> requestAsync(
            HttpRequest request,
            Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer,
            Callback<T> callback) {
        return requestAsync(request, transformer, CallbackFuture.wrap(callback));
    }

    private <T> CompletableFuture<HttpResponse<T>> requestAsync(
            HttpRequest request,
            Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer,
            CompletableFuture<HttpResponse<T>> callback) {

        Objects.requireNonNull(callback);

        HttpUriRequest requestObj = RequestPrep.prepareRequest(request, true);

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
}
