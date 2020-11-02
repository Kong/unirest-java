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

import kong.unirest.apache.ApacheAsyncClient;
import kong.unirest.apache.ApacheClient;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.nio.client.HttpAsyncClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    public static final int DEFAULT_MAX_CONNECTIONS = 200;
    public static final int DEFAULT_MAX_PER_ROUTE = 20;
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 60000;

    private Optional<Client> client = Optional.empty();
    private Optional<AsyncClient> asyncClient = Optional.empty();
    private Optional<ObjectMapper> objectMapper = Optional.of(new JsonObjectMapper());

    private List<HttpRequestInterceptor> apacheinterceptors = new ArrayList<>();
    private Headers headers;
    private Proxy proxy;
    private int connectionTimeout;
    private int socketTimeout;
    private int maxTotal;
    private int maxPerRoute;
    private boolean followRedirects;
    private boolean cookieManagement;
    private boolean useSystemProperties;
    private String defaultResponseEncoding = StandardCharsets.UTF_8.name();
    private Function<Config, AsyncClient> asyncBuilder;
    private Function<Config, Client> clientBuilder;
    private boolean requestCompressionOn = true;
    private boolean automaticRetries;
    private boolean verifySsl = true;
    private boolean addShutdownHook = false;
    private KeyStore keystore;
    private Supplier<String> keystorePassword = () -> null;
    private String cookieSpec;
    private UniMetric metrics = new NoopMetric();
    private long ttl = -1;
    private SSLContext sslContext;
    private String[] ciphers;
    private String[] protocols;
    private CompoundInterceptor interceptor = new CompoundInterceptor();
    private HostnameVerifier hostnameVerifier;
    private String defaultBaseUrl;
    private CacheManager cache;

    public Config() {
        setDefaults();
    }

    private void setDefaults() {
        apacheinterceptors.clear();
        proxy = null;
        cache = null;
        headers = new Headers();
        connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        socketTimeout = DEFAULT_SOCKET_TIMEOUT;
        maxTotal = DEFAULT_MAX_CONNECTIONS;
        maxPerRoute = DEFAULT_MAX_PER_ROUTE;
        followRedirects = true;
        cookieManagement = true;
        requestCompressionOn = true;
        automaticRetries = true;
        verifySsl = true;
        keystore = null;
        keystorePassword = null;
        sslContext = null;
        ciphers = null;
        protocols = null;
        interceptor = new CompoundInterceptor();

        this.objectMapper = Optional.of(new JsonObjectMapper());
        try {
            asyncBuilder = ApacheAsyncClient::new;
            clientBuilder = ApacheClient::new;
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
    @Deprecated // use httpClient(Function<Config, Client> httpClient) with the ApacheConfig.builder()
    public Config httpClient(HttpClient httpClient) {
        client = Optional.of(new ApacheClient(httpClient, this, null));
        return this;
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
     * Set the asynchronous AbstractHttpAsyncClient implementation to use for every asynchronous request
     *
     * @param value Custom CloseableHttpAsyncClient implementation
     * @return this config object
     * @deprecated use asyncClient(AsyncClient value)
     */
    @Deprecated
    public Config asyncClient(HttpAsyncClient value) {
        this.asyncClient = Optional.of(new ApacheAsyncClient(value, this, null, null));
        return this;
    }

    /**
     * Set the full async configuration including monitors. These will be shutDown on a Unirest.shudown()
     *
     * @param value Custom AsyncConfig class. The actual AsyncHttpClient is required.
     * @return this config object
     */
    public Config asyncClient(AsyncClient value) {
        asyncClient = Optional.ofNullable(value);
        return this;
    }

    /**
     * Set the full async configuration including monitors. These will be shutDown on a Unirest.shudown()
     *
     * @param asyncClientBuilder A builder function for creating a AsyncClient
     * @return this config object
     */
    public Config asyncClient(Function<Config, AsyncClient> asyncClientBuilder) {
        this.asyncBuilder = asyncClientBuilder;
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
        this.objectMapper = Optional.ofNullable(om);
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
     * Set a custom HostnameVerifier
     * @param value the verifier
     * @return this config object
     */
    public Config hostnameVerifier(HostnameVerifier value) {
        this.hostnameVerifier = value;
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
     * Set the connection timeout
     *
     * @param inMillies The timeout until a connection with the server is established (in milliseconds). Default is 10000. Set to zero to disable the timeout.
     * @return this config object
     */
    public Config connectTimeout(int inMillies) {
        validateClientsNotRunning();
        this.connectionTimeout = inMillies;
        return this;
    }

    /**
     * Set the socket timeout
     *
     * @param inMillies The timeout to receive data (in milliseconds). Default is 60000. Set to zero to disable the timeout.
     * @return this config object
     */
    public Config socketTimeout(int inMillies) {
        validateClientsNotRunning();
        this.socketTimeout = inMillies;
        return this;
    }

    /**
     * Set the concurrency levels
     *
     * @param total    Defines the overall connection limit for a connection pool. Default is 200.
     * @param perRoute Defines a connection limit per one HTTP route (this can be considered a per target host limit). Default is 20.
     * @return this config object
     */
    public Config concurrency(int total, int perRoute) {
        validateClientsNotRunning();
        this.maxTotal = total;
        this.maxPerRoute = perRoute;
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
     * Sets a global error handler by wrapping it in a default interceptor
     * If the response was NOT a 200-series response or a mapping exception happened. Invoke this consumer,
     * this function is deprecated in favor of a full interceptor pattern.
     *
     * Setting a custom interceptor will make this function throw an exception
     * @param consumer a function to consume a HttpResponse
     * @return this config object
     * @deprecated this is merging with the interceptor concept. see interceptor(Interceptor value)
     */
    @Deprecated
    public Config errorHandler(Consumer<HttpResponse<?>> consumer) {
        Optional<DefaultInterceptor> df = getDefaultInterceptor();
        df.ifPresent(d -> d.setConsumer(consumer));
        df.orElseThrow(() -> new UnirestConfigException(
                "You attempted to set a custom error handler while also overriding the Unirest interceptor.\n" +
                "please use the interceptor only. This function is deprecated"));
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
     * Add a HttpRequestInterceptor to the clients. This can be called multiple times to add as many as you like.
     * https://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpRequestInterceptor.html
     *
     * @param value The addInterceptor
     * @return this config object
     * @deprecated use the Unirest Interceptors rather than Apache
     */
    @Deprecated
    public Config addInterceptor(HttpRequestInterceptor value) {
        validateClientsNotRunning();
        apacheinterceptors.add(value);
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
     * Automaticly retry certain recoverable errors like socket timeouts. Up to 4 times
     * Note that currently this only works on synchronous calls.
     * Default is true
     *
     * @param value a bool is its true or not.
     * @return this config object
     */
    public Config automaticRetries(boolean value) {
        automaticRetries = value;
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
     */
    public void cacheResponses(boolean value) {
        if(value){
            this.cache = new CacheManager();
        } else {
            this.cache = null;
        }
    }

    /**
     * Enable Response Caching with custom options
     * @param value enable or disable response caching
     */
    public void cacheResponses(Cache.Builder value) {
        this.cache = value.build();
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
     * Total time to live (TTL)  defines maximum life span of persistent connections regardless of their expiration setting.
     * No persistent connection will be re-used past its TTL value.
     *
     * @param duration of ttl.
     * @param unit the time unit of the ttl
     * @return this config object
     */
    public Config connectionTTL(long duration, TimeUnit unit) {
        this.ttl = unit.toMillis(duration);
        return this;
    }


    /**
     * Sugar!
     * Total time to live (TTL)  defines maximum life span of persistent connections regardless of their expiration setting.
     * No persistent connection will be re-used past its TTL value.
     *
     * @param duration of ttl.
     * @return this config object
     */
    public Config connectionTTL(Duration duration){
        this.ttl = duration.toMillis();
        return this;
    }

    /**
     * Register the client with a system shutdown hook. Note that this creates up to two threads
     * (depending on if you use both sync and async clients). default is false
     *
     * @param value a bool is its true or not.
     * @return this config object
     */
    public Config addShutdownHook(boolean value) {
        this.addShutdownHook = value;
        if (value) {
            client.ifPresent(Client::registerShutdownHook);
            asyncClient.ifPresent(AsyncClient::registerShutdownHook);
        }
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
        return client.isPresent() || asyncClient.isPresent();
    }

    /**
     * Shutdown the current config and re-init.
     *
     * @return this config
     */
    public Config reset() {
        shutDown(false);
        return this;
    }


    /**
     * Shut down the configuration and its clients.
     * The config can be re-initialized with its settings
     *
     * @param clearOptions should the current non-client settings be retained.
     */
    public void shutDown(boolean clearOptions) {
        List<Exception> ex = Stream.concat(
                client.map(Client::close).orElseGet(Stream::empty),
                asyncClient.map(AsyncClient::close).orElseGet(Stream::empty)
        ).collect(Collectors.toList());

        client = Optional.empty();
        asyncClient = Optional.empty();

        if (clearOptions) {
            setDefaults();
        }

        if (!ex.isEmpty()) {
            throw new UnirestException(ex);
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
     * Return the current HttpAsyncClient. One will be build if it does
     * not yet exist.
     *
     * @return Apache HttpAsyncClient
     */
    public AsyncClient getAsyncClient() {
        if (!asyncClientIsReady()) {
            buildAsyncClient();
        }
        return getFinalAsyncClient();
    }

    private AsyncClient getFinalAsyncClient(){
        if(cache == null){
            return asyncClient.get();
        }
        return cache.wrapAsync(asyncClient.get());
    }

    private boolean asyncClientIsReady() {
        return asyncClient
                .map(AsyncClient::isRunning)
                .orElse(false);
    }

    private synchronized void buildAsyncClient() {
        if (!asyncClientIsReady()) {
            AsyncClient value = asyncBuilder.apply(this);
            verifyIsOn(value);
            asyncClient = Optional.of(value);
        }
    }

    private void verifyIsOn(AsyncClient value) {
        if (!value.isRunning()) {
            throw new UnirestConfigException("Attempted to get a new async client but it was not started. Please ensure it is");
        }
    }

    // Accessors for unirest.

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
     * @return the maximum number of connections the clients for this config will manage at once
     *         default: 200
     */
    public int getMaxConnections() {
        return maxTotal;
    }

    /**
     * @return the maximum number of connections per route the clients for this config will manage at once
     *         default: 20
     */
    public int getMaxPerRoutes() {
        return maxPerRoute;
    }

    /**
     * @return the connection timeout in milliseconds
     *         default: 10000
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @return socket timeout in milliseconds
     *         default: 60000
     */
    public int getSocketTimeout() {
        return socketTimeout;
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
        return objectMapper.orElseThrow(() -> new UnirestException("No Object Mapper Configured. Please config one with Unirest.config().setObjectMapper"));
    }

    private void validateClientsNotRunning() {
        if (client.isPresent() || asyncClient.isPresent()) {
            throw new UnirestConfigException(
                    "Http Clients are already built in order to build a new config execute Unirest.config().reset() before changing settings. \n" +
                            "This should be done rarely."
            );
        }
    }

    /**
     * @return currently configured Apache HttpRequestInterceptors
     * @deprecated use Unirest Interceptors instead
     */
    @Deprecated
    public List<HttpRequestInterceptor> getInterceptor() {
        return apacheinterceptors;
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
     * @return if automatic retries are on (default is false)
     */
    public boolean isAutomaticRetries() {
        return automaticRetries;
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
     * @return if shutdown hooks configure automatically (default is false)
     */
    public boolean shouldAddShutdownHook() {
        return addShutdownHook;
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
     * @return the maximum life span of persistent connections regardless of their expiration setting.
     */
    public long getTTL() {
        return ttl;
    }

    /**
     * @return the currently configured Interceptor
     */
    public Interceptor getUniInterceptor() {
        return interceptor;
    }

    /**
     * @return  The currently configred error handler
     * @deprecated use interceptors instead
     */
    @Deprecated
    public Consumer<HttpResponse<?>> getErrorHandler() {
        return getDefaultInterceptor()
                .map(DefaultInterceptor::getConsumer)
                .orElseGet(() -> r -> {});
    }

    /**
     * @return the SSL connection configuration
     */
    public SSLContext getSslContext() {
        return sslContext;
    }

    private Optional<DefaultInterceptor> getDefaultInterceptor() {
        return interceptor.getInterceptors().stream()
                .filter(i -> i instanceof DefaultInterceptor)
                .map(i -> (DefaultInterceptor)i)
                .findFirst();
    }

    /**
     * @return the current HostnameVerifier
     */
    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
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
}
