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

package kong.unirest.apache;

import BehaviorTests.BddTest;
import BehaviorTests.MockServer;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApacheClientTest extends BddTest {
    boolean requestConfigUsed = false;
    boolean interceptorCalled = false;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Override
    @AfterEach
    public void tearDown() {
        super.tearDown();
        requestConfigUsed = false;
    }

    @Test
    void settingACustomClientWithBuilder() {
        HttpClient client = HttpClientBuilder.create()
                .addInterceptorFirst((HttpRequestInterceptor) (q, w) -> interceptorCalled = true)
                .build();

        Unirest.config().httpClient(ApacheClient.builder(client)
                .withRequestConfig((c, w) -> {
                    requestConfigUsed = true;
                    return RequestConfig.custom().build();
                }));

        assertMockResult();
    }

    @Test
    void canSetACustomAsyncClientWithBuilder() throws Exception {
        try(CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create().build()) {
            client.start();

            Unirest.config().asyncClient(ApacheAsyncClient.builder(client)
                    .withRequestConfig((c, w) -> {
                        requestConfigUsed = true;
                        return RequestConfig.custom().build();
                    })
            );

            assertAsyncResult();
            assertTrue(requestConfigUsed);
        }
    }

    private void assertAsyncResult() throws Exception {
        MockServer.setStringResponse("Howdy Ho!");
        HttpResponse<String> result =  Unirest.get(MockServer.GET).asStringAsync().get();
        assertEquals(200, result.getStatus());
        assertEquals("Howdy Ho!", result.getBody());
    }

    private void assertMockResult() {
        Unirest.get(MockServer.GET).asString();
        assertTrue(requestConfigUsed);
        assertTrue(interceptorCalled);
    }
}
