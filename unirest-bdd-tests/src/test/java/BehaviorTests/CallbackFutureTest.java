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

package BehaviorTests;

import kong.unirest.core.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static BehaviorTests.MockCallback.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CallbackFutureTest extends BddTest {

    @ParameterizedTest @ValueSource(booleans = {false, true}) @Timeout(15)
    void cancellationStopsReadingAfterHeadersArrive(boolean retryEnabled) throws Exception {
        Unirest.config().retryAfter(retryEnabled);
        var reading = new CompletableFuture<InputStream>();
        var stopped = new CompletableFuture<Void>();
        var future = Unirest.get(MockServer.STREAMING)
                .asObjectAsync(response -> readUntilClosed(response, reading, stopped));

        try {
            reading.get(5, TimeUnit.SECONDS);
            assertThat(future.cancel(true)).isTrue();
            stopped.get(5, TimeUnit.SECONDS);
            assertThat(future.isCancelled()).isTrue();
        } finally {
            future.cancel(true);
            MockServer.releaseStreamingBody();
            var body = reading.getNow(null);
            if (body != null) {
                body.close();
                stopped.get(5, TimeUnit.SECONDS);
            }
        }
    }

    private String readUntilClosed(RawResponse response, CompletableFuture<InputStream> reading, CompletableFuture<Void> stopped) {
        var body = response.getContent();
        try {
            body.read();
            reading.complete(body);
            body.transferTo(OutputStream.nullOutputStream());
            return "";
        } catch (IOException e) {
            throw new UnirestException(e);
        } finally {
            try {
                body.close();
            } catch (IOException e) {
                throw new UnirestException(e);
            } finally {
                stopped.complete(null);
            }
        }
    }

    @Test @Timeout(10)
    void throwingMetricCompletesFutureAndNotifiesFailureCallback() throws Exception {
        var originalMetric = Unirest.config().getMetric();
        try {
            var metricFailure = new IllegalStateException("metric failed");
            Unirest.config().instrumentWith(request -> (response, failure) -> {
                throw metricFailure;
            });
            var callbackFailure = new CompletableFuture<UnirestException>();
            var future = Unirest.get(MockServer.TIMEOUT)
                    .requestTimeout(100)
                    .asEmptyAsync(new NoopCallback<Empty>() {
                        @Override
                        public void failed(UnirestException e) {
                            callbackFailure.complete(e);
                        }
                    });

            assertThatThrownBy(() -> future.get(5, TimeUnit.SECONDS))
                    .isInstanceOf(ExecutionException.class)
                    .hasCause(metricFailure);
            assertThat(callbackFailure.get(1, TimeUnit.SECONDS))
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("metric failed");
        } finally {
            Unirest.config().instrumentWith(originalMetric);
        }
    }

    @ParameterizedTest @ValueSource(booleans = {false, true}) @Timeout(10)
    void cancellingRequestInvokesCancelledCallback(boolean retryEnabled) throws Exception {
        Unirest.config().retryAfter(retryEnabled);
        var callbackResult = new CompletableFuture<String>();
        var future = Unirest.get(MockServer.TIMEOUT)
                .requestTimeout(5000)
                .asEmptyAsync(new Callback<Empty>() {
                    @Override
                    public void completed(HttpResponse<Empty> response) {
                        callbackResult.complete("completed");
                    }

                    @Override
                    public void failed(UnirestException e) {
                        callbackResult.complete("failed");
                    }

                    @Override
                    public void cancelled() {
                        callbackResult.complete("cancelled");
                    }
                });

        assertThat(future.cancel(true)).isTrue();
        assertThat(future.isCancelled()).isTrue();
        assertThat(callbackResult.completeOnTimeout("no callback", 5, TimeUnit.SECONDS)
                .get(6, TimeUnit.SECONDS))
                .isEqualTo("cancelled");
    }

    @Test @Timeout(10)
    void requestTimeoutCompletesFutureExceptionally() throws Exception {
        var future = Unirest.get(MockServer.TIMEOUT)
                .requestTimeout(100)
                .asEmptyAsync();

        assertThatThrownBy(() -> future.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasRootCauseInstanceOf(HttpTimeoutException.class);

        assertThat(future.isCompletedExceptionally())
                .isTrue();
    }

    @Test @Timeout(5)
    void onSuccess() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("Snazzy", "Shoes")
                .asJsonAsync()
                .thenAccept(r -> {
                    parse(r).assertParam("Snazzy", "Shoes");
                    asyncSuccess();
                }).get();

        assertAsync();
    }

    @Test @Timeout(5)
    void onSuccessSupplyCallback() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("Snazzy", "Shoes")
                .asJsonAsync(new NoopCallback<>())
                .thenAccept(r -> {
                    parse(r).assertParam("Snazzy", "Shoes");
                    asyncSuccess();
                }).get();

        assertAsync();
    }

    @Test @Timeout(5) @Disabled
    void onFailure() throws Exception {
        Unirest.get("http://localhost:0000")
                .asJsonAsync(json(this))
                .isCompletedExceptionally();

        assertFailed("java.net.ConnectException: Connection refused");
    }

    boolean wasCalled = false;

    @Test @Disabled
    void callFailureFunction() throws Exception {
        wasCalled = false;
        try {
            Unirest.get("http://localhost:0000")
                    .asJsonAsync(new Callback<JsonNode>() {
                        @Override
                        public void completed(HttpResponse<JsonNode> response) {
                            throw new UnirestException("Failure!");
                        }

                        @Override
                        public void failed(UnirestException e) {
                            wasCalled = true;
                        }

                        @Override
                        public void cancelled() {

                        }
                    }).get();
        }catch (Exception e){}

        assertTrue(wasCalled);
    }

    @Test @Disabled
    void cancelFunction() throws Exception {
        wasCalled = false;
        CompletableFuture<HttpResponse<JsonNode>> future = Unirest.get("http://localhost:0000")
                .asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        throw new UnirestException("Failure!");
                    }

                    @Override
                    public void failed(UnirestException e) {

                    }

                    @Override
                    public void cancelled() {
                        wasCalled = true;
                    }
                });

        future.cancel(true);
        assertTrue(wasCalled);
    }
}