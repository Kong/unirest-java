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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import kong.unirest.AsyncClient;
import kong.unirest.Client;
import kong.unirest.Unirest;
import kong.unirest.Util;
import kong.unirest.apache.ApacheAsyncClient;
import kong.unirest.apache.ApacheClient;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import java.io.IOException;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtil {
    private static final ObjectMapper om = new ObjectMapper();

    static {
        om.registerModule(new GuavaModule());
    }

    public static <T> T readValue(String body, Class<T> as) {
        try {
            return om.readValue(body, as);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getResource(String resourceName){
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void freeze(Instant now) {
        Util.freezeClock(now);
    }

    public static void reset() {
        Util.resetClock();
    }


    public static Client getFailureClient() throws IOException {
        HttpClient client = mock(HttpClient.class);
        when(client.execute(any(HttpHost.class), any(HttpUriRequest.class))).thenThrow(new IOException("Something horrible happened"));
        when(client.execute(any(HttpUriRequest.class))).thenThrow(new IOException("Something horrible happened"));
        return new ApacheClient(client, Unirest.config());
    }

    public static AsyncClient getFailureAsyncClient() {
        CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create()
                .addInterceptorFirst((HttpRequestInterceptor) (r, c) -> {
                    throw new IOException("Something horrible happened");
                }).build();
        client.start();
        return new ApacheAsyncClient(client, Unirest.config());
    }

}
