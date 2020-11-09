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

import kong.unirest.GetRequest;
import kong.unirest.TestUtil;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

import java.io.InputStream;
import java.security.KeyStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled // dont normally run these because they depend on badssl.com
class CertificateTests extends BddTest {

    @Test
    void canDoClientCertificates() throws Exception {
        Unirest.config().clientCertificateStore(readStore(), "badssl.com");

        Unirest.get("https://client.badssl.com/")
                .asString()
                .ifFailure(r -> fail(r.getStatus() + " request failed " + r.getBody()))
                .ifSuccess(r -> System.out.println(" woot "));;
    }


    @Test
    void canLoadKeyStoreByPath() {
        Unirest.config().clientCertificateStore("src/test/resources/certs/badssl.com-client.p12", "badssl.com");

        Unirest.get("https://client.badssl.com/")
                .asString()
                .ifFailure(r -> fail(r.getStatus() + " request failed " + r.getBody()))
                .ifSuccess(r -> System.out.println(" woot "));;
    }

    @Test
    void loadWithSSLContext() throws Exception {
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(readStore(), "badssl.com".toCharArray()) // use null as second param if you don't have a separate key password
                .build();

        Unirest.config().sslContext(sslContext);

        int response = Unirest.get("https://client.badssl.com/").asEmpty().getStatus();
        assertEquals(200, response);
    }

    @Test
    void loadWithSSLContextAndProtocol() throws Exception {
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(readStore(), "badssl.com".toCharArray()) // use null as second param if you don't have a separate key password
                .build();

        Unirest.config().sslContext(sslContext).protocols("TLSv1.2");

        int response = Unirest.get("https://client.badssl.com/").asEmpty().getStatus();
        assertEquals(200, response);
    }

    @Test
    void loadWithSSLContextAndCipher() throws Exception {
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(readStore(), "badssl.com".toCharArray()) // use null as second param if you don't have a separate key password
                .build();

        Unirest.config().sslContext(sslContext).ciphers("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");

        int response = Unirest.get("https://client.badssl.com/").asEmpty().getStatus();
        assertEquals(200, response);
    }

    @Test
    void loadWithSSLContextAndCipherAndProtocol() throws Exception {
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(readStore(), "badssl.com".toCharArray()) // use null as second param if you don't have a separate key password
                .build();

        Unirest.config()
                .sslContext(sslContext)
                .protocols("TLSv1.2")
                .ciphers("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");

        int response = Unirest.get("https://client.badssl.com/").asEmpty().getStatus();
        assertEquals(200, response);
    }

    @Test
    void sslHandshakeFailsWhenServerIsReceivingAnUnsupportedCipher() throws Exception {
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(readStore(), "badssl.com".toCharArray()) // use null as second param if you don't have a separate key password
                .build();

        Unirest.config()
                .sslContext(sslContext)
                .protocols("TLSv1.2")
                .ciphers("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");

        GetRequest request = Unirest.get("https://client.badssl.com/");
        assertThrows(UnirestException.class, request::asEmpty);
    }

    @Test
    void clientPreventsToUseUnsafeProtocol() throws Exception {
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(readStore(), "badssl.com".toCharArray()) // use null as second param if you don't have a separate key password
                .build();

        Unirest.config()
                .sslContext(sslContext)
                .protocols("SSLv3");

        GetRequest request = Unirest.get("https://client.badssl.com/");
        assertThrows(UnirestException.class, request::asEmpty);
    }

    @Test
    void canSetHoestNameVerifyer() {
        Unirest.config().hostnameVerifier(new NoopHostnameVerifier());

        int response = Unirest.get("https://badssl.com/").asEmpty().getStatus();
        assertEquals(200, response);
    }

    @Test
    void rawApacheClientCert() throws Exception {
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(readStore(), "badssl.com".toCharArray()) // use null as second param if you don't have a separate key password
                .build();

        HttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();

        HttpResponse response = httpClient.execute(new HttpGet("https://client.badssl.com/"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();

        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        EntityUtils.consume(entity);
    }

    @Test
    void rawApacheWithConnectionManager() throws Exception {
        SSLContext sc = SSLContexts.custom()
                .loadKeyMaterial(readStore(), "badssl.com".toCharArray()) // use null as second param if you don't have a separate key password
                .build();

        SSLConnectionSocketFactory sslSocketFactory =
                new SSLConnectionSocketFactory(sc, new NoopHostnameVerifier());


        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("https", sslSocketFactory)
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .build();

        PoolingHttpClientConnectionManager cm =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        CloseableHttpClient httpClient =
                HttpClients.custom()
                        .setSSLSocketFactory(sslSocketFactory)
                        .setConnectionManager(cm)
                        .build();

        Unirest.config().httpClient(httpClient);

        Unirest.get("https://client.badssl.com/")
                .asString()
                .ifFailure(r -> fail(r.getStatus() + " request failed " + r.getBody()))
                .ifSuccess(r -> System.out.println(" woot "));

    }

    @Test
    void badName() {
        fails("https://wrong.host.badssl.com/",
                SSLPeerUnverifiedException.class,
                "javax.net.ssl.SSLPeerUnverifiedException: " +
                        "Certificate for <wrong.host.badssl.com> doesn't match any of the subject alternative names: " +
                        "[*.badssl.com, badssl.com]");
        disableSsl();
        canCall("https://wrong.host.badssl.com/");
    }

    @Test
    void expired() {
        fails("https://expired.badssl.com/",
                SSLHandshakeException.class,
                "javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: " +
                        "PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed");
        disableSsl();
        canCall("https://expired.badssl.com/");
    }

    @Test
    void selfSigned() {
        fails("https://self-signed.badssl.com/",
                SSLHandshakeException.class,
                "javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: " +
                        "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: " +
                        "unable to find valid certification path to requested target");
        disableSsl();
        canCall("https://self-signed.badssl.com/");
    }

    @Test
    void badNameAsync() {
        failsAsync("https://wrong.host.badssl.com/",
                SSLPeerUnverifiedException.class,
                "javax.net.ssl.SSLPeerUnverifiedException: " +
                        "Host name 'wrong.host.badssl.com' does not match the certificate subject provided by the peer " +
                        "(CN=*.badssl.com, O=Lucas Garron Torres, L=Walnut Creek, ST=California, C=US)");
        disableSsl();
        canCallAsync("https://wrong.host.badssl.com/");
    }

    @Test
    void expiredAsync() {
        failsAsync("https://expired.badssl.com/",
                SSLHandshakeException.class,
                "javax.net.ssl.SSLHandshakeException: General SSLEngine problem");
        disableSsl();
        canCallAsync("https://expired.badssl.com/");
    }

    @Test
    void selfSignedAsync() {
        failsAsync("https://self-signed.badssl.com/",
                SSLHandshakeException.class,
                "javax.net.ssl.SSLHandshakeException: General SSLEngine problem");
        disableSsl();
        canCallAsync("https://self-signed.badssl.com/");
    }

    private void disableSsl() {
        Unirest.config().reset().verifySsl(false);
    }

    private void failsAsync(String url, Class<? extends Throwable> exClass, String error) {
        TestUtil.assertExceptionUnwrapped(() -> Unirest.get(url).asEmptyAsync().get(),
                exClass,
                error);
    }

    private void fails(String url, Class<? extends Throwable> exClass, String error) {
        TestUtil.assertExceptionUnwrapped(() -> Unirest.get(url).asEmpty(),
                exClass,
                error);
    }

    private void canCall(String url) {
        assertEquals(200, Unirest.get(url).asEmpty().getStatus());
    }

    private void canCallAsync(String url) {
        try {
            assertEquals(200, Unirest.get(url).asEmptyAsync().get().getStatus());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private KeyStore readStore() throws Exception {
        try (InputStream keyStoreStream = this.getClass().getResourceAsStream("/certs/badssl.com-client.p12")) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyStoreStream, "badssl.com".toCharArray());
            return keyStore;
        }
    }
}
