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

package kong.unirest.core;

import kong.unirest.core.java.JavaClient;
import kong.unirest.core.json.CoreFactory;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.net.http.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class Config {
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final String JDK_HTTPCLIENT_KEEPALIVE_TIMEOUT = "jdk.httpclient.keepalive.timeout";

    private Optional<Client> client = Optional.empty();
    private Supplier<ObjectMapper> objectMapper;

    private Executor customExecutor;
    private Headers headers;
    private Proxy proxy;
    private int connectionTimeout;
    private Integer requestTimeout;
    private boolean followRedirects;
    private boolean cookieManagement;
    private boolean useSystemProperties;
    private String defaultResponseEncoding = StandardCharsets.UTF_8.name();
    private Function<Config, Client> clientBuilder;
    private boolean requestCompressionOn = true;
    private boolean verifySsl = true;
    private KeyStore keystore;
    private Supplier<String> keystorePassword = () -> null;
    private String cookieSpec;
    private UniMetric metrics = new NoopMetric();
    private SSLContext sslContext;
    private String[] ciphers;
    private String[] protocols;
    private CompoundInterceptor interceptor = new CompoundInterceptor();
    private String defaultBaseUrl;
    private CacheManager cache;
    private HttpClient.Version version = HttpClient.Version.HTTP_2;
    private RetryStrategy retry;

    public Config() {
        setDefaults();
    }

    private void setDefaults() {
        proxy = null;
        cache = null;
        customExecutor =  null;
        headers = new Headers();
        connectionTimeout = DEFAULT_CONNECT_TIMEOUT;
        requestTimeout = null;
        followRedirects = true;
        useSystemProperties = false;
        cookieManagement = true;
        requestCompressionOn = true;
        verifySsl = true;
        keystore = null;
        keystorePassword = null;
        sslContext = null;
        ciphers = null;
        protocols = null;
        defaultBaseUrl = null;
        interceptor = new CompoundInterceptor();
        retry = null;
        objectMapper = () -> CoreFactory.getCore().getObjectMapper();
        version = HttpClient.Version.HTTP_2;

        try {
            clientBuilder = JavaClient::new;
        }catch (BootstrapMethodError e){
            throw new UnirestException("It looks like you are using an older version of Apache Http Client. \n" +
                    "For security and performance reasons Unirest requires the most recent version. Please upgrade.", e);
        }
    }

    /**
     * Set the HttpClient implementation to use for every synchronous request
     *
     * @param httpClient Custom httpClient implementation
     * @return this config object
     */
    public Config httpClient(Client httpClient) {
        client = Optional.ofNullable(httpClient);
        return this;
    }

    /**
     * Provide a builder for a client
     *
     * @param httpClient Custom httpClient implementation
     * @return this config object
     */
    public Config httpClient(Function<Config, Client> httpClient) {
        clientBuilder = httpClient;
        return this;
    }

    /**
     * Sets a custom executor for requests
     * @param executor – the Executor
     * @return this config builder
     * Implementation Note:
     * The default executor uses a thread pool, with a custom thread factory.
     * If a security manager has been installed, the thread factory creates
     * threads that run with an access control context that has no permissions.
     */
    public Config executor(Executor executor){
        this.customExecutor  = executor;
        return this;
    }

    /**
     * Set a proxy
     *
     * @param value Proxy settings object.
     * @return this config object
     */
    public Config proxy(Proxy value) {
        validateClientsNotRunning();
        this.proxy = value;
        return this;
    }

    /**
     * Set a proxy
     *
     * @param host the hostname of the proxy server.
     * @param port the port of the proxy server
     * @return this config object
     */
    public Config proxy(String host, int port) {
        return proxy(new Proxy(host, port));
    }

    /**
     * Set an authenticated proxy
     *
     * @param host the hostname of the proxy server.
     * @param port the port of the proxy server
     * @param username username for authenticated proxy
     * @param password password for authenticated proxy
     * @return this config object
     */
    public Config proxy(String host, int port, String username, String password) {
        return proxy(new Proxy(host, port, username, password));
    }

    /**
     * Set the ObjectMapper implementation to use for Response to Object binding
     *
     * @param om Custom implementation of ObjectMapper interface
     * @return this config object
     */
    public Config setObjectMapper(ObjectMapper om) {
        this.objectMapper = () -> om;
        return this;
    }

    /**
     * Set a custom SSLContext.
     *
     * @param ssl the SSLContext to use for custom ssl context
     * @return this config object
     * @throws UnirestConfigException if a keystore was already configured.
     */
    public Config sslContext(SSLContext ssl) {
        verifySecurityConfig(this.keystore);
        this.sslContext = ssl;
        return this;
    }

    /**
     * Set a custom array of ciphers
     * @param values the array of ciphers
     * @return this config object
     */
    public Config ciphers(String... values) {
        this.ciphers = values;
        return this;
    }

    /**
     * Set a custom array of protocols
     * @param values the array of protocols
     * @return this config object
     */
    public Config protocols(String... values) {
        this.protocols = values;
        return this;
    }

    private void verifySecurityConfig(Object thing) {
        if(thing != null){
            throw new UnirestConfigException("You may only configure a SSLContext OR a Keystore, but not both");
        }
    }

    /**
     * Set a custom keystore
     *
     * @param store the keystore to use for a custom ssl context
     * @param password the password for the store
     * @return this config object
     * @throws UnirestConfigException if a SSLContext was already configured.
     */
    public Config clientCertificateStore(KeyStore store, String password) {
        verifySecurityConfig(this.sslContext);
        this.keystore = store;
        this.keystorePassword = () -> password;
        return this;
    }

    /**
     * Set a custom keystore via a file path. Must be a valid PKCS12 file
     *
     * @param fileLocation the path keystore to use for a custom ssl context
     * @param password the password for the store
     * @return this config object
     * @throws UnirestConfigException if a SSLContext was already configured.
     */
    public Config clientCertificateStore(String fileLocation, String password) {
        verifySecurityConfig(this.sslContext);
        try (InputStream keyStoreStream = Util.getFileInputStream(fileLocation)) {
            this.keystorePassword = () -> password;
            this.keystore = KeyStore.getInstance("PKCS12");
            this.keystore.load(keyStoreStream, keystorePassword.get().toCharArray());
        } catch (Exception e) {
            throw new UnirestConfigException(e);
        }
        return this;
    }

    /**
     * Sets the connect timeout duration for this client.
     *
     * <p> In the case where a new connection needs to be established, if
     * the connection cannot be established within the given {@code
     * duration}, then {@link HttpClient#send(java.net.http.HttpRequest, HttpResponse.BodyHandler)
     * HttpClient::send} throws an {@link HttpConnectTimeoutException}, or
     * {@link HttpClient#sendAsync(HttpRequest, HttpResponse.BodyHandler)
     * HttpClient::sendAsync} completes exceptionally with an
     * {@code HttpConnectTimeoutException}. If a new connection does not
     * need to be established, for example if a connection can be reused
     * from a previous request, then this timeout duration has no effect.
     *
     * @param inMillies the duration to allow the underlying connection to be
     *                 established
     * @return this builder
     */
    public Config connectTimeout(int inMillies) {
        validateClientsNotRunning();
        this.connectionTimeout = inMillies;
        return this;
    }

    /**
     * Sets a default timeout for all requests. If the response is not received
     * within the specified timeout then an {@link HttpTimeoutException} is
     * thrown.
     * completes exceptionally with an {@code HttpTimeoutException}. The effect
     * of not setting a timeout is the same as setting an infinite Duration, ie.
     * block forever.
     *
     * @param inMillies the timeout duration in millies
     * @return this builder
     * @throws IllegalArgumentException if the duration is non-positive
     */
    public Config requestTimeout(Integer inMillies){
        if(inMillies != null && inMillies < 1){
            throw new IllegalArgumentException("request timeout must be a positive integer");
        }
        this.requestTimeout = inMillies;
        return this;
    }

    /**
     * Clear default headers
     * @return this config object
     */
    public Config clearDefaultHeaders() {
        headers.clear();
        return this;
    }

    /**
     * Default basic auth credentials
     * @param username the username
     * @param password the password
     * @return this config object
     */
    public Config setDefaultBasicAuth(String username, String password) {
        headers.replace("Authorization", Util.toBasicAuthValue(username, password));
        return this;
    }

    /**
     * Set default header to appear on all requests
     *
     * @param name  The name of the header.
     * @param value The value of the header.
     * @return this config object
     */
    public Config setDefaultHeader(String name, String value) {
        headers.replace(name, value);
        return this;
    }

    /**
     * Set default header to appear on all requests, value is through a Supplier
     * This is useful for adding tracing elements to requests.
     *
     * @param name  The name of the header.
     * @param value a supplier that will get called as part of the request.
     * @return this config object
     */
    public Config setDefaultHeader(String name, Supplier<String> value) {
        headers.add(name, value);
        return this;
    }

    /**
     * Add default header to appear on all requests
     *
     * @param name  The name of the header.
     * @param value The value of the header.
     * @return this config object
     */
    public Config addDefaultHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    /**
     * Adds a default cookie to be added to all requests with this config
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @return this config object
     */
    public Config addDefaultCookie(String name, String value) {
        return addDefaultCookie(new Cookie(name, value));
    }

    /**
     * Adds a default cookie to be added to all requests with this config
     * @param cookie the cookie
     * @return this config object
     */
    public Config addDefaultCookie(Cookie cookie) {
        this.headers.cookie(cookie);
        return this;
    }

    /**
     * Add a metric object for instrumentation
     * @param metric a UniMetric object
     * @return this config object
     */
    public Config instrumentWith(UniMetric metric) {
        this.metrics = metric;
        return this;
    }

    /**
     * Add a Interceptor which will be called before and after the request;
     * @param value The Interceptor
     * @return this config object
     */
    public Config interceptor(Interceptor value) {
        Objects.requireNonNull(value, "Interceptor may not be null");
        this.interceptor.register(value);
        return this;
    }

    /**
     * Allow the client to follow redirects. Defaults to TRUE
     *
     * @param enable The name of the header.
     * @return this config object
     */
    public Config followRedirects(boolean enable) {
        validateClientsNotRunning();
        this.followRedirects = enable;
        return this;
    }

    /**
     * Allow the client to manage cookies. Defaults to TRUE
     *
     * @param enable The name of the header.
     * @return this config object
     */
    public Config enableCookieManagement(boolean enable) {
        validateClientsNotRunning();
        this.cookieManagement = enable;
        return this;
    }

    /**
     * Toggle verifying SSL/TLS certificates. Defaults to TRUE
     *
     * @param value a bool is its true or not.
     * @return this config object
     */
    public Config verifySsl(boolean value) {
        this.verifySsl = value;
        return this;
    }

    /**
     * Tell the HttpClients to use the system properties for things like proxies
     *
     * @param value a bool is its true or not.
     * @return this config object
     */
    public Config useSystemProperties(boolean value) {
        this.useSystemProperties = value;
        return this;
    }

    /**
     * Turn on or off requesting all content as compressed. (GZIP encoded)
     * Default is true
     *
     * @param value a bool is its true or not.
     * @return this config object
     */
    public Config requestCompression(boolean value) {
        this.requestCompressionOn = value;
        return this;
    }

    /**
     * Sets a cookie policy
     * Acceptable values:
     *  'default' (same as Netscape),
     *  'netscape',
     *  'ignoreCookies',
     *  'standard' (RFC 6265 interoprability profile) ,
     *  'standard-strict' (RFC 6265 strict profile)
     *
     * @param policy: the policy for cookies to follow
     * @return this config object
     */
    public Config cookieSpec(String policy) {
        this.cookieSpec = policy;
        return this;
    }

    /**
     * Enable Response Caching with default options
     * @param value enable or disable response caching
     * @return this config object
     */
    public Config cacheResponses(boolean value) {
        if(value){
            this.cache = new CacheManager();
        } else {
            this.cache = null;
        }
        return this;
    }

    /**
     * Enable Response Caching with custom options
     * @param value enable or disable response caching
     * @return this config object
     */
    public Config cacheResponses(Cache.Builder value) {
        this.cache = value.build();
        return this;
    }

    /**
     * Set the default encoding that will be used for serialization into Strings.
     * The default-default is UTF-8
     *
     * @param value a bool is its true or not.
     * @return this config object
     */
    public Config setDefaultResponseEncoding(String value) {
        Objects.requireNonNull(value, "Encoding cannot be null");
        this.defaultResponseEncoding = value;
        return this;
    }

    /**
     * Sets the jdk.httpclient.keepalive.timeout setting
     *      https://docs.oracle.com/en/java/javase/20/docs/api/java.net.http/module-summary.html
     * The number of seconds to keep idle HTTP connections alive in the keep alive cache.
     * This property applies to both HTTP/1.1 and HTTP/2.
     *
     * @param duration of ttl.
     * @param unit the time unit of the ttl
     * @return this config object
     */
    public Config connectionTTL(long duration, TimeUnit unit) {
        Objects.requireNonNull(unit, "TimeUnit required");
        var ttl = unit.toSeconds(duration);
        if(ttl > -1){
            System.setProperty(JDK_HTTPCLIENT_KEEPALIVE_TIMEOUT, String.valueOf(ttl));
        }
        return this;
    }

    /**
     * Sets the jdk.httpclient.keepalive.timeout setting
     *      https://docs.oracle.com/en/java/javase/20/docs/api/java.net.http/module-summary.html
     * The number of seconds to keep idle HTTP connections alive in the keep alive cache.
     * This property applies to both HTTP/1.1 and HTTP/2.
     *
     * @param duration of ttl.
     * @return this config object
     */
    public Config connectionTTL(Duration duration){
        return connectionTTL(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Automatically retry synchronous requests on 429/529 responses with the Retry-After response header
     * Default is false
     *
     * @param value a bool is its true or not.
     * @return this config object
     */
    public Config retryAfter(boolean value) {
       return retryAfter(value, 10);
    }

    /**
     * Automatically retry synchronous requests on 429/529 responses with the Retry-After response header
     * Default is false
     *
     * @param value a bool is its true or not.
     * @param maxRetryAttempts max retry attempts
     * @return this config object
     */
    public Config retryAfter(boolean value, int maxRetryAttempts) {
        if(value) {
            this.retry = new RetryStrategy.Standard(maxRetryAttempts);
        } else {
            this.retry = null;
        }
        return this;
    }

    /**
     * Automatically retry synchronous requests on 429/529 responses with the Retry-After response header
     * Default is false
     *
     * @param strategy a RetryStrategy
     * @return this config object
     */
    public Config retryAfter(RetryStrategy strategy) {
        this.retry = strategy;
        return this;
    }

    /**
     * Requests a specific HTTP protocol version where possible.
     *
     * This is a direct proxy setter for the Java Http-Client that powers unirest.
     *
     * <p> If this method is not invoked prior to using, then newly built clients will prefer {@linkplain
     * HttpClient.Version#HTTP_2 HTTP/2}.
     *
     * <p> If set to {@linkplain HttpClient.Version#HTTP_2 HTTP/2}, then each request
     * will attempt to upgrade to HTTP/2. If the upgrade succeeds, then the
     * response to this request will use HTTP/2 and all subsequent requests
     * and responses to the same
     * <a href="https://tools.ietf.org/html/rfc6454#section-4">origin server</a>
     * will use HTTP/2. If the upgrade fails, then the response will be
     * handled using HTTP/1.1
     *
     * Constraints may also affect the selection of protocol version.
     * For example, if HTTP/2 is requested through a proxy, and if the implementation
     * does not support this mode, then HTTP/1.1 may be used
     *
     * @param value the requested HTTP protocol version
     * @return this config
     */
    public Config version(HttpClient.Version value) {
        Objects.requireNonNull(value);
        version = value;
        return this;
    }

    /**
     * set a default base url for all routes.
     * this is overridden if the url contains a valid base already
     * the url may contain path params
     *
     * for example. Setting a default path of 'http://somwhere'
     * and then calling Unirest with Unirest.get('/place')
     * will result in a path of 'https://somwehre/place'
     * @param value the base URL to use
     * @return  this config object
     */
    public Config defaultBaseUrl(String value) {
        this.defaultBaseUrl = value;
        return this;
    }

    /**
     * Return default headers that are added to every request
     *
     * @return Headers
     */
    public Headers getDefaultHeaders() {
        return headers;
    }

    /**
     * Does the config have currently running clients? Find out here.
     *
     * @return boolean
     */
    public boolean isRunning() {
        return client.isPresent();
    }

    /**
     * Shutdown the current config and re-init.
     *
     * @return this config
     */
    public Config reset() {
        reset(false);
        return this;
    }


    /**
     * Shut down the configuration and its clients.
     * The config can be re-initialized with its settings
     *
     * @param clearOptions should the current non-client settings be retained.
     */
    public void reset(boolean clearOptions) {
        client = Optional.empty();

        if (clearOptions) {
            setDefaults();
        }
    }

    /**
     * Return the current Client. One will be build if it does
     * not yet exist.
     *
     * @return A synchronous Client
     */
    public Client getClient() {
        if (!client.isPresent()) {
            buildClient();
        }
        return getFinalClient();
    }

    private Client getFinalClient(){
        if(cache == null){
            return client.get();
        } else {
            return cache.wrap(client.get());
        }
    }

    private synchronized void buildClient() {
        if (!client.isPresent()) {
            client = Optional.of(clientBuilder.apply(this));
        }
    }

    /**
     * @return if cookie management should be enabled.
     *         default: true
     */
    public boolean getEnabledCookieManagement() {
        return cookieManagement;
    }

    /**
     * @return if the clients should follow redirects
     *         default: true
     */
    public boolean getFollowRedirects() {
        return followRedirects;
    }

    /**
     * @return the connection timeout in milliseconds
     *         default: 10000
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @return the connection timeout in milliseconds
     *         default: null (infinite)
     */
    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * @return a security keystore if one has been provided
     */
    public KeyStore getKeystore() {
        return this.keystore;
    }

    /**
     * @return The password for the keystore if provided
     */
    public String getKeyStorePassword() {
        return this.keystorePassword.get();
    }

    /**
     * @return a configured object mapper
     * @throws UnirestException if none has been configured.
     */
    public ObjectMapper getObjectMapper() {
        ObjectMapper om = this.objectMapper.get();
        if(om == null){
            throw new UnirestConfigException("No Object Mapper Configured. Please config one with Unirest.config().setObjectMapper");
        }
        return om;
    }

    private void validateClientsNotRunning() {
        if (client.isPresent()) {
            throw new UnirestConfigException(
                    "Http Clients are already built in order to build a new config execute Unirest.config().reset() before changing settings. \n" +
                            "This should be done rarely."
            );
        }
    }

    /**
     * @return the configured proxy configuration
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * @return if the system will pick up system properties (default is false)
     */
    public boolean useSystemProperties() {
        return this.useSystemProperties;
    }

    /**
     * @return the default encoding (UTF-8 is the default default)
     */
    public String getDefaultResponseEncoding() {
        return defaultResponseEncoding;
    }

    /**
     * @return if request compression is on (default is true)
     */
    public boolean isRequestCompressionOn() {
        return requestCompressionOn;
    }

    /**
     * Will unirest verify the SSL?
     * You should only do this in non-prod environments.
     * Default is true
     * @return if unirest will verify the SSL
     */
    public boolean isVerifySsl() {
        return verifySsl;
    }

    /**
     * @return the configured Cookie Spec
     */
    public String getCookieSpec() {
        return cookieSpec;
    }

    /**
     * @return the currently configured UniMetric object
     */
    public UniMetric getMetric() {
        return metrics;
    }

    /**
     * @return the currently configured Interceptor
     */
    public Interceptor getUniInterceptor() {
        return interceptor;
    }

    /**
     * @return the SSL connection configuration
     */
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * @return the ciphers for the SSL connection configuration
     */
    public String[] getCiphers() {
        return ciphers;
    }

    /**
     * @return the protocols for the SSL connection configuration
     */
    public String[] getProtocols() {
        return protocols;
    }

    /**
     * @return the default base URL
     */
    public String getDefaultBaseUrl() {
        return this.defaultBaseUrl;
    }

    public Executor getCustomExecutor(){
        return customExecutor;
    }

    /**
     * @return the preferred http version
     */
    public HttpClient.Version getVersion() {
        return version;
    }
    /**
     * @return if unirest will retry requests on 429/529
     */
    public boolean isAutomaticRetryAfter(){
        return retry != null;
    }

    /**
     * @return the max number of times to attempt to do a 429/529 retry-after
     */
    public int maxRetries() {
        return retry.getMaxAttempts();
    }

    /**
     * @return the maximum life span of persistent connections regardless of their expiration setting.
     */
    public long getTTL() {
        try {
            return Long.parseLong(System.getProperty(JDK_HTTPCLIENT_KEEPALIVE_TIMEOUT));
        }catch (NumberFormatException e){
            return -1;
        }
    }

    public RetryStrategy getRetryStrategy() {
        return retry;
    }
}
