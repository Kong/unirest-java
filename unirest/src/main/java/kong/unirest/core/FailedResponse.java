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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a failed HTTP response when an exception occurs before receiving a server response.
 * <p>
 * This class can be returned by an interceptor instead of throwing an exception,
 * allowing failed requests to be handled through the normal response processing flow.
 * Since no actual HTTP response was received, this class provides synthetic values
 * for status code, headers, and body.
 * </p>
 *
 * @param <T> the type of the response body (always {@code null} for failed responses)
 * @see HttpResponse
 */
public class FailedResponse<T> implements HttpResponse<T> {
    private final Exception failureReason;

    /**
     * Creates a new FailedResponse wrapping the given exception.
     *
     * @param e the exception that caused the request to fail
     */
    public FailedResponse(Exception e) {
        this.failureReason = e;
    }

    /**
     * Returns a synthetic status code of 542.
     * <p>
     * Since no actual response was received from the server, this method returns
     * a non-standard 5xx status code to indicate a client-side failure.
     * </p>
     *
     * @return 542 (a synthetic error status code)
     */
    @Override
    public int getStatus() {
        return 542;
    }

    /**
     * Returns the exception message as the status text.
     *
     * @return the message from the underlying exception
     */
    @Override
    public String getStatusText() {
        return failureReason.getMessage();
    }

    /**
     * Returns an empty headers collection.
     * <p>
     * Since no response was received, there are no headers to return.
     * </p>
     *
     * @return an empty {@link Headers} object
     */
    @Override
    public Headers getHeaders() {
        return new Headers();
    }

    /**
     * Returns {@code null} since no response body was received.
     *
     * @return {@code null}
     */
    @Override
    public T getBody() {
        return null;
    }

    /**
     * Returns a parsing exception containing the original failure reason.
     *
     * @return an {@link Optional} containing a {@link UnirestParsingException} wrapping the failure
     */
    @Override
    public Optional<UnirestParsingException> getParsingError() {
        return Optional.of(new UnirestParsingException(failureReason.getMessage(), failureReason));
    }

    /**
     * Applies the mapping function to the body.
     * <p>
     * Since the body is always {@code null} for failed responses, this passes
     * {@code null} to the function.
     * </p>
     *
     * @param func a function to transform the body
     * @param <V> the type to transform the body into
     * @return the result of applying the function to {@code null}
     */
    @Override
    public <V> V mapBody(Function<T, V> func) {
        return func.apply(null);
    }

    /**
     * Maps this response to a new response type.
     * <p>
     * Since the body is always {@code null} for failed responses, this passes
     * {@code null} to the function.
     * </p>
     *
     * @param func a function to transform the body
     * @param <V> the type to transform the body into
     * @return a new HttpResponse with the transformed body type
     */
    @Override
    public <V> HttpResponse<V> map(Function<T, V> func) {
        return (HttpResponse<V>) func.apply(null);
    }

    /**
     * Does nothing, as this response is never successful.
     *
     * @param consumer a function to consume a successful HttpResponse (never invoked)
     * @return this HttpResponse
     */
    @Override
    public HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer) {
        return this;
    }

    /**
     * Invokes the consumer with this response.
     * <p>
     * Since this is always a failed response, the consumer is always invoked.
     * </p>
     *
     * @param consumer a function to consume the failed HttpResponse
     * @return this HttpResponse
     */
    @Override
    public HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * Invokes the consumer for a failed response.
     * <p>
     * Note: The consumer receives {@code null} since the body cannot be mapped
     * to the error class when no response was received.
     * </p>
     *
     * @param <E> the error type
     * @param errorClass the class to transform the body to (unused since body is null)
     * @param consumer a function to consume the failed HttpResponse
     * @return this HttpResponse
     */
    @Override
    public <E> HttpResponse<T> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer) {
        consumer.accept(null);
        return this;
    }

    /**
     * Always returns {@code false} since this represents a failed response.
     *
     * @return {@code false}
     */
    @Override
    public boolean isSuccess() {
        return false;
    }

    /**
     * Returns {@code null} since there is no body to map to an error class.
     *
     * @param <E> the error type
     * @param errorClass the class for the error (unused)
     * @return {@code null}
     */
    @Override
    public <E> E mapError(Class<? extends E> errorClass) {
        return null;
    }

    /**
     * Returns an empty cookies collection.
     * <p>
     * Since no response was received, there are no cookies to return.
     * </p>
     *
     * @return an empty {@link Cookies} collection
     */
    @Override
    public Cookies getCookies() {
        return new Cookies();
    }

    /**
     * Returns {@code null} since there is no request summary available.
     *
     * @return {@code null}
     */
    @Override
    public HttpRequestSummary getRequestSummary() {
        return null;
    }

}
