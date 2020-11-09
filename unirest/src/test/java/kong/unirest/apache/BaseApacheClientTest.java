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

import kong.unirest.Headers;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BaseApacheClientTest {

    private HttpUriRequest request;
    private Headers headers;

    @BeforeEach
    void setUp() {
        request = new HttpGet();
        headers = new Headers();
    }

    @Test
    void basicHost() {
        request = new HttpGet(URI.create("http://zombo.com"));
        HttpHost host = BaseApacheClient.determineTarget(request, headers);
        assertEquals("zombo.com", host.getHostName());
        assertNull(host.getAddress());
        assertEquals(-1, host.getPort());
        assertEquals("http", host.getSchemeName());
    }

    @Test
    void basicHost_withPort() {
        request = new HttpGet(URI.create("http://zombo.com:8080"));
        HttpHost host = BaseApacheClient.determineTarget(request, headers);
        assertEquals("zombo.com", host.getHostName());
        assertNull(host.getAddress());
        assertEquals(8080, host.getPort());
        assertEquals("http", host.getSchemeName());
    }

    @Test
    void willIgnoreHostHeaderprovided_IfNotIPv4() {
        headers.add("Host","homestarrunner.com");
        request = new HttpGet(URI.create("http://zombo.com"));
        HttpHost host = BaseApacheClient.determineTarget(request, headers);
        assertEquals("zombo.com", host.getHostName());
    }

    @Test
    void willUseHostHeaderprovided_IfNotIPv4() throws UnknownHostException {
        headers.add("Host","homestarrunner.com");
        request = new HttpGet(URI.create("http://127.0.0.1"));
        HttpHost host = BaseApacheClient.determineTarget(request, headers);
        assertEquals("homestarrunner.com", host.getHostName());
        assertEquals(InetAddress.getByName("127.0.0.1"), host.getAddress());
    }

    @Test
    void ifUrlIsRelativeThenReturnNull() {
        request = new HttpGet(URI.create("/somewhere"));
        HttpHost host = BaseApacheClient.determineTarget(request, headers);
        assertNull(host);
    }
}