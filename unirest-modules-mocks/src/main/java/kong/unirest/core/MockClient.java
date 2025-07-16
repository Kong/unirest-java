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


import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * A Mock client for unirest to make requests against
 * This implements both sync and async clients
 */
public class MockClient implements Client {
    private final Supplier<Config> config;
    private List<Routes> routes = new ArrayList<>();
    private SocketSet remoteSocket;
    private Invocation defaultResponse;

    public MockClient(Supplier<Config> config){
        this.config = config;
    }
    /**
     * Creates a new MockClient and registers it on the primary static UnirestInstance
     * @return the Mock Client
     */
    public static MockClient register() {
        return register(Unirest.primaryInstance());
    }

    /**
     * Creates a new MockClient and registers it on the Unirest instance
     * @param unirest an instance of Unirest
     * @return the Mock Client
     */
    public static MockClient register(UnirestInstance unirest) {
        MockClient client = new MockClient(unirest::config);
        unirest.config().httpClient(client);
        return client;
    }

    /**
     * Clears any MockClient from the primary instance
     */
    public static void clear() {
        clear(Unirest.primaryInstance());
    }

    /**
     * Clears any MockClient from the instance
     * @param unirest the instance to clear the mocks from
     */
    public static void clear(UnirestInstance unirest) {
        if(unirest.config().getClient() instanceof MockClient){
            unirest.config().httpClient((Client) null);
        }
    }

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

    @Override
    public <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request,
                                                          Function<RawResponse, HttpResponse<T>> transformer,
                                                          CompletableFuture<HttpResponse<T>> callback,
                                                          Class<?> resultTypes) {
        return CompletableFuture.supplyAsync(() -> request(request, transformer, resultTypes));
    }

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

    @Override
    public CompletableFuture<Void> sse(SseRequest request, SseHandler handler) {
        return null;
    }

    public SocketSet<MockWebSocket, MockListener> serversSocket() {
        if(remoteSocket == null){
            throw new UnirestException("No Socket Yet Established");
        }
        return remoteSocket;
    }



    @Override
    public Object getClient() {
        return this;
    }

    /**
     * Start an expectation chain.
     * @param method the Http method
     * @param path the base path
     * @return an Expectation which can have additional criteria added to it.
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
     * Expect ANY call to a path with this method
     * @param method the Http Method
     * @return this expectation builder
     */
    public Expectation expect(HttpMethod method) {
        return expect(method, null);
    }

    /**
     * Assert a specific method and path were invoked
     * @param method the Http method
     * @param path the base path
     * @return an Assert object which can have additional criteria chained to it.
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
     * Verify that all Expectations were invoked
     */
    public void verifyAll() {
        routes.forEach(Routes::verifyAll);
    }

    /**
     * Reset all expectations
     */
    public void reset() {
        routes.clear();
        defaultResponse = null;
    }

    /**
     * return this status for any request that doesn't match a expectation
     */
    public ExpectedResponse defaultResponse() {
        this.defaultResponse = new Invocation();
        return this.defaultResponse.thenReturn();
    }
}
