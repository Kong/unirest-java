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
import java.net.Authenticator;
import java.net.ProxySelector;
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

/**
 * Central configuration class for Unirest HTTP client settings.
 * <p>
 * This class provides a fluent API for configuring all aspects of HTTP client behavior,
 * including timeouts, proxies, SSL/TLS settings, headers, cookies, interceptors, and more.
 * Configuration can be applied to the primary static {@link Unirest} instance or to
 * individual {@link UnirestInstance} objects.
 * </p>
 *
 * <h2>Basic Usage:</h2>
 * <pre>{@code
 * // Configure the primary instance
 * Unirest.config()
 *     .connectTimeout(5000)
 *     .defaultBaseUrl("https://api.example.com")
 *     .setDefaultHeader("Accept", "application/json");
 *
 * // Or configure a custom instance
 * UnirestInstance instance = new UnirestInstance(new Config());
 * instance.config()
 *     .proxy("proxy.example.com", 8080)
 *     .verifySsl(false);
 * }</pre>
 *
 * <h2>Configuration Categories:</h2>
 * <ul>
 *   <li><b>Timeouts:</b> {@link #connectTimeout(int)}, {@link #requestTimeout(Integer)}, {@link #connectionTTL(long, TimeUnit)}</li>
 *   <li><b>Proxy:</b> {@link #proxy(String, int)}, {@link #proxy(Proxy)}, {@link #proxy(ProxySelector)}</li>
 *   <li><b>SSL/TLS:</b> {@link #verifySsl(boolean)}, {@link #sslContext(SSLContext)}, {@link #clientCertificateStore(KeyStore, String)}</li>
 *   <li><b>Headers/Cookies:</b> {@link #setDefaultHeader(String, String)}, {@link #addDefaultCookie(String, String)}</li>
 *   <li><b>Serialization:</b> {@link #setObjectMapper(ObjectMapper)}</li>
 *   <li><b>Behavior:</b> {@link #followRedirects(boolean)}, {@link #enableCookieManagement(boolean)}, {@link #retryAfter(boolean)}</li>
 *   <li><b>Caching:</b> {@link #cacheResponses(boolean)}, {@link #cacheResponses(Cache.Builder)}</li>
 *   <li><b>Interceptors:</b> {@link #interceptor(Interceptor)}, {@link #instrumentWith(UniMetric)}</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * Configuration should be performed before making requests. Once HTTP clients are built
 * (after the first request), certain settings cannot be changed without calling {@link #reset()}.
 * Methods that require this will throw {@link UnirestConfigException} if clients are already running.
 * </p>
 *
 * @see Unirest
 * @see UnirestInstance
 * @see Client
 * @see Interceptor
 */
public class Config {
    /**
     * Default connection timeout in milliseconds (10 seconds).
     * Used when no custom timeout is configured via {@link #connectTimeout(int)}.
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;

    /**
     * System property name for configuring HTTP keep-alive timeout.
     * <p>
     * This property controls the number of seconds to keep idle HTTP connections
     * alive in the keep-alive cache. Applies to both HTTP/1.1 and HTTP/2.
     * </p>
     *
     * @see #connectionTTL(long, TimeUnit)
     * @see <a href="https://docs.oracle.com/en/java/javase/20/docs/api/java.net.http/module-summary.html">Java HTTP Client Module</a>
     */
    public static final String JDK_HTTPCLIENT_KEEPALIVE_TIMEOUT = "jdk.httpclient.keepalive.timeout";

    /**
     * System property name for disabling hostname verification.
     * <p>
     * When set to "true", disables SSL hostname verification for the entire JVM.
     * <b>Warning:</b> This affects all HTTP clients in the JVM, not just Unirest.
     * Should only be used in non-production environments.
     * </p>
     *
     * @see #disableHostNameVerification(boolean)
     */
    public static final String JDK_HTTPCLIENT_DISABLE_HOST_NAME_VERIFICATION = "jdk.internal.httpclient.disableHostnameVerification";

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
    private Authenticator authenticator;
    private ProxySelector proxySelector;

    /**
     * Creates a new Config instance with default settings.
     */
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
        authenticator = null;
        proxySelector = null;

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
     * This will set any ProxySelector object already set to null and take over all proxy work
     *
     * @param value Proxy settings object.
     * @return this config object
     */
    public Config proxy(Proxy value) {
        validateClientsNotRunning();
        this.proxySelector = null;
        this.proxy = value;
        return this;
    }

    /**
     * Set a proxy selector.
     * This will set any Proxy object already set to null and take over all proxy work
     *
     * @param value ProxySelector object.
     * @return this config object
     */
    public Config proxy(ProxySelector value) {
        validateClientsNotRunning();
        this.proxy = null;
        this.proxySelector = value;
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
     * Removes all default headers from this configuration.
     *
     * @return this config object for method chaining
     * @see #setDefaultHeader(String, String)
     * @see #addDefaultHeader(String, String)
     */
    public Config clearDefaultHeaders() {
        headers.clear();
        return this;
    }

    /**
     * Sets default Basic Authentication credentials for all requests.
     * <p>
     * This adds an {@code Authorization} header with Base64-encoded credentials
     * to all requests made with this configuration.
     * </p>
     *
     * @param username the username for Basic Authentication
     * @param password the password for Basic Authentication
     * @return this config object for method chaining
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
     * Configures whether the client should automatically follow redirects.
     * <p>
     * When enabled, the client will automatically follow HTTP redirects (3xx responses).
     * </p>
     *
     * @param enable {@code true} to follow redirects (default), {@code false} to disable
     * @return this config object for method chaining
     * @throws UnirestConfigException if called after the client has been built
     * @see #getFollowRedirects()
     */
    public Config followRedirects(boolean enable) {
        validateClientsNotRunning();
        this.followRedirects = enable;
        return this;
    }

    /**
     * Configures whether the client should manage cookies automatically.
     * <p>
     * When enabled, the client will store cookies from responses and send them
     * with subsequent requests to the same domain.
     * </p>
     *
     * @param enable {@code true} to enable cookie management (default), {@code false} to disable
     * @return this config object for method chaining
     * @throws UnirestConfigException if called after the client has been built
     * @see #getEnabledCookieManagement()
     */
    public Config enableCookieManagement(boolean enable) {
        validateClientsNotRunning();
        this.cookieManagement = enable;
        return this;
    }

    /**
     * Configures whether SSL/TLS certificates should be verified.
     * <p>
     * <b>Warning:</b> Disabling SSL verification is a security risk and should
     * only be done in development or testing environments, never in production.
     * </p>
     *
     * @param value {@code true} to verify SSL certificates (default), {@code false} to skip verification
     * @return this config object for method chaining
     * @see #isVerifySsl()
     */
    public Config verifySsl(boolean value) {
        this.verifySsl = value;
        return this;
    }

    /**
     * Configures whether the client should use system properties for configuration.
     * <p>
     * When enabled, the client will read proxy settings and other configuration
     * from system properties.
     * </p>
     *
     * @param value {@code true} to use system properties, {@code false} to ignore them (default)
     * @return this config object for method chaining
     * @see #useSystemProperties()
     */
    public Config useSystemProperties(boolean value) {
        this.useSystemProperties = value;
        return this;
    }

    /**
     * Configures whether the client should request compressed responses.
     * <p>
     * When enabled, the client sends an {@code Accept-Encoding: gzip} header
     * and automatically decompresses GZIP-encoded responses.
     * </p>
     *
     * @param value {@code true} to request compression (default), {@code false} to disable
     * @return this config object for method chaining
     * @see #isRequestCompressionOn()
     */
    public Config requestCompression(boolean value) {
        this.requestCompressionOn = value;
        return this;
    }

    /**
     * Sets the cookie specification policy for cookie handling.
     * <p>
     * Acceptable values:
     * <ul>
     *   <li>{@code "default"} - same as Netscape</li>
     *   <li>{@code "netscape"} - original Netscape cookie spec</li>
     *   <li>{@code "ignoreCookies"} - ignore all cookies</li>
     *   <li>{@code "standard"} - RFC 6265 interoperability profile</li>
     *   <li>{@code "standard-strict"} - RFC 6265 strict profile</li>
     * </ul>
     *
     * @param policy the cookie policy name
     * @return this config object for method chaining
     * @see #getCookieSpec()
     */
    public Config cookieSpec(String policy) {
        this.cookieSpec = policy;
        return this;
    }

    /**
     * Enables or disables response caching with default options.
     * <p>
     * When enabled, HTTP responses will be cached based on standard HTTP caching headers.
     * </p>
     *
     * @param value {@code true} to enable caching, {@code false} to disable
     * @return this config object for method chaining
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
     * Enables response caching with custom cache configuration.
     *
     * @param value the cache builder with custom configuration
     * @return this config object for method chaining
     * @see Cache.Builder
     */
    public Config cacheResponses(Cache.Builder value) {
        this.cache = value.build();
        return this;
    }

    /**
     * Sets the default character encoding for response body serialization.
     *
     * @param value the encoding name (e.g., "UTF-8", "ISO-8859-1")
     * @return this config object for method chaining
     * @throws NullPointerException if value is null
     * @see #getDefaultResponseEncoding()
     */
    public Config setDefaultResponseEncoding(String value) {
        Objects.requireNonNull(value, "Encoding cannot be null");
        this.defaultResponseEncoding = value;
        return this;
    }

    /**
     * Sets the HTTP connection keep-alive timeout.
     * <p>
     * This configures the {@code jdk.httpclient.keepalive.timeout} system property,
     * which controls how long idle HTTP connections are kept alive in the connection pool.
     * Applies to both HTTP/1.1 and HTTP/2.
     * </p>
     *
     * @param duration the keep-alive duration
     * @param unit the time unit of the duration
     * @return this config object for method chaining
     * @throws NullPointerException if unit is null
     * @see #getTTL()
     * @see <a href="https://docs.oracle.com/en/java/javase/20/docs/api/java.net.http/module-summary.html">Java HTTP Client Module</a>
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
     * Sets the HTTP connection keep-alive timeout.
     * <p>
     * This configures the {@code jdk.httpclient.keepalive.timeout} system property,
     * which controls how long idle HTTP connections are kept alive in the connection pool.
     * Applies to both HTTP/1.1 and HTTP/2.
     * </p>
     *
     * @param duration the keep-alive duration
     * @return this config object for method chaining
     * @see #getTTL()
     * @see <a href="https://docs.oracle.com/en/java/javase/20/docs/api/java.net.http/module-summary.html">Java HTTP Client Module</a>
     */
    public Config connectionTTL(Duration duration){
        return connectionTTL(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Enables automatic retry for requests that receive 429 (Too Many Requests) or
     * 529 (Site Overloaded) responses with a {@code Retry-After} header.
     * <p>
     * When enabled, the client will wait for the duration specified in the
     * {@code Retry-After} header before automatically retrying the request,
     * up to a maximum of 10 retry attempts.
     * </p>
     *
     * @param value {@code true} to enable automatic retry, {@code false} to disable (default)
     * @return this config object for method chaining
     * @see #retryAfter(boolean, int)
     * @see #isAutomaticRetryAfter()
     */
    public Config retryAfter(boolean value) {
       return retryAfter(value, 10);
    }

    /**
     * Enables automatic retry for requests that receive 429 (Too Many Requests) or
     * 529 (Site Overloaded) responses with a {@code Retry-After} header.
     * <p>
     * When enabled, the client will wait for the duration specified in the
     * {@code Retry-After} header before automatically retrying the request.
     * </p>
     *
     * @param value {@code true} to enable automatic retry, {@code false} to disable (default)
     * @param maxRetryAttempts the maximum number of retry attempts
     * @return this config object for method chaining
     * @see #maxRetries()
     * @see #isAutomaticRetryAfter()
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
     * Configures automatic retry using a custom retry strategy.
     * <p>
     * This allows for custom logic to determine when and how to retry requests.
     * </p>
     *
     * @param strategy the custom {@link RetryStrategy} to use, or {@code null} to disable retry
     * @return this config object for method chaining
     * @see #getRetryStrategy()
     * @see #isAutomaticRetryAfter()
     */
    public Config retryAfter(RetryStrategy strategy) {
        this.retry = strategy;
        return this;
    }

    /**
     * Requests a specific HTTP protocol version where possible.
     * <p>
     * If this method is not invoked, newly built clients will prefer
     * {@link HttpClient.Version#HTTP_2 HTTP/2}.
     * </p>
     * <p>
     * If set to {@link HttpClient.Version#HTTP_2 HTTP/2}, each request will attempt
     * to upgrade to HTTP/2. If the upgrade succeeds, the response and all subsequent
     * requests/responses to the same origin server will use HTTP/2. If the upgrade
     * fails, HTTP/1.1 will be used.
     * </p>
     * <p>
     * Constraints may affect protocol version selection. For example, if HTTP/2 is
     * requested through a proxy and the implementation does not support this mode,
     * HTTP/1.1 may be used instead.
     * </p>
     *
     * @param value the requested HTTP protocol version
     * @return this config object for method chaining
     * @throws NullPointerException if value is null
     * @see #getVersion()
     */
    public Config version(HttpClient.Version value) {
        Objects.requireNonNull(value);
        version = value;
        return this;
    }

    /**
     * Sets a default base URL to be prepended to all relative request paths.
     * <p>
     * This is useful when making multiple requests to the same server. The base URL
     * is only used if the request path does not already contain a valid base URL.
     * The URL may contain path parameters.
     * </p>
     *
     * <h4>Example:</h4>
     * <pre>{@code
     * Unirest.config().defaultBaseUrl("http://api.example.com");
     * Unirest.get("/users");  // Results in: http://api.example.com/users
     * }</pre>
     *
     * @param value the base URL to use for relative paths
     * @return this config object for method chaining
     * @see #getDefaultBaseUrl()
     */
    public Config defaultBaseUrl(String value) {
        this.defaultBaseUrl = value;
        return this;
    }

    /**
     * Returns the default headers that are added to every request.
     *
     * @return the default {@link Headers} collection
     */
    public Headers getDefaultHeaders() {
        return headers;
    }

    /**
     * Checks if the HTTP client has been built and is currently running.
     * <p>
     * Once a client is running, certain configuration options cannot be changed
     * without first calling {@link #reset()}.
     * </p>
     *
     * @return {@code true} if the client has been built and is running, {@code false} otherwise
     */
    public boolean isRunning() {
        return client.isPresent();
    }

    /**
     * Shuts down the current HTTP client and prepares for re-initialization.
     * <p>
     * This method preserves all configuration settings but releases the HTTP client,
     * allowing configuration changes that require a client restart.
     * </p>
     *
     * @return this config object for method chaining
     * @see #reset(boolean)
     */
    public Config reset() {
        reset(false);
        return this;
    }


    /**
     * Shuts down the HTTP client and optionally resets all configuration to defaults.
     * <p>
     * When {@code clearOptions} is {@code false}, all configuration settings are preserved
     * but the HTTP client is released. When {@code true}, all settings are reset to their
     * default values.
     * </p>
     *
     * @param clearOptions if {@code true}, reset all settings to defaults; if {@code false}, preserve current settings
     */
    public void reset(boolean clearOptions) {
        client = Optional.empty();

        if (clearOptions) {
            setDefaults();
        }
    }

    /**
     * Returns the current HTTP client, building one if it does not yet exist.
     * <p>
     * If response caching is enabled, the returned client will be wrapped
     * with caching functionality.
     * </p>
     *
     * @return the configured {@link Client} instance
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
     * Returns whether cookie management is enabled.
     *
     * @return {@code true} if cookie management is enabled (default), {@code false} otherwise
     * @see #enableCookieManagement(boolean)
     */
    public boolean getEnabledCookieManagement() {
        return cookieManagement;
    }

    /**
     * Returns whether automatic redirect following is enabled.
     *
     * @return {@code true} if redirects are followed automatically (default), {@code false} otherwise
     * @see #followRedirects(boolean)
     */
    public boolean getFollowRedirects() {
        return followRedirects;
    }

    /**
     * Returns the connection timeout in milliseconds.
     *
     * @return the connection timeout in milliseconds (default: {@value #DEFAULT_CONNECT_TIMEOUT})
     * @see #connectTimeout(int)
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Returns the request timeout in milliseconds.
     *
     * @return the request timeout in milliseconds, or {@code null} for infinite timeout (default)
     * @see #requestTimeout(Integer)
     */
    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Returns the configured SSL keystore for client certificate authentication.
     *
     * @return the configured {@link KeyStore}, or {@code null} if not configured
     * @see #clientCertificateStore(KeyStore, String)
     * @see #clientCertificateStore(String, String)
     */
    public KeyStore getKeystore() {
        return this.keystore;
    }

    /**
     * Returns the password for the configured keystore.
     *
     * @return the keystore password, or {@code null} if not configured
     * @see #clientCertificateStore(KeyStore, String)
     */
    public String getKeyStorePassword() {
        return this.keystorePassword.get();
    }

    /**
     * Returns the configured ObjectMapper for JSON serialization/deserialization.
     *
     * @return the configured {@link ObjectMapper}
     * @throws UnirestConfigException if no ObjectMapper has been configured
     * @see #setObjectMapper(ObjectMapper)
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
     * Returns the configured proxy settings.
     *
     * @return the configured {@link Proxy}, or {@code null} if no proxy is configured
     * @see #proxy(Proxy)
     * @see #proxy(String, int)
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Returns whether the client uses system properties for configuration.
     *
     * @return {@code true} if system properties are used, {@code false} otherwise (default)
     * @see #useSystemProperties(boolean)
     */
    public boolean useSystemProperties() {
        return this.useSystemProperties;
    }

    /**
     * Returns the default character encoding for response body serialization.
     *
     * @return the default encoding name (default: UTF-8)
     * @see #setDefaultResponseEncoding(String)
     */
    public String getDefaultResponseEncoding() {
        return defaultResponseEncoding;
    }

    /**
     * Returns whether request compression (GZIP) is enabled.
     *
     * @return {@code true} if compression is enabled (default), {@code false} otherwise
     * @see #requestCompression(boolean)
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
     * Returns the configured cookie specification policy.
     *
     * @return the cookie spec name, or {@code null} if using default behavior
     * @see #cookieSpec(String)
     */
    public String getCookieSpec() {
        return cookieSpec;
    }

    /**
     * Returns the configured metrics collector for instrumentation.
     *
     * @return the configured {@link UniMetric} instance
     * @see #instrumentWith(UniMetric)
     */
    public UniMetric getMetric() {
        return metrics;
    }

    /**
     * Returns the configured request/response interceptor.
     *
     * @return the configured {@link Interceptor} instance
     * @see #interceptor(Interceptor)
     */
    public Interceptor getUniInterceptor() {
        return interceptor;
    }

    /**
     * Returns the configured SSL context for secure connections.
     *
     * @return the configured {@link SSLContext}, or {@code null} if not configured
     * @see #sslContext(SSLContext)
     */
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * Returns the configured SSL/TLS cipher suites.
     *
     * @return the array of cipher suite names, or {@code null} if using defaults
     * @see #ciphers(String...)
     */
    public String[] getCiphers() {
        return ciphers;
    }

    /**
     * Returns the configured SSL/TLS protocols.
     *
     * @return the array of protocol names, or {@code null} if using defaults
     * @see #protocols(String...)
     */
    public String[] getProtocols() {
        return protocols;
    }

    /**
     * Returns the default base URL prepended to relative request paths.
     *
     * @return the default base URL, or {@code null} if not configured
     * @see #defaultBaseUrl(String)
     */
    public String getDefaultBaseUrl() {
        return this.defaultBaseUrl;
    }

    /**
     * Returns the custom executor used for asynchronous requests.
     *
     * @return the custom {@link Executor}, or {@code null} if using the default
     * @see #executor(Executor)
     */
    public Executor getCustomExecutor(){
        return customExecutor;
    }

    /**
     * Returns the preferred HTTP protocol version.
     *
     * @return the configured {@link HttpClient.Version} (default: HTTP/2)
     * @see #version(HttpClient.Version)
     */
    public HttpClient.Version getVersion() {
        return version;
    }

    /**
     * Returns whether automatic retry on 429/529 responses is enabled.
     *
     * @return {@code true} if automatic retry is enabled, {@code false} otherwise (default)
     * @see #retryAfter(boolean)
     * @see #retryAfter(RetryStrategy)
     */
    public boolean isAutomaticRetryAfter(){
        return retry != null;
    }

    /**
     * Returns the maximum number of retry attempts for 429/529 responses.
     *
     * @return the maximum number of retry attempts
     * @throws NullPointerException if no retry strategy is configured
     * @see #retryAfter(boolean, int)
     */
    public int maxRetries() {
        return retry.getMaxAttempts();
    }

    /**
     * Returns the connection keep-alive timeout (TTL) in seconds.
     * <p>
     * This value is read from the system property {@value #JDK_HTTPCLIENT_KEEPALIVE_TIMEOUT}.
     * </p>
     *
     * @return the TTL in seconds, or {@code -1} if not configured or invalid
     * @see #connectionTTL(long, TimeUnit)
     */
    public long getTTL() {
        try {
            return Long.parseLong(System.getProperty(JDK_HTTPCLIENT_KEEPALIVE_TIMEOUT));
        }catch (NumberFormatException e){
            return -1;
        }
    }

    /**
     * Returns the configured retry strategy for handling 429/529 responses.
     *
     * @return the configured {@link RetryStrategy}, or {@code null} if not configured
     * @see #retryAfter(RetryStrategy)
     */
    public RetryStrategy getRetryStrategy() {
        return retry;
    }

    /**
     * Sets the system property jdk.internal.httpclient.disableHostnameVerification
     *
     * Disables or enables HostNameVerification for the ENTIRE JVM. This will impact all consumers of
     * Unirest, java.net.http.HttpClient, or other consumers of either.
     *
     * @param enabled boolean value for property. true DISABLES host name verification
     * @return this config object
     */
    public Config disableHostNameVerification(boolean enabled) {
        System.setProperty(JDK_HTTPCLIENT_DISABLE_HOST_NAME_VERIFICATION, String.valueOf(enabled));
        return this;
    }

    /**
     * Sets an authenticator for handling authentication challenges.
     * <p>
     * The authenticator is used by the underlying HTTP client to respond to
     * authentication challenges from servers or proxies.
     * </p>
     *
     * @param auth the {@link Authenticator} to use for authentication challenges
     * @return this config object for method chaining
     * @see #getAuthenticator()
     */
    public Config authenticator(Authenticator auth) {
        this.authenticator = auth;
        return this;
    }

    /**
     * Returns the configured authenticator for handling authentication challenges.
     *
     * @return the configured {@link Authenticator}, or {@code null} if not configured
     * @see #authenticator(Authenticator)
     */
    public Authenticator getAuthenticator(){
        return authenticator;
    }

    /**
     * Returns the configured proxy selector for dynamic proxy selection.
     *
     * @return the configured {@link ProxySelector}, or {@code null} if not configured
     * @see #proxy(ProxySelector)
     */
    public ProxySelector getProxySelector(){
        return proxySelector;
    }
}
