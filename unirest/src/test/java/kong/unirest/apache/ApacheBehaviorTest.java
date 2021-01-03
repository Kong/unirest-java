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
import kong.unirest.Unirest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.client.HttpAsyncClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class ApacheBehaviorTest extends BddTest {
    @Test
    void issue_41_IllegalThreadStateExceptionUnderHighLoad() throws IOException {
        Unirest.get(MockServer.GET).asStringAsync();

        HttpAsyncClient first = Unirest.config().getAsyncClient().getClient();
        IntStream.range(1, 50).forEach(i ->{
            assertSame(first, Unirest.config().getAsyncClient().getClient());
        });

        ((CloseableHttpAsyncClient)Unirest.config().getAsyncClient().getClient()).close();
        Unirest.get(MockServer.GET).asStringAsync();

        HttpAsyncClient second = Unirest.config().getAsyncClient().getClient();
        assertNotSame(first, second);

        IntStream.range(1, 50).forEach(i ->{
            assertSame(second, Unirest.config().getAsyncClient().getClient());
        });
    }

    @Test
    public void setTimeoutsAndCustomClient() {
        try {
            Unirest.config().connectTimeout(1000).socketTimeout(2000);
        } catch (Exception e) {
            fail();
        }

        try {
            Unirest.config().asyncClient(HttpAsyncClientBuilder.create().build());
        } catch (Exception e) {
            fail();
        }

        try {
            Unirest.config().asyncClient(HttpAsyncClientBuilder.create().build());
            Unirest.config().connectTimeout(1000).socketTimeout(2000);
            fail();
        } catch (Exception e) {
            // Ok
        }

        try {
            Unirest.config().httpClient(HttpClientBuilder.create().build());
            Unirest.config().connectTimeout(1000).socketTimeout(2000);
            fail();
        } catch (Exception e) {
            // Ok
        }
    }

    @Test
    void canSaveSomeOptions(){
        HttpRequestInterceptor i = mock(HttpRequestInterceptor.class);
        CloseableHttpAsyncClient c = mock(CloseableHttpAsyncClient.class);

        Unirest.config()
                .addInterceptor(i)
                .connectTimeout(4000)
                .asyncClient(c);

        Unirest.shutDown(false);

        assertNotEquals(c, Unirest.config().getAsyncClient());
        assertEquals(i, Unirest.config().getInterceptor().get(0));
        assertEquals(4000, Unirest.config().getConnectionTimeout());
    }
}
