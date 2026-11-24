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

import kong.unirest.core.Config;
import kong.unirest.core.Empty;
import kong.unirest.core.Headers;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Interceptor;
import kong.unirest.core.MetricContext;
import kong.unirest.core.SseRequestImpl;
import kong.unirest.core.UnirestException;
import kong.unirest.core.UnirestInstance;
import kong.unirest.core.WebSocketRequestImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpHeaders;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JavaClientTest {

    private static final String SECRET = "Bearer s3cr3t\n";
    private static final String MESSAGE = "invalid value for header \"Authorization\": illegal character 0x0a at index 13";

    @Test
    void cancellationClosesBodyDuringResponseTransformation() throws Exception {
        var body = mock(InputStream.class);
        var transport = CompletableFuture.completedFuture(responseWithBody(body));
        var httpClient = mock(HttpClient.class);
        when(httpClient.<InputStream>sendAsync(any(), any())).thenReturn(transport);
        var config = new Config();
        var client = new JavaClient(config, httpClient);
        var transforming = new CompletableFuture<Void>();
        var released = new CompletableFuture<Void>();
        doAnswer(invocation -> {
            released.complete(null);
            return null;
        }).when(body).close();
        var request = new UnirestInstance(config).get("http://localhost/");
        var result = client.request(request, raw -> {
            transforming.complete(null);
            released.join();
            return null;
        }, new CompletableFuture<HttpResponse<Empty>>(), Empty.class);

        try {
            transforming.get(5, TimeUnit.SECONDS);
            assertThat(result.cancel(true)).isTrue();
            verify(body).close();
            assertThat(result.isCancelled()).isTrue();
        } finally {
            released.complete(null);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void cancellationClosesLateResponseBody(boolean closeFails) throws Exception {
        var transport = new CompletableFuture<java.net.http.HttpResponse<InputStream>>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }
        };
        var httpClient = mock(HttpClient.class);
        when(httpClient.<InputStream>sendAsync(any(), any())).thenReturn(transport);
        var body = mock(InputStream.class);
        var closeFailure = new IOException("close failed");
        if (closeFails) {
            doThrow(closeFailure).when(body).close();
        }
        var config = new Config();
        var client = new JavaClient(config, httpClient);
        var request = new UnirestInstance(config).get("http://localhost/");
        var result = client.request(request, raw -> null, new CompletableFuture<HttpResponse<Empty>>(), Empty.class);

        assertThat(result.cancel(true)).isTrue();
        transport.complete(responseWithBody(body));

        verify(body).close();
        assertThat(result.isCancelled()).isTrue();
        if (closeFails) {
            assertThat(result.handle((response, error) -> error).join())
                    .isInstanceOf(CancellationException.class)
                    .hasSuppressedException(closeFailure);
        }
    }

    private static java.net.http.HttpResponse<InputStream> responseWithBody(InputStream body) {
        java.net.http.HttpResponse<InputStream> response = mock(java.net.http.HttpResponse.class);
        when(response.body()).thenReturn(body);
        when(response.headers()).thenReturn(HttpHeaders.of(Map.of(), (name, value) -> true));
        return response;
    }

    @ParameterizedTest
    @MethodSource("metricFailures")
    void throwingMetricCompletesRequestExceptionally(Throwable failure) {
        var transport = new CompletableFuture<java.net.http.HttpResponse<InputStream>>();
        var httpClient = mock(HttpClient.class);
        when(httpClient.<InputStream>sendAsync(any(), any())).thenReturn(transport);
        var metric = mock(MetricContext.class);
        doThrow(failure).when(metric).complete(any(), any());
        var config = new Config().instrumentWith(request -> metric);
        var request = new UnirestInstance(config).get("http://localhost/");
        var callback = new CompletableFuture<HttpResponse<Empty>>();
        var client = new JavaClient(config, httpClient);
        var result = client.request(request, raw -> null, callback, Empty.class);

        transport.completeExceptionally(new IOException("network failed"));

        assertThatThrownBy(() -> result.get(1, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCause(failure);
        assertThat(callback.isCompletedExceptionally()).isTrue();
    }

    private static Stream<Throwable> metricFailures() {
        return Stream.of(new IllegalStateException("metric failed"), new AssertionError("metric failed"));
    }

    @Test
    void cancellationReachesTransportWithoutInvokingFailureInterceptor() {
        var transport = new CompletableFuture<java.net.http.HttpResponse<InputStream>>();
        var httpClient = mock(HttpClient.class);
        when(httpClient.<InputStream>sendAsync(any(), any())).thenReturn(transport);
        var interceptor = mock(Interceptor.class);
        var config = new Config().interceptor(interceptor);
        var request = new UnirestInstance(config).get("http://localhost/");
        var callback = new CompletableFuture<HttpResponse<Empty>>();
        var client = new JavaClient(config, httpClient);

        var result = client.request(request, raw -> null, callback, Empty.class);
        assertThat(result.cancel(true)).isTrue();

        assertThat(result).isSameAs(callback);
        assertThat(transport.isCancelled()).isTrue();
        verify(interceptor).onRequest(request, config);
        verifyNoMoreInteractions(interceptor);
    }

    @Test
    void invalidHeaderValueIsNotCopiedIntoTheException() {
        var unirest = new UnirestInstance(new Config());

        assertThatThrownBy(() -> unirest.get("http://localhost/").header("Authorization", SECRET).asEmpty())
                .isInstanceOf(UnirestException.class)
                .hasMessageNotContaining("s3cr3t")
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage(MESSAGE);
    }

    @Test
    void invalidSseHeaderValueIsNotCopiedIntoTheException() {
        var request = new SseRequestImpl(new Config(), "http://localhost/sse").header("Authorization", SECRET);

        assertThatThrownBy(() -> SseRequestBuilder.getHttpRequest(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MESSAGE);
    }

    @Test
    void invalidWebSocketHeaderValueIsNotCopiedIntoTheException() {
        var config = new Config();
        var request = new WebSocketRequestImpl(config, "ws://localhost/").header("Authorization", SECRET);

        assertThatThrownBy(() -> new JavaClient(config).websocket(request, new WebSocket.Listener() { }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MESSAGE);
    }

    @Test
    void acceptsTheSameHeaderValuesAsTheJdk() {
        for (int c = 0; c <= Character.MAX_VALUE; c++) {
            String value = "a" + (char) c + "b";
            assertThat(acceptedByUnirest(value)).as("char 0x%04x", c).isEqualTo(acceptedByJdk(value));
        }
    }

    private static boolean acceptedByUnirest(String value) {
        var headers = new Headers();
        headers.add("X-Test", value);
        try {
            JavaClient.validHeaderValue(headers.all().get(0));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean acceptedByJdk(String value) {
        try {
            HttpRequest.newBuilder(URI.create("http://localhost/")).header("X-Test", value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
