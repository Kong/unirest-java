/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

import org.apache.http.client.HttpClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

    @InjectMocks
    private Config config;

    @Test
    public void shouldKeepConnectionTimeOutDefault(){
        assertEquals(Config.DEFAULT_CONNECT_TIMEOUT, config.getConnectionTimeout());
    }

    @Test
    public void shouldKeepSocketTimeoutDefault(){
        assertEquals(Config.DEFAULT_SOCKET_TIMEOUT, config.getSocketTimeout());
    }

    @Test
    public void shouldKeepMaxTotalDefault(){
        assertEquals(Config.DEFAULT_MAX_CONNECTIONS, config.getMaxConnections());
    }

    @Test
    public void shouldKeepMaxPerRouteDefault(){
        assertEquals(Config.DEFAULT_MAX_PER_ROUTE, config.getMaxPerRoutes());
    }

    @Test
    public void onceTheConfigIsRunningYouCannotChangeConfig(){
        config.httpClient(mock(HttpClient.class));
        config.asyncClient(mock(HttpAsyncClient.class));

        TestUtil.assertException(() -> config.socketTimeout(533),
                UnirestConfigException.class,
                "Http Clients are already build in order to build a new config execute Unirest.config().reset() " +
                        "before changing settings. \n" +
                        "This should be done rarely.");

        config.reset().socketTimeout(533);
    }
}