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

import kong.unirest.Config;
import kong.unirest.UnirestConfigException;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

class SecurityConfig {
    private final Config config;
    private SSLContext sslContext;
    private SSLConnectionSocketFactory sslSocketFactory;


    public SecurityConfig(Config config) {
        this.config = config;
    }

    public PoolingHttpClientConnectionManager createManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(buildSocketFactory(),
                null, null, null,
                config.getTTL(), TimeUnit.MILLISECONDS);

        manager.setMaxTotal(config.getMaxConnections());
        manager.setDefaultMaxPerRoute(config.getMaxPerRoutes());
        return manager;
    }

    private Registry<ConnectionSocketFactory> buildSocketFactory() {
        try {
            if (!config.isVerifySsl()) {
                return createDisabledSSLContext();
            } else if (config.getKeystore() != null || config.getSslContext() != null) {
                return createCustomSslContext();
            } else {
                return createDefaultRegistry();
            }
        } catch (Exception e) {
            throw new UnirestConfigException(e);
        }
    }

    private Registry<ConnectionSocketFactory> createDefaultRegistry() {
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
    }

    private Registry<ConnectionSocketFactory> createCustomSslContext() {
        SSLConnectionSocketFactory socketFactory = getSocketFactory();
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", socketFactory)
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();
    }

    private Registry<ConnectionSocketFactory> createDisabledSSLContext() throws Exception {
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(new SSLContextBuilder()
                        .loadTrustMaterial(null, (x509CertChain, authType) -> true)
                        .build(),
                        NoopHostnameVerifier.INSTANCE))
                .build();
    }

    private SSLConnectionSocketFactory getSocketFactory() {
        if(sslSocketFactory == null) {
            sslSocketFactory = new SSLConnectionSocketFactory(createSslContext(), config.getProtocols(), config.getCiphers(), getHostnameVerifier());
        }
        return sslSocketFactory;
    }

    private HostnameVerifier getHostnameVerifier() {
        if(config.getHostnameVerifier() != null){
            return config.getHostnameVerifier();
        }
        return new DefaultHostnameVerifier();
    }

    private SSLContext createSslContext() {
        if(sslContext == null) {
            if(config.getSslContext() != null){
                sslContext = config.getSslContext();
            } else {
                try {
                    char[] pass = Optional.ofNullable(config.getKeyStorePassword())
                            .map(String::toCharArray)
                            .orElse(null);
                    sslContext = SSLContexts.custom()
                            .loadKeyMaterial(config.getKeystore(), pass)
                            .build();
                } catch (Exception e) {
                    throw new UnirestConfigException(e);
                }
            }
        }
        return sslContext;
    }

    public void configureSecurity(HttpClientBuilder cb) {
        if(config.getKeystore() != null){
            cb.setSSLContext(createSslContext());
            cb.setSSLSocketFactory(getSocketFactory());
        }
        if (!config.isVerifySsl()) {
            disableSsl(cb);
        }
    }

    private void disableSsl(HttpClientBuilder cb) {
        try {
            cb.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
            cb.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (arg0, arg1) -> true).build());
        } catch (Exception e) {
            throw new UnirestConfigException(e);
        }
    }
}
