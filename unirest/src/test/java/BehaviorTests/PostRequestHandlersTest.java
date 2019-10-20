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

import kong.unirest.apache.MockAsyncClient;
import kong.unirest.apache.MockedClient;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class PostRequestHandlersTest extends BddTest {

    private HttpResponse<RequestCapture> captured;

    @Override
    @After
    public void tearDown() {
        super.tearDown();
        captured = null;
    }

    @Test
    public void onSuccessDoSomething() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .ifSuccess(r -> captured = r)
                .ifFailure(r -> fail("should not have been called"));

        assertNotNull(captured);
        captured.getBody().assertParam("foo", "bar");
    }

    @Test
    public void onFailDoSomething() {
        Unirest.get(MockServer.INVALID_REQUEST)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .ifFailure(r -> captured = r)
                .ifSuccess(r -> fail("should not have been called"));

        assertNotNull(captured);
        assertEquals(400, captured.getStatus());
    }

    @Test
    public void itsAFailIfTheMapperFails() {
        MockServer.setStringResponse("not what you expect");

        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .ifFailure(r -> captured = r)
                .ifSuccess(r -> fail("should not have been called"));

        assertNotNull(captured);
        assertEquals(200, captured.getStatus());
        assertTrue(captured.getParsingError().isPresent());
        assertEquals("not what you expect", captured.getParsingError().get().getOriginalBody());
    }

    @Test
    public void canConfigureAGlobalErrorHandler(){
        Error error = new Error();
        Unirest.config().errorHandler(error);

        Unirest.get(MockServer.INVALID_REQUEST).asEmpty();

        assertEquals(400, error.httpResponse.getStatus());
    }

    @Test
    public void canUseWithConsumer(){
        Error error = new Error();
        Unirest.config().errorHandler(error);

        Unirest.get(MockServer.INVALID_REQUEST).thenConsume(e -> {});

        assertEquals(400, error.httpResponse.getStatus());
    }

    @Test
    public void canConfigureAGlobalErrorHandlerAsync()  throws Exception {
        Error error = new Error();
        Unirest.config().errorHandler(error);

        Unirest.get(MockServer.INVALID_REQUEST).asEmptyAsync().get();

        assertEquals(400, error.httpResponse.getStatus());
    }

    @Test
    public void monitorTotalFailure() {
        MockedClient httpClient = new MockedClient(Unirest.config());
        RuntimeException e = new RuntimeException();
        httpClient.errorOnAccess(e);

        TestErrorHandler handler = new TestErrorHandler();
        handler.response = mock(HttpResponse.class);
        Unirest.config().httpClient(httpClient).errorHandler(handler);

        assertSame(handler.response, Unirest.get(MockServer.GET).asString());
        assertSame(e, handler.caught);
    }

    @Test @Ignore
    public void monitorTotalFailure_async() throws ExecutionException, InterruptedException {
        TestErrorHandler handler = new TestErrorHandler();
        handler.response = mock(HttpResponse.class);
        Unirest.config().errorHandler(handler);

        assertSame(handler.response, Unirest.get("http://localhost:00").asStringAsync().get());
        assertTrue(handler.caught instanceof ConnectException);
    }



    @Test
    public void onSuccessBeSuccessful() {
        HttpResponse<RequestCapture> response = Unirest.get(MockServer.GET)
            .queryString("foo", "bar")
            .asObject(RequestCapture.class);

        assertTrue(response.isSuccess());
    }

    @Test
    public void onFailBeUnsuccessful() {
        HttpResponse<RequestCapture> response = Unirest.get(MockServer.INVALID_REQUEST)
            .queryString("foo", "bar")
            .asObject(RequestCapture.class);

        assertFalse(response.isSuccess());
    }

    @Test
    public void beUnsuccessfulIfTheMapperFails() {
        MockServer.setStringResponse("not what you expect");

        HttpResponse<RequestCapture> response = Unirest.get(MockServer.GET)
            .queryString("foo", "bar")
            .asObject(RequestCapture.class);

        assertFalse(response.isSuccess());
    }

    private class Error implements Consumer<HttpResponse<?>> {

        public HttpResponse<?> httpResponse;

        @Override
        public void accept(HttpResponse<?> httpResponse) {

            this.httpResponse = httpResponse;
        }
    }
}
