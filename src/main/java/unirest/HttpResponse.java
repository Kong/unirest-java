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

import java.io.InputStream;
import java.util.Optional;
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
     * This method is a lie. You never get the real raw response from it
     * you only get a copy, or worse, the body transformed BACK to a stream
     * If you want to use raw content use the new functional methods
     * @deprecated this method is redundant and not the original stream. Use the functional asObject methods.
     * @return a copy of the input stream
     * */
    @Deprecated
    InputStream getRawBody();

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
     * @param func a function to transform a body type to something else.
     * @param <V> The return type of the function
     * @return the return type
     */
    <V> V mapBody(Function<T, V> func);

    /**
     * This method is a lie. You never get the real raw response from it
     * you only get a copy, or worse, the body transformed BACK to a stream
     * If you want to use raw content use the new functional methods
     * @param func a function to map a inputstream to a new body
     * @param <V> the return type
     * @return the return type
     */
    @Deprecated
    <V> V mapRawBody(Function<InputStream, V> func);
}
