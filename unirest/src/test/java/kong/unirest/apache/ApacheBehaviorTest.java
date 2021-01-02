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
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

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
}
