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

package kong.unirest;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.ManagementFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ClientFactoryTest {

    @Before @After
    public void before(){
        Unirest.shutDown(true);
    }

    @Test
    public void shouldReuseThreadPool() {
        int startingCount = ManagementFactory.getThreadMXBean().getThreadCount();
        //IntStream.range(0,100).forEach(i -> ClientFactory.refresh());
        assertThat(ManagementFactory.getThreadMXBean().getThreadCount(), is(lessThan(startingCount + 10)));
    }

    @Test
    public void canSaveSomeOptions(){
        HttpRequestInterceptor i = mock(HttpRequestInterceptor.class);
        CloseableHttpAsyncClient c = mock(CloseableHttpAsyncClient.class);

        Unirest.config()
                .addInterceptor(i)
                .connectTimeout(4000)
                .asyncClient(c);

        Unirest.shutDown(false);

        assertNotEquals(c, Unirest.config().getAsyncClient());
        assertEquals(i, Unirest.config().getInterceptors().get(0));
        assertEquals(4000, Unirest.config().getConnectionTimeout());
    }
}