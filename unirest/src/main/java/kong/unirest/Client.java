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

import java.util.function.Function;
import java.util.stream.Stream;

public interface Client {
    /**
     * @return the underlying client if this instance is wrapping another library (like Apache).
     */
    Object getClient();

    /**
     * Make a request
     * @param <T> The type of the body
     * @param request the prepared request object
     * @param transformer the function to transform the response
     * @return a HttpResponse with a transformed body
     * @deprecated use the version with the resultType
     */
    @Deprecated
    <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer);

    /**
     * Make a request
     * @param <T> The type of the body
     * @param request the prepared request object
     * @param transformer the function to transform the response
     * @param resultType the final body result type. This is a hint to downstream systems to make up for type erasure.
     * @return a HttpResponse with a transformed body
     */
    default <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, Class<?> resultType){
        return request(request, transformer);
    }

    /**
     * @return a stream of exceptions possibly thrown while closing all the things.
     */
    Stream<Exception> close();

    void registerShutdownHook();
}
