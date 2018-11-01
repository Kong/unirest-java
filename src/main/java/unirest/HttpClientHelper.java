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
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class HttpClientHelper {

    public static <T> HttpResponse<T> request(HttpRequest request,
                                              Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer) {

        HttpRequestBase requestObj = RequestPrep.prepareRequest(request, false);
        HttpClient client = Options.getHttpClient(); // The
        // DefaultHttpClient
        // is thread-safe

        org.apache.http.HttpResponse response;
        try {
            response = client.execute(requestObj);
            HttpResponse<T> httpResponse = transformer.apply(response);
            requestObj.releaseConnection();
            return httpResponse;
        } catch (Exception e) {
            throw new UnirestException(e);
        } finally {
            requestObj.releaseConnection();
        }
    }

    public static <T> CompletableFuture<HttpResponse<T>> requestAsync(HttpRequest httpRequest, Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer) {
        return requestAsync(httpRequest, transformer, new CompletableFuture<>());
    }

    public static <T> CompletableFuture<HttpResponse<T>> requestAsync(HttpRequest request, Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer, Callback<T> callback) {
        return requestAsync(request, transformer, CallbackFuture.wrap(callback));
    }

    public static <T> CompletableFuture<HttpResponse<T>> requestAsync(HttpRequest request,
                                                                      Function<org.apache.http.HttpResponse, HttpResponse<T>> transformer,
                                                                      CompletableFuture<HttpResponse<T>> callback) {
        Objects.requireNonNull(callback);

        HttpUriRequest requestObj = RequestPrep.prepareRequest(request, true);

        asyncClient()
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

    private static CloseableHttpAsyncClient asyncClient() {
        CloseableHttpAsyncClient asyncHttpClient = Options.getAsyncHttpClient();
        if (!asyncHttpClient.isRunning()) {
            tryStart(asyncHttpClient);
        }
        return asyncHttpClient;
    }

    private static synchronized void tryStart(CloseableHttpAsyncClient asyncHttpClient) {
        if (!asyncHttpClient.isRunning()) {
            asyncHttpClient.start();
            AsyncIdleConnectionMonitorThread asyncIdleConnectionMonitorThread = (AsyncIdleConnectionMonitorThread) Options.getOption(Option.ASYNC_MONITOR);
            asyncIdleConnectionMonitorThread.start();
        }
    }
}
