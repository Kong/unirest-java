/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.client.HttpAsyncClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
    private Optional<ObjectMapper> objectMapper = Optional.empty();

    private List<HttpRequestInterceptor> interceptors = new ArrayList<>();
    private Headers defaultHeaders;
    private HttpHost proxy;
    private CredentialsProvider proxyCreds;
    private int connectionTimeout;
    private int socketTimeout;
    private int maxTotal;
    private int maxPerRoute;
    private boolean followRedirects;
    private boolean cookieManagement;
    private boolean useSystemProperties;
    private Function<Config, AsyncClient> asyncBuilder = ApacheAsyncClient::new;
    private Function<Config, Client> clientBuilder = ApacheClient::new;

    public Config() {
        setDefaults();
    }

    private void setDefaults(){
        interceptors.clear();
        proxy = null;
        defaultHeaders = new Headers();
        connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        socketTimeout = DEFAULT_SOCKET_TIMEOUT;
        maxTotal = DEFAULT_MAX_CONNECTIONS;
        maxPerRoute = DEFAULT_MAX_PER_ROUTE;
        followRedirects = true;
        cookieManagement = true;
    }

    /**
     * Set the HttpClient implementation to use for every synchronous request
     *
     * @param httpClient Custom httpClient implementation
     * @return this config object
     */
    public Config httpClient(HttpClient httpClient) {
        client = Optional.of(new ApacheClient(httpClient));
        return this;
    }

    /**
     * Set the HttpClient implementation to use for every synchronous request
     *
     * @param httpClient Custom httpClient implementation
     * @return this config object
     */
    public Config httpClient(Client httpClient) {
        client = Optional.of(httpClient);
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
     */
    public Config asyncClient(HttpAsyncClient value) {
        this.asyncClient = Optional.of(new ApacheAsyncClient(value, null, null));
        return this;
    }

    /**
     * Set the full async configuration including monitors. These will be shutDown on a Unirest.shudown()
     *
     * @param value Custom AsyncConfig class. The actual AsyncHttpClient is required.
     * @return this config object
     */
    public Config asyncClient(AsyncClient value) {
        asyncClient = Optional.of(value);
        return this;
    }

    /**
     * Set the full async configuration including monitors. These will be shutDown on a Unirest.shudown()
     *
     * @param asyncClientBuilder A builder function for creating a AsyncClient
     * @return this config object
     */
    public Config asyncClient(Function<Config, AsyncClient> asyncClientBuilder){
        this.asyncBuilder = asyncClientBuilder;
        return this;
    }

    /**
     * Set a proxy
     *
     * @param value Proxy settings object.
     * @return this config object
     */
    public Config proxy(HttpHost value) {
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
        return proxy(new HttpHost(host, port));
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
        proxyCreds = new BasicCredentialsProvider();
        proxyCreds.setCredentials(new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password));
        return proxy(new HttpHost(host, port));
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
        defaultHeaders.clear();
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
        defaultHeaders.add(name, value);
        return this;
    }

    /**
     * Add a HttpRequestInterceptor to the clients. This can be called multiple times to add as many as you like.
     * https://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpRequestInterceptor.html
     *
     * @param interceptor The addInterceptor
     * @return this config object
     */
    public Config addInterceptor(HttpRequestInterceptor interceptor) {
        validateClientsNotRunning();
        interceptors.add(interceptor);
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
     * Return default headers that are added to every request
     *
     * @return Headers
     */
    public Headers getDefaultHeaders() {
        return defaultHeaders;
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

        if(!ex.isEmpty()){
            throw new UnirestException(ex);
        }
    }

    /**
     * Return the current HttpClient. One will be build if it does
     * not yet exist.
     *
     * @return  Apache HttpClient
     */
    public HttpClient getClient() {
        if (!client.isPresent()) {
            buildClient();
        }
        return client.get().getClient();
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
     * @return  Apache HttpAsyncClient
     */
    public HttpAsyncClient getAsyncHttpClient() {
        if (!asyncClientIsReady()) {
            buildAsyncClient();
        }
        return asyncClient.get().getClient();
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
        if(!value.isRunning()){
            throw new UnirestConfigException("Attempted to get a new async client but it was not started. Please ensure it is");
        }
    }


    // Accessors for unirest.

    boolean getEnabledCookieManagement() {
        return cookieManagement;
    }

    boolean getFollowRedirects() {
        return followRedirects;
    }

    int getMaxConnections() {
        return maxTotal;
    }

    int getMaxPerRoutes() {
        return maxPerRoute;
    }

    int getConnectionTimeout() {
        return connectionTimeout;
    }

    int getSocketTimeout() {
        return socketTimeout;
    }

    ObjectMapper getObjectMapper() {
        return objectMapper.orElseThrow(() -> new UnirestException("No Object Mapper Configured. Please config one with Unirest.config().setObjectMapper"));
    }

    private void validateClientsNotRunning() {
        if (client.isPresent() || asyncClient.isPresent()) {
            throw new UnirestConfigException(
                    "Http Clients are already build in order to build a new config execute Unirest.config().reset() before changing settings. \n" +
                            "This should be done rarely."
            );
        }
    }

    List<HttpRequestInterceptor> getInterceptors() {
        return interceptors;
    }

    HttpHost getProxy() {
        return proxy;
    }

    CredentialsProvider getProxyCreds(){
        return proxyCreds;
    }

    boolean useSystemProperties(){
        return this.useSystemProperties;
    }
}
