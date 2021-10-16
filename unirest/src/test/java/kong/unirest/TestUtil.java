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
import kong.unirest.java.JavaClient;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.io.InputStream;

import java.security.KeyStore;
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

    public static KeyStore readStore() throws Exception {
        try (InputStream keyStoreStream = TestUtil.class.getResourceAsStream("/certs/badssl.com-client.p12")) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyStoreStream, "badssl.com".toCharArray());
            return keyStore;
        }
    }

    public static Client getFailureClient() throws Exception {
        HttpClient mock = mock(HttpClient.class);
        when(mock.send(any(java.net.http.HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Something horrible happened"));
        return new JavaClient(Unirest.config(), mock);
    }

    public static Client getFailureAsyncClient()  throws Exception {

        HttpClient client  = HttpClient.newBuilder()
               //.sslContext(new SSLContext())
                .authenticator(new Authenticator() {
                    @Override
                    public PasswordAuthentication requestPasswordAuthenticationInstance(String host, InetAddress addr, int port, String protocol, String prompt, String scheme, URL url, RequestorType reqType) {
                        throw new RuntimeException("boo");
                    }
                }).build();
        return new JavaClient(Unirest.config(), client);
    }

}
