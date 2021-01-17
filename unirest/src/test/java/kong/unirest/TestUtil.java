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

import BehaviorTests.MockServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import kong.unirest.java.JavaClient;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtil {
    private static final ObjectMapper om = new ObjectMapper();

    static {
        om.registerModule(new GuavaModule());
    }

    public static String toString(InputStream is) {
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }

    public static <T> T readValue(InputStream i, Class<T> clss) {
        try {
            return om.readValue(i, clss);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertException(ExRunnable runnable, Class<? extends Throwable> exClass, String message) {
        try{
            runnable.run();
            fail("Expected exception but got none. \nExpected " + exClass);
        } catch (Exception e){
            if (!e.getClass().isAssignableFrom(exClass)) {
                fail("Expected wrong exception type \n Expected: " + exClass + "\n but got " + e.getClass() + "\n\n" + Throwables.getStackTraceAsString(e));
            }
            assertEquals(message, e.getMessage(), "Wrong Error Message");
        }
    }

    public static void assertExceptionUnwrapped(ExRunnable runnable, Class<? extends Throwable> exClass, String message) {
        try{
            runnable.run();
            fail("Expected exception but got none. \nExpected " + exClass);
        } catch (Exception e){
            if (!e.getCause().getClass().isAssignableFrom(exClass)) {
                fail("Expected wrong exception type \n Expected: " + exClass + "\n but got " + e.getCause().getClass());
            }
            assertEquals(message, e.getMessage(), "Wrong Error Message");
        }
    }

    public static <T> T readValue(String body, Class<T> as) {
        try {
            return om.readValue(body, as);
        } catch (IOException e) {
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

    public static void assertBasicAuth(String raw, String username, String password) {
        assertNotNull(raw, "Authorization Header Missing");
        String credentials = raw.replace("Basic ","");
        assertEquals(username + ":" + password, new String(Base64.getDecoder().decode(credentials)));
    }

    public static String getResource(String resourceName){
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static File rezFile(String name) {
        try {
            return new File(MockServer.class.getResource(name).toURI());
        } catch (URISyntaxException e) {
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

    public static File getImageFile() {
        return rezFile("/image.jpg");
    }

    public static <T> T defaultIfNull(T t, Supplier<T> supplier) {
        if(t == null){
            return supplier.get();
        }
        return t;
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

    public static AsyncClient getFailureAsyncClient()  throws Exception {

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

    @FunctionalInterface
    public interface ExRunnable {
       void run() throws Exception;
    }

}
