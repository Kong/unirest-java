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
 * The HTTP client interface responsible for executing HTTP requests.
 * <p>
 * This interface defines the contract for the underlying HTTP client implementation
 * that performs the actual network communication. It supports synchronous and asynchronous
 * HTTP requests, WebSocket connections, and Server-Sent Events (SSE).
 * </p>
 * <p>
 * The default implementation uses Java's built-in {@link java.net.http.HttpClient},
 * but this interface allows for alternative implementations or mock clients for testing.
 * </p>
 *
 * @see HttpRequest
 * @see HttpResponse
 * @see RawResponse
 */
public interface Client {

    /**
     * Returns the underlying HTTP client instance.
     * <p>
     * This method provides access to the native HTTP client being wrapped by this implementation,
     * allowing direct interaction with the underlying library when needed.
     * </p>
     *
     * @param <T> the type of the underlying client (e.g., {@link java.net.http.HttpClient})
     * @return the underlying client instance
     */
    <T> T getClient();

    /**
     * Executes a synchronous HTTP request and transforms the response.
     * <p>
     * This method blocks until the response is received and transformed.
     * </p>
     *
     * @param <T>         the type of the transformed response body
     * @param request     the prepared HTTP request to execute
     * @param transformer a function to transform the raw response into the desired type
     * @param resultType  the expected body result type; used as a hint to downstream systems
     *                    to work around Java type erasure
     * @return an {@link HttpResponse} containing the transformed body
     */
    <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, Class<?> resultType);

    /**
     * Executes an asynchronous HTTP request and transforms the response.
     * <p>
     * This method returns immediately with a {@link CompletableFuture} that will be
     * completed when the response is received and transformed.
     * </p>
     *
     * @param <T>         the type of the transformed response body
     * @param request     the prepared HTTP request to execute
     * @param transformer a function to transform the raw response into the desired type
     * @param callback    the {@link CompletableFuture} that will be completed with the response
     * @param resultType  the expected body result type; used as a hint to downstream systems
     *                    to work around Java type erasure
     * @return a {@link CompletableFuture} that will complete with the transformed {@link HttpResponse}
     */
    <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request,
                                                   Function<RawResponse, HttpResponse<T>> transformer,
                                                   CompletableFuture<HttpResponse<T>> callback,
                                                   Class<?> resultType);

    /**
     * Establishes a WebSocket connection.
     * <p>
     * Creates a WebSocket connection to the specified endpoint and associates it
     * with the provided listener to handle incoming messages and connection events.
     * </p>
     *
     * @param request  the WebSocket connection request containing the target URI and configuration
     * @param listener the {@link WebSocket.Listener} to handle WebSocket events
     * @return a {@link WebSocketResponse} representing the established connection
     */
    WebSocketResponse websocket(WebSocketRequest request, WebSocket.Listener listener);

    /**
     * Establishes a Server-Sent Events (SSE) connection with a handler.
     * <p>
     * Opens an SSE connection to the specified endpoint. Because SSE events are
     * streamed continuously, they are processed asynchronously. The provided handler
     * is used to consume incoming events as they arrive.
     * </p>
     *
     * @param request the SSE request containing the target URI and configuration
     * @param handler the {@link SseHandler} to process incoming events
     * @return a {@link CompletableFuture} that completes when the SSE connection is closed
     */
    CompletableFuture<Void> sse(SseRequest request, SseHandler handler);

    /**
     * Establishes a Server-Sent Events (SSE) connection and returns a stream of events.
     * <p>
     * Opens an SSE connection to the specified endpoint and returns a {@link Stream}
     * of events that can be processed using standard stream operations.
     * </p>
     *
     * @param request the SSE request containing the target URI and configuration
     * @return a {@link Stream} of {@link Event} objects representing the incoming SSE events
     */
    Stream<Event> sse(SseRequest request);
}
