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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import kong.unirest.core.Client;
import kong.unirest.core.Unirest;
import kong.unirest.core.java.JavaClient;
import org.assertj.guava.api.MultimapAssert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestUtil {
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

    public static File getImageFile() {
        return rezFile("/image.jpg");
    }

    public static File rezFile(String name) {
        try {
            return new File(MockServer.class.getResource(name).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream rezInput(String name) {
        try {
            return MockServer.class.getResourceAsStream(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getFileBytes(String s) {
        try {
            final InputStream stream = new FileInputStream(rezFile(s));
            final byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            stream.close();
            return bytes;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static <K, V> Map<K, V> mapOf(Object... keyValues) {
        Map<K, V> map = new HashMap<>();

        K key = null;
        for (int index = 0; index < keyValues.length; index++) {
            if (index % 2 == 0) {
                key = (K)keyValues[index];
            }
            else {
                map.put(key, (V)keyValues[index]);
            }
        }

        return map;
    }

    public static String getResource(String resourceName) {
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Client getFailureClient() throws Exception {
        HttpClient mock = mock(HttpClient.class);
        when(mock.send(any(java.net.http.HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Something horrible happened"));
        return new JavaClient(Unirest.config(), mock);
    }

    public static void reset() {

    }

    public static Client getFailureAsyncClient() {
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

    public static <K, V> MultimapAssert<K, V> assertMultiMap(final Multimap<K, V> actual) {
        return org.assertj.guava.api.Assertions.assertThat(actual);
    }

    public static final class Matchers {
        public static Consumer<HeaderAsserts.HeaderValue> isBase64Encoded() {
            return h -> {
              var value = h.getValue();
              try {
                  Base64.getDecoder().decode(value);
              }catch (IllegalArgumentException e){
                  fail("Header was not base64 encoded: " + value);
              }
            };
        }
    }
}
