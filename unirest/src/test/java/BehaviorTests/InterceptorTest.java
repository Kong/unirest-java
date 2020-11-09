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

import kong.unirest.*;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Sets.newHashSet;
import static kong.unirest.TestUtil.rezFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterceptorTest extends BddTest {

    private UniInterceptor interceptor;
    private final String ioErrorMessage = "Something horrible happened";;

    @BeforeEach
    public void setUp() {
        super.setUp();
        interceptor = new UniInterceptor("x-custom", "foo");
    }

    @Test
    void canAddInterceptor() {
        Unirest.config().interceptor(interceptor);
        Unirest.get(MockServer.GET).asObject(RequestCapture.class);

        interceptor.cap.assertHeader("x-custom", "foo");
        assertEquals(MockServer.GET, interceptor.reqSum.getUrl());
    }

    @Test
    void canAddTwoInterceptor() {
        Unirest.config().interceptor(interceptor);
        Unirest.config().interceptor(new UniInterceptor("fruit", "grapes"));
        Unirest.get(MockServer.GET).asObject(RequestCapture.class);

        interceptor.cap.assertHeader("x-custom", "foo");
        interceptor.cap.assertHeader("fruit", "grapes");
    }

    @Test
    void canAddInterceptorToAsync() throws ExecutionException, InterruptedException {
        Unirest.config().interceptor(interceptor);

        Unirest.get(MockServer.GET)
                .asObjectAsync(RequestCapture.class)
                .get();

        interceptor.cap.assertHeader("x-custom", "foo");
    }

    @Test
    void totalFailure() throws Exception {
        Unirest.config().httpClient(getFailureClient()).interceptor(interceptor);

        TestUtil.assertException(() -> Unirest.get(MockServer.GET).asEmpty(),
                UnirestException.class,
                "java.io.IOException: " + ioErrorMessage);
    }

    @Test
    void canReturnEmptyResultRatherThanThrow() throws Exception {
        Unirest.config().httpClient(getFailureClient()).interceptor(interceptor);
        interceptor.failResponse = true;

        HttpResponse<String> response = Unirest.get(MockServer.GET).asString();

        assertEquals(542, response.getStatus());
        assertEquals(ioErrorMessage, response.getStatusText());
    }

    @Test
    void totalAsyncFailure() throws Exception {
        Unirest.config().addInterceptor((r, c) -> {
            throw new IOException(ioErrorMessage);
        }).interceptor(interceptor);

        TestUtil.assertException(() -> Unirest.get(MockServer.GET).asStringAsync().get(),
                ExecutionException.class,
                "java.io.IOException: " + ioErrorMessage);
    }

    @Test
    void totalAsyncFailure_Recovery() throws Exception {
        interceptor.failResponse = true;
        Unirest.config().addInterceptor((r, c) -> {
            throw new IOException(ioErrorMessage);
        }).interceptor(interceptor);

        HttpResponse<String> response = Unirest.get(MockServer.GET).asStringAsync().get();

        assertEquals(542, response.getStatus());
        assertEquals(ioErrorMessage, response.getStatusText());
    }

    private HttpClient getFailureClient() throws IOException {
        HttpClient client = mock(HttpClient.class);
        when(client.execute(any(HttpHost.class), any(HttpUriRequest.class))).thenThrow(new IOException(ioErrorMessage));
        when(client.execute(any(HttpUriRequest.class))).thenThrow(new IOException(ioErrorMessage));
        return client;
    }

    @Test @Deprecated
    void canAddApacheInterceptor() {
        Unirest.config().addInterceptor(new TestInterceptor());

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("x-custom", "foo");
    }

    @Test @Deprecated
    void canAddApacheInterceptorToAsync() throws ExecutionException, InterruptedException {
        Unirest.config().addInterceptor(new TestInterceptor());

        Unirest.get(MockServer.GET)
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertHeader("x-custom", "foo");
    }

    @Test
    void loggingBodyPartsExample() {
        final Set<String> values = new HashSet<>();
        Unirest.config().interceptor(new Interceptor() {
            @Override
            public void onRequest(HttpRequest<?> request, Config config) {
                request.getBody().ifPresent(b ->
                        b.multiParts().forEach(part ->
                                values.add(part.toString())));
            }
        });

        Unirest.post(MockServer.POST)
                .field("fruit", "apples")
                .field("file", rezFile("/spidey.jpg"))
                .asEmpty();

        assertEquals(newHashSet("file=spidey.jpg","fruit=apples"), values);
    }

    private class TestInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(org.apache.http.HttpRequest httpRequest, org.apache.http.protocol.HttpContext httpContext) throws HttpException, IOException {
            httpRequest.addHeader("x-custom", "foo");
        }
    }

    private class UniInterceptor implements Interceptor {
        RequestCapture cap;
        HttpRequestSummary reqSum;
        boolean failResponse;
        private String name;
        private String value;

        public UniInterceptor(String name, String value){
            this.name = name;
            this.value = value;
        }

        @Override
        public void onRequest(HttpRequest<?> request, Config config) {
            request.header(name, value);
        }

        @Override
        public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
            cap = (RequestCapture)response.getBody();
            reqSum = request;
        }

        @Override
        public HttpResponse<?> onFail(Exception e, HttpRequestSummary request, Config config) {
            if(failResponse){
                return new FailedResponse(e);
            }
            return Interceptor.super.onFail(e, request, config);
        }
    }
}
