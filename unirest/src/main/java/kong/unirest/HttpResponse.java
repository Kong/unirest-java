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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @param <T> a Http Response holding a specific type of body.
 */
public interface HttpResponse<T> {

    /**
     * @return the HTTP status code.
     */
    int getStatus();

    /**
     * @return status text
     */
    String getStatusText();

    /**
     * @return Response Headers (map) with <b>same case</b> as server response.
     * For instance use <code>getHeaders().getFirst("Location")</code> and not <code>getHeaders().getFirst("location")</code> to get first header "Location"
     */
    Headers getHeaders();

    /**
     * @return the body
     */
    T getBody();

    /**
     * If the transformation to the body failed by an exception it will be kept here
     * @return a possible RuntimeException. Checked exceptions are wrapped in a UnirestException
     */
    Optional<UnirestParsingException> getParsingError();

    /**
     * Map the body into another type
     * @param func a function to transform a body type to something else.
     * @param <V> The return type of the function
     * @return the return type
     */
    <V> V mapBody(Function<T, V> func);

    /**
     * Map the Response into another response with a different body
     * @param func a function to transform a body type to something else.
     * @param <V> The return type of the function
     * @return the return type
     */
    <V> HttpResponse<V> map(Function<T, V> func);

    /**
     * If the response was a 200-series response. Invoke this consumer
     * can be chained with ifFailure
     * @param consumer a function to consume a HttpResponse
     * @return the same response
     */
    HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer);

    /**
     * If the response was NOT a 200-series response or a mapping exception happened. Invoke this consumer
     * can be chained with ifSuccess
     * @param consumer a function to consume a HttpResponse
     * @return the same response
     */
    HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer);

     /**
     * @return true if the response was a 200-series response and no mapping exception happened, else false
     */
    boolean isSuccess();
}
