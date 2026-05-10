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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The HTTP client abstraction responsible for executing HTTP requests.
 * <p>
 * This interface defines the contract for HTTP client implementations that
 * handle synchronous and asynchronous requests, WebSocket connections, and
 * Server-Sent Events (SSE). The default implementation uses Java's built-in
 * {@link java.net.http.HttpClient}.
 * </p>
 * <p>
 * Custom implementations can be provided to wrap other HTTP libraries or
 * to add custom behavior such as logging, metrics, or request manipulation.
 * </p>
 *
 * @see Config#httpClient(Client)
 */
public interface Client {
    /**
     * Returns the underlying HTTP client implementation.
     * <p>
     * This method provides access to the wrapped HTTP client library,
     * allowing direct access to implementation-specific features when needed.
     * </p>
     *
     * @param <T> the type of the underlying client
     * @return the underlying client instance
     */
    <T> T getClient();

    /**
     * Executes a synchronous HTTP request.
     *
     * @param <T> the type of the response body
     * @param request the prepared request object containing the HTTP method, URL, headers, and body
     * @param transformer the function to transform the raw response into the desired response type
     * @param resultType the expected body result type; used as a hint to downstream systems
     *                   to work around type erasure
     * @return an {@link HttpResponse} containing the status, headers, and transformed body
     */
    <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, Class<?> resultType);

    /**
     * Executes an asynchronous HTTP request.
     *
     * @param <T> the type of the response body
     * @param request the prepared request object containing the HTTP method, URL, headers, and body
     * @param transformer the function to transform the raw response into the desired response type
     * @param callback the {@link CompletableFuture} that will be completed with the response
     *                 when the request finishes
     * @param resultType the expected body result type; used as a hint to downstream systems
     *                   to work around type erasure
     * @return a {@link CompletableFuture} that will be completed with the HTTP response
     */
    <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request,
                                                   Function<RawResponse, HttpResponse<T>> transformer,
                                                   CompletableFuture<HttpResponse<T>> callback,
                                                   Class<?> resultType);

    /**
     * Creates a WebSocket connection.
     *
     * @param request the WebSocket connection request containing the URL and headers
     * @param listener the WebSocket listener to handle incoming messages and connection events
     * @return a {@link WebSocketResponse} representing the established WebSocket connection
     */
    WebSocketResponse websocket(WebSocketRequest request, WebSocket.Listener listener);


    /**
     * Executes a Server-Sent Events (SSE) connection with a handler.
     * <p>
     * Because SSE events are streamed, they are processed asynchronously.
     * The provided handler is invoked for each event received from the server.
     * </p>
     *
     * @param request the SSE request containing the URL and headers
     * @param handler the {@link SseHandler} to process incoming events
     * @return a {@link CompletableFuture} that completes when the SSE connection closes
     */
    CompletableFuture<Void> sse(SseRequest request, SseHandler handler);

    /**
     * Executes a Server-Sent Events (SSE) connection and returns a stream of events.
     * <p>
     * This method provides a reactive-style API for consuming SSE events as a
     * {@link Stream}. The stream will continue to provide events until the
     * connection is closed or an error occurs.
     * </p>
     *
     * @param request the SSE request containing the URL and headers
     * @return a {@link Stream} of {@link Event} objects representing the SSE events
     */
    Stream<Event> sse(SseRequest request);
}
