package io.github.openunirest.request;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.async.Callback;
import io.github.openunirest.http.async.CallbackFuture;
import io.github.openunirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import io.github.openunirest.http.exceptions.UnirestException;
import io.github.openunirest.http.options.Option;
import io.github.openunirest.http.options.Options;
import io.github.openunirest.http.utils.ClientFactory;
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
        HttpClient client = ClientFactory.getHttpClient(); // The
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
        CloseableHttpAsyncClient asyncHttpClient = ClientFactory.getAsyncHttpClient();
        if (!asyncHttpClient.isRunning()) {
            asyncHttpClient.start();
            AsyncIdleConnectionMonitorThread asyncIdleConnectionMonitorThread = (AsyncIdleConnectionMonitorThread) Options.getOption(Option.ASYNC_MONITOR);
            asyncIdleConnectionMonitorThread.start();
        }
        return asyncHttpClient;
    }
}
