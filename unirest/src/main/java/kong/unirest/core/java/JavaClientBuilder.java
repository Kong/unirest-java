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

package kong.unirest.core.java;

import kong.unirest.core.Config;
import kong.unirest.core.Proxy;
import kong.unirest.core.UnirestConfigException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.net.*;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

class JavaClientBuilder implements Function<Config, HttpClient> {
    @Override
    public HttpClient apply(Config config) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .followRedirects(redirectPolicy(config))
                .connectTimeout(Duration.ofMillis(config.getConnectionTimeout()));
        configureTLSOptions(config, builder);

        if(config.getVersion() != null){
            builder.version(config.getVersion());
        }

        if(config.getCustomExecutor() != null){
            builder.executor(config.getCustomExecutor());
        }
        if(config.getEnabledCookieManagement()){
            builder = builder.cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        }
        if(config.getProxy() != null){
            createProxy(builder, config.getProxy());
        }
        if(config.useSystemProperties()){
            builder.proxy(ProxySelector.getDefault());
        }
        return builder.build();
    }

    private void configureTLSOptions(Config config, HttpClient.Builder builder) {
        SSLParameters params = new SSLParameters();
        if(!config.isVerifySsl()){
            builder.sslContext(NeverUseInProdTrustManager.create());
        } else if (config.getKeystore() != null){
            builder.sslContext(getSslContext(config));
        } else if(config.getSslContext() != null){
            builder.sslContext(config.getSslContext());
        }
        if(config.getProtocols() != null){
            params.setProtocols(config.getProtocols());
        }
        if(config.getCiphers() != null){
            params.setCipherSuites(config.getCiphers());
        }
        builder.sslParameters(params);
    }

    private void createProxy(HttpClient.Builder builder, Proxy proxy) {
        InetSocketAddress address = InetSocketAddress.createUnresolved(proxy.getHost(), proxy.getPort());
        builder.proxy(ProxySelector.of(address));
        if(proxy.isAuthenticated()){
            builder.authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxy.getUsername(), proxy.getPassword().toCharArray());
                }
            });
        }
    }

    private SSLContext getSslContext(Config config) {
        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(config.getKeystore());

            char[] pass = Optional.ofNullable(config.getKeyStorePassword())
                    .map(String::toCharArray)
                    .orElse(null);

            return SSLContextBuilder.create()
                    .loadKeyMaterial(config.getKeystore(), pass)
                    .build();
        }catch (Exception e){
            throw new UnirestConfigException(e);
        }
    }

    private HttpClient.Redirect redirectPolicy(Config config) {
        if(config.getFollowRedirects()){
            return HttpClient.Redirect.NORMAL;
        }
        return HttpClient.Redirect.NEVER;
    }

}
