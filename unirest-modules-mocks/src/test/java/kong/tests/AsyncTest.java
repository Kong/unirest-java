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

package kong.tests;

import kong.unirest.core.HttpMethod;
import kong.unirest.core.Callback;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AsyncTest extends Base {

    @Test
    void canExpectAsync() throws Exception {
        client.expect(HttpMethod.GET, path).thenReturn("Hey Ma");

        String body = Unirest.get(path).asStringAsync().get().getBody();

        assertEquals("Hey Ma", body);

        client.verifyAll();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void invokesCompletedCallback(boolean retriesEnabled) throws Exception {
        Unirest.config().retryAfter(retriesEnabled);
        client.expect(HttpMethod.GET, path).thenReturn("Hey Ma");

        var received = new CompletableFuture<HttpResponse<String>>();
        var response = Unirest.get(path).asStringAsync(received::complete).get(5, TimeUnit.SECONDS);

        assertThat(received.get(5, TimeUnit.SECONDS)).isSameAs(response);
        assertThat(response.getBody()).isEqualTo("Hey Ma");
}

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void invokesFailedCallback(boolean retriesEnabled) throws Exception {
        Unirest.config().retryAfter(retriesEnabled);

        var failure = new IllegalStateException("mock response failed");
        client.expect(HttpMethod.GET, path).thenReturn().thenReturn(() -> {
            throw failure;
        });
        var received = new CompletableFuture<UnirestException>();
        var callback = new Callback<String>() {
            @Override
            public void completed(HttpResponse<String> response) {
                received.completeExceptionally(new AssertionError("Unexpected completed callback"));
            }

            @Override
            public void failed(UnirestException error) {
                received.complete(error);
            }
        };

        var response = Unirest.get(path).asStringAsync(callback);

        assertThatThrownBy(() -> response.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class).hasCause(failure);
        assertThat(received.get(5, TimeUnit.SECONDS)).hasRootCause(failure);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void invokesCancelledCallback(boolean retriesEnabled) throws Exception {
        Unirest.config().retryAfter(retriesEnabled);
        var started = new CompletableFuture<Void>();
        var release = new CompletableFuture<String>();
        client.expect(HttpMethod.GET, path).thenReturn().thenReturn(() -> {
            started.complete(null);
            return release.join();
        });
        var received = new CompletableFuture<String>();
        var callback = new Callback<String>() {
            @Override
            public void completed(HttpResponse<String> response) {
                received.complete("completed");
            }

            @Override
            public void failed(UnirestException error) {
                received.complete("failed");
            }

            @Override
            public void cancelled() {
                received.complete("cancelled");
            }
        };

        try {
            var response = Unirest.get(path).asStringAsync(callback);
            started.get(5, TimeUnit.SECONDS);

            assertThat(response.cancel(true)).isTrue();
            assertThat(received.get(5, TimeUnit.SECONDS)).isEqualTo("cancelled");
        } finally {
            release.complete("Hey Ma");
        }
    }
}
