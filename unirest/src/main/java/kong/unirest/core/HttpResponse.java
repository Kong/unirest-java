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
 * Represents an HTTP response with a typed body.
 * <p>
 * This interface provides access to the response status, headers, body, and cookies.
 * It also supports functional-style operations for mapping and conditional processing.
 *
 * @param <T> the type of the response body
 */
public interface HttpResponse<T> {

    /**
     * Returns the HTTP status code of the response.
     *
     * @return the HTTP status code (e.g., 200, 404, 500)
     */
    int getStatus();

    /**
     * Returns the HTTP status text of the response.
     *
     * @return the HTTP status text (e.g., "OK", "Not Found")
     */
    String getStatusText();

    /**
     * Returns the response headers.
     * <p>
     * Headers are returned with the <b>same case</b> as the server response.
     * For instance, use {@code getHeaders().getFirst("Location")} and not
     * {@code getHeaders().getFirst("location")} to get the first "Location" header.
     *
     * @return the response {@link Headers}
     */
    Headers getHeaders();

    /**
     * Returns the response body.
     *
     * @return the body of type {@code T}
     */
    T getBody();

    /**
     * Returns any exception that occurred during body transformation.
     * <p>
     * If the transformation to the body failed, the exception is captured here.
     * Checked exceptions are wrapped in a {@link UnirestParsingException}.
     *
     * @return an {@link Optional} containing the parsing exception, or empty if parsing succeeded
     */
    Optional<UnirestParsingException> getParsingError();

    /**
     * Maps the body into another type.
     *
     * @param func a function to transform the body to another type
     * @param <V> the type to transform the body into
     * @return the result of applying the function to the body
     */
    <V> V mapBody(Function<T, V> func);

    /**
     * Maps this response into another response with a different body type.
     *
     * @param func a function to transform the body to another type
     * @param <V> the type to transform the body into
     * @return a new {@link HttpResponse} with the transformed body
     */
    <V> HttpResponse<V> map(Function<T, V> func);

    /**
     * Invokes the consumer if the response was successful (2xx status code).
     * <p>
     * Can be chained with {@link #ifFailure(Consumer)}.
     *
     * @param consumer a consumer to process the successful response
     * @return this response for method chaining
     */
    HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer);

    /**
     * Invokes the consumer if the response was not successful or a mapping exception occurred.
     * <p>
     * Can be chained with {@link #ifSuccess(Consumer)}.
     *
     * @param consumer a consumer to process the failed response
     * @return this response for method chaining
     */
    HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer);


    /**
     * Invokes the consumer if the response was not successful or a mapping exception occurred,
     * mapping the body to the specified error type.
     * <p>
     * Can be chained with {@link #ifSuccess(Consumer)}.
     *
     * @param <E> the error type to map the body to
     * @param errorClass the class of the error type
     * @param consumer a consumer to process the failed response with the mapped error body
     * @return this response for method chaining
     */
    <E> HttpResponse<T> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer);

    /**
     * Indicates whether this response represents a successful request.
     *
     * @return {@code true} if the response has a 2xx status code and no mapping exception occurred,
     *         {@code false} otherwise
     */
    boolean isSuccess();

    /**
     * Maps the body into an error object if the response was not successful.
     * <p>
     * Uses the configured {@link ObjectMapper} for deserialization.
     *
     * @param <E> the error type
     * @param errorClass the class to map the error body to
     * @return the mapped error object, or {@code null} if the response was successful
     */
    <E> E mapError(Class<? extends E> errorClass);

    /**
     * Returns the cookies from the response.
     * <p>
     * Cookies are parsed from the {@code Set-Cookie} header.
     *
     * @return a {@link Cookies} collection containing the response cookies
     */
    Cookies getCookies();

    /**
     * Returns a summary of the request that created this response.
     *
     * @return the {@link HttpRequestSummary} for the originating request
     */
    HttpRequestSummary getRequestSummary();
}
