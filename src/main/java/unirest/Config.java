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
import org.apache.http.client.HttpClient;
import org.apache.http.nio.client.HttpAsyncClient;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    public static final int DEFAULT_MAX_CONNECTIONS = 200;
    public static final int DEFAULT_MAX_PER_ROUTE = 20;
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 60000;

    private ClientFactory factory = new ClientFactory(this);

    private Optional<ClientConfig> client = Optional.empty();
    private Optional<AsyncConfig> asyncClient = Optional.empty();
    private Optional<ObjectMapper> objectMapper = Optional.empty();

    private List<HttpRequestInterceptor> interceptors = new ArrayList<>();
    private HttpHost proxy;
    private Headers defaultHeaders;
    private int connectionTimeout;
    private int socketTimeout;
    private int maxTotal;
    private int maxPerRoute;
    private boolean followRedirects;
    private boolean cookieManagement;

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

    public Config(){
        setDefaults();
    }

    /**
     * Set the HttpClient implementation to use for every synchronous request
     *
     * @param httpClient Custom httpClient implementation
     */
    public Config httpClient(HttpClient httpClient) {
        client = Optional.of(new ClientConfig(httpClient, null, null));
        return this;
    }

    /**
     * Set the HttpClient implementation to use for every synchronous request
     *
     * @param httpClient Custom httpClient implementation
     */
    public Config httpClient(ClientConfig httpClient) {
        client = Optional.of(httpClient);
        return this;
    }

    /**
     * Set the asynchronous AbstractHttpAsyncClient implementation to use for every asynchronous request
     *
     * @param value Custom CloseableHttpAsyncClient implementation
     */
    public Config asyncClient(HttpAsyncClient value) {
        this.asyncClient = Optional.of(new AsyncConfig(value, null, null));
        return this;
    }

    /**
     * Set the full async configuration including monitors. These will be shutDown on a Unirest.shudown()
     *
     * @param value Custom AsyncConfig class. The actual AsyncHttpClient is required.
     */
    public Config asyncClient(AsyncConfig value) {
        asyncClient = Optional.of(value);
        return this;
    }

    /**
     * Set a proxy
     *
     * @param value Proxy settings object.
     */
    public Config proxy(HttpHost value) {
        this.proxy = value;
        return this;
    }

    /**
     * Set the ObjectMapper implementation to use for Response to Object binding
     *
     * @param om Custom implementation of ObjectMapper interface
     */
    public Config setObjectMapper(ObjectMapper om) {
        this.objectMapper = Optional.ofNullable(om);
        return this;
    }

    /**
     * Set the connection timeout
     *
     * @param inMillies The timeout until a connection with the server is established (in milliseconds). Default is 10000. Set to zero to disable the timeout.
     */
    public Config connectTimeout(int inMillies) {
        validateNotCustomClient();
        this.connectionTimeout = inMillies;
        return this;
    }

    /**
     * Set the socket timeout
     *
     * @param inMillies The timeout to receive data (in milliseconds). Default is 60000. Set to zero to disable the timeout.
     */
    public Config socketTimeout(int inMillies) {
        validateNotCustomClient();
        this.socketTimeout = inMillies;
        return this;
    }

    /**
     * Set the concurrency levels
     *
     * @param total    Defines the overall connection limit for a connection pool. Default is 200.
     * @param perRoute Defines a connection limit per one HTTP route (this can be considered a per target host limit). Default is 20.
     */
    public Config concurrency(int total, int perRoute) {
        validateNotCustomClient();
        this.maxTotal = total;
        this.maxPerRoute = perRoute;
        return this;
    }

    /**
     * Clear default headers
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
     */
    public Config addInterceptor(HttpRequestInterceptor interceptor) {
        validateNotCustomClient();
        interceptors.add(interceptor);
        return this;
    }

    /**
     * Allow the client to follow redirects. Defaults to TRUE
     *
     * @param enable The name of the header.
     */
    public Config followRedirects(boolean enable) {
        validateNotCustomClient();
        this.followRedirects = enable;
        return this;
    }

    /**
     * Allow the client to manage cookies. Defaults to TRUE
     *
     * @param enable The name of the header.
     */
    public Config enableCookieManagement(boolean enable) {
        validateNotCustomClient();
        this.cookieManagement = enable;
        return this;
    }

    public void shutDown(boolean clearOptions) {
        List<Exception> ex = Stream.concat(
                client.map(ClientConfig::close).orElseGet(Stream::empty),
                asyncClient.map(AsyncConfig::close).orElseGet(Stream::empty)
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

    private void validateNotCustomClient() {
        if (client.isPresent() || asyncClient.isPresent()) {
            throw new UnirestConfigException(
                    "You can't set custom client settings when providing custom client implementations. " +
                            "Set the timeouts directly in your custom client configuration instead."
            );
        }
    }

    public HttpClient getClient() {
        if (!client.isPresent()) {
            client = Optional.of(factory.buildHttpClient());
        }
        return client.get().getClient();
    }

    public HttpAsyncClient getAsyncHttpClient() {
        if (!asyncClient.isPresent()) {
            buildAsyncClient();
        }
        return asyncClient.get().getClient();
    }

    private synchronized void buildAsyncClient() {
        if (!asyncClient.isPresent()) {
            AsyncConfig value = factory.buildAsyncClient();
            asyncClient = Optional.of(value);
        }
    }

    List<HttpRequestInterceptor> getInterceptors() {
        return interceptors;
    }

    HttpHost getProxy() {
        return proxy;
    }

    public Headers getDefaultHeaders() {
        return defaultHeaders;
    }

    public boolean isRunning() {
        return client.isPresent() || asyncClient.isPresent();
    }

    public static Stream<Exception> collectExceptions(Optional<Exception>... ex) {
        return Stream.of(ex).flatMap(Util::stream);
    }
}
