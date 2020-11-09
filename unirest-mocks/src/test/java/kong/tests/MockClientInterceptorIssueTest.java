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

import kong.unirest.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MockClientInterceptorIssueTest {

    private final ExampleInterceptor interceptor = new ExampleInterceptor();

    private static class ExampleInterceptor implements Interceptor {

        public HttpResponse<?> response;

        @Override
        public void onRequest(HttpRequest<?> request, Config config) {
            request.header("Test-Header", "Header Value");
        }

        @Override
        public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
            this.response = response;
        }
    }

    private UnirestInstance unirestInstance;

    @BeforeEach
    public void setup() {
        this.unirestInstance = Unirest.spawnInstance();
        this.unirestInstance.config().interceptor(interceptor);
    }

    @Test
    void interceptor_is_called() {
        MockClient unirestMock = MockClient.register(this.unirestInstance);

        unirestMock
                .expect(HttpMethod.GET, "http://example.com")
                .header("Test-Header", "Header Value")
                .thenReturn("Call Result");

        HttpResponse<String> response = this.unirestInstance.get("http://example.com").asString();

        unirestMock.verifyAll();
        assertEquals("Call Result", response.getBody());
        assertSame(response, interceptor.response);
    }
}
