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


import kong.unirest.core.java.Event;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * A mock HTTP client for testing Unirest-based code without making real network requests.
 * <p>
 * MockClient implements the {@link Client} interface and can be registered with a Unirest instance
 * to intercept HTTP requests. It supports both synchronous and asynchronous request handling,
 * as well as WebSocket connections.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Define expected requests and their responses using the fluent expectation API</li>
 *   <li>Verify that expected requests were made with specific parameters</li>
 *   <li>Simulate WebSocket connections for testing real-time communication</li>
 *   <li>Configure default responses for unexpected requests</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Register the mock client
 * MockClient mock = MockClient.register();
 *
 * // Set up expectations
 * mock.expect(HttpMethod.GET, "/api/users")
 *     .thenReturn()
 *     .withStatus(200)
 *     .withBody("[{\"id\": 1, \"name\": \"John\"}]");
 *
 * // Make requests using Unirest (they will be intercepted by the mock)
 * HttpResponse<String> response = Unirest.get("/api/users").asString();
 *
 * // Verify expectations
 * mock.assertThat(HttpMethod.GET, "/api/users").wasInvokedTimes(1);
 * mock.verifyAll();
 *
 * // Clean up
 * mock.reset();
 * }</pre>
 *
 * @see Client
 * @see Expectation
 * @see Assert
 */
public class MockClient implements Client {
    private final Supplier<Config> config;
    private List<Routes> routes = new ArrayList<>();
    private SocketSet remoteSocket;
    private Invocation defaultResponse;

    /**
     * Creates a new MockClient with the specified configuration supplier.
     *
     * @param config a supplier that provides the Unirest configuration
     */
    public MockClient(Supplier<Config> config){
        this.config = config;
    }

    /**
     * Creates a new MockClient and registers it on the primary static UnirestInstance.
     * <p>
     * This is a convenience method for quickly setting up mocking in tests that use
     * the static {@link Unirest} methods.
     * </p>
     *
     * @return the newly created and registered MockClient
     */
    public static MockClient register() {
        return register(Unirest.primaryInstance());
    }

    /**
     * Creates a new MockClient and registers it on the specified Unirest instance.
     * <p>
     * After registration, all HTTP requests made through the provided instance
     * will be intercepted by this mock client.
     * </p>
     *
     * @param unirest the Unirest instance to register the mock client with
     * @return the newly created and registered MockClient
     */
    public static MockClient register(UnirestInstance unirest) {
        MockClient client = new MockClient(unirest::config);
        unirest.config().httpClient(client);
        return client;
    }

    /**
     * Clears any MockClient from the primary Unirest instance.
     * <p>
     * This restores the primary instance to use the default HTTP client.
     * </p>
     */
    public static void clear() {
        clear(Unirest.primaryInstance());
    }

    /**
     * Clears any MockClient from the specified Unirest instance.
     * <p>
     * This restores the instance to use the default HTTP client.
     * Only clears the client if the current client is a MockClient.
     * </p>
     *
     * @param unirest the instance to clear the mock client from
     */
    public static void clear(UnirestInstance unirest) {
        if(unirest.config().getClient() instanceof MockClient){
            unirest.config().httpClient((Client) null);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Executes the request against configured expectations and returns a mocked response.
     * If no matching expectation is found, a new route is created with the default response.
     * </p>
     */
    @Override
    public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, Class<?> resultType) {
        Routes exp = findExpectation(request);
        Config c = this.config.get();
        c.getUniInterceptor().onRequest(request, c);
        MetricContext metric = c.getMetric().begin(request.toSummary());
        RawResponse response = exp.exchange(request, c);
        metric.complete(new ResponseSummary(response), null);
        HttpResponse<T> rez = transformer.apply(response);
        c.getUniInterceptor().onResponse(rez, request.toSummary(), c);
        return rez;
    }

    private Routes findExpectation(HttpRequest request) {
        return routes.stream()
                .filter(e -> e.matches(request))
                .findFirst()
                .orElseGet(() -> createNewPath(request));
    }

    private Routes createNewPath(HttpRequest request) {
        Routes p = new Routes(request, defaultResponse);
        routes.add(p);
        return p;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Executes the request asynchronously against configured expectations.
     * The request is processed in a separate thread using {@link CompletableFuture#supplyAsync}.
     * </p>
     */
    @Override
    public <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request,
                                                          Function<RawResponse, HttpResponse<T>> transformer,
                                                          CompletableFuture<HttpResponse<T>> callback,
                                                          Class<?> resultTypes) {
        return CompletableFuture.supplyAsync(() -> request(request, transformer, resultTypes));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a mock WebSocket connection with both client and server sides available for testing.
     * The server-side socket can be accessed via {@link #serversSocket()} to simulate
     * server-initiated messages.
     * </p>
     */
    @Override
    public WebSocketResponse websocket(WebSocketRequest request, WebSocket.Listener listener) {
        MockWebSocket clientSocket = new MockWebSocket();
        WebSocket.Listener clientListener = listener;

        MockWebSocket serverWebSocket = new MockWebSocket();
        MockListener serverListener = new MockListener();

        remoteSocket = new SocketSet(serverWebSocket, serverListener, "server");
        clientSocket.init(remoteSocket);
        serverWebSocket.init(new SocketSet(clientSocket, clientListener, "client"));

        return new WebSocketResponse(completedFuture(clientSocket), clientListener);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> SSE support is not yet implemented in the mock client.
     * This method currently returns {@code null}.
     * </p>
     */
    @Override
    public CompletableFuture<Void> sse(SseRequest request, SseHandler handler) {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note:</b> SSE support is not yet implemented in the mock client.
     * This method currently returns an empty stream.
     * </p>
     */
    @Override
    public Stream<Event> sse(SseRequest request) {
        HttpRequest<?> httpRequest = new MockSseHttpRequest(config.get(), request);
        RawResponse response = findExpectation(httpRequest).exchange(httpRequest, config.get());
        return parseSseEvents(response.getContentAsString());
    }

    private Stream<Event> parseSseEvents(String body) {
        if (body == null || body.isEmpty()) {
            return Stream.empty();
        }

        List<Event> events = new ArrayList<>();
        SseEventBuilder builder = new SseEventBuilder(config.get());

        for (String line : body.split("\\R", -1)) {
            if (line.isEmpty()) {
                builder.addEvent(events);
            } else if (!line.startsWith(":")) {
                builder.accept(line);
            }
        }

        builder.addEvent(events);
        return events.stream();
    }

    /**
     * Returns the server-side socket set for the most recent WebSocket connection.
     * <p>
     * This allows tests to simulate server behavior by sending messages from the server
     * side of the WebSocket connection.
     * </p>
     *
     * @return the server-side {@link SocketSet} containing the mock WebSocket and listener
     * @throws UnirestException if no WebSocket connection has been established
     */
    public SocketSet<MockWebSocket, MockListener> serversSocket() {
        if(remoteSocket == null){
            throw new UnirestException("No Socket Yet Established");
        }
        return remoteSocket;
    }



    /**
     * {@inheritDoc}
     *
     * @return this MockClient instance
     */
    @Override
    public Object getClient() {
        return this;
    }

    /**
     * Starts an expectation chain for a specific HTTP method and path.
     * <p>
     * Use this method to define what response should be returned when a matching
     * request is made.
     * </p>
     *
     * @param method the HTTP method to match (GET, POST, etc.)
     * @param path   the URL path to match (can include path parameters)
     * @return an {@link Expectation} which can have additional criteria and response configuration added
     */
    public Expectation expect(HttpMethod method, String path) {
        Path p = new Path(path);
        Routes exp = findByPath(method, p).orElseGet(() -> new Routes(method, p));
        if(!this.routes.contains(exp)) {
            this.routes.add(exp);
        }
        return exp.newExpectation();
    }

    /**
     * Starts an expectation chain for any request with the specified HTTP method.
     * <p>
     * This matches any path, useful when you only care about the HTTP method.
     * </p>
     *
     * @param method the HTTP method to match
     * @return an {@link Expectation} which can have additional criteria and response configuration added
     */
    public Expectation expect(HttpMethod method) {
        return expect(method, null);
    }

    /**
     * Creates an assertion for verifying a specific method and path were invoked.
     * <p>
     * Use this to verify that expected requests were made during a test.
     * </p>
     *
     * @param method the HTTP method to verify
     * @param path   the URL path to verify
     * @return an {@link Assert} object for chaining additional verification criteria
     * @throws UnirestAssertion if no matching invocation was found
     */
    public Assert assertThat(HttpMethod method, String path) {
        return findByPath(method, new Path(path))
                .orElseThrow(() -> new UnirestAssertion(String.format("No Matching Invocation:: %s %s", method, path)));
    }

    private Optional<Routes> findByPath(HttpMethod get, Path path) {
        return routes.stream()
                    .filter(e -> e.matches(get, path))
                    .findFirst();
    }

    /**
     * Verifies that all configured expectations were invoked.
     * <p>
     * This checks that every expectation set up with {@link #expect(HttpMethod, String)}
     * was actually called during the test.
     * </p>
     *
     * @throws UnirestAssertion if any expectation was not satisfied
     */
    public void verifyAll() {
        routes.forEach(Routes::verifyAll);
    }

    /**
     * Resets all expectations and clears the default response.
     * <p>
     * Call this between tests to ensure a clean state. This clears:
     * </p>
     * <ul>
     *   <li>All configured expectations</li>
     *   <li>All recorded invocations</li>
     *   <li>The default response</li>
     * </ul>
     */
    public void reset() {
        routes.clear();
        defaultResponse = null;
    }

    /**
     * Configures a default response for requests that don't match any expectation.
     * <p>
     * If a request is made that doesn't match any configured expectation,
     * this response will be returned instead of throwing an error.
     * </p>
     *
     * @return an {@link ExpectedResponse} for configuring the default response details
     */
    public ExpectedResponse defaultResponse() {
        this.defaultResponse = new Invocation();
        return this.defaultResponse.thenReturn();
    }

    private static class MockSseHttpRequest extends BaseRequest<MockSseHttpRequest> {
        private MockSseHttpRequest(Config config, SseRequest request) {
            super(config, HttpMethod.GET, request.getUrl());
            this.headers.putAll(request.getHeaders());
        }
    }

    private static class SseEventBuilder {

        private final Config config;
        private String id = "";
        private String event = "";
        private final StringBuilder data = new StringBuilder();

        private SseEventBuilder(Config config) {
            this.config = config;
        }

        private void accept(String line) {
            int separator = line.indexOf(':');
            String field = separator >= 0 ? line.substring(0, separator) : line;
            String value = separator >= 0 ? line.substring(separator + 1) : "";

            if (value.startsWith(" ")) {
                value = value.substring(1);
            }

            accept(field, value);
        }

        private void accept(String field, String value) {
            switch (field) {
                case "id":
                    id = value;
                    break;
                case "event":
                    event = value;
                    break;
                case "data":
                    data.append(value).append('\n');
                    break;
                default:
                    break;
            }
        }

        private void addEvent(List<Event> events) {
            if (data.length() == 0) {
                return;
            }

            data.setLength(data.length() - 1);
            events.add(new Event(id, event, data.toString(), config));
            reset();
        }

        private void reset() {
            id = "";
            event = "";
            data.setLength(0);
        }
    }
}
