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



import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public interface AsyncClient {
    /**
     * @param <T> the underlying client
     * @return the underlying client if this instance is wrapping another library (like Apache).
      */
    <T> T getClient();

    /**
     * Make a Async request
     * @param <T> The type of the body
     * @param request the prepared request object
     * @param transformer the function to transform the response
     * @param callback the CompletableFuture that will handle the eventual response
     * @return a CompletableFuture of a response
     */
    <T> CompletableFuture<HttpResponse<T>> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer, CompletableFuture<HttpResponse<T>> callback);

    /**
     * @return a stream of exceptions possibly thrown while closing all the things.
     */
    default Stream<Exception> close() {
        return Stream.empty();
    }

    /**
     * @return is the client running?
     */
    default boolean isRunning() {
        return true;
    }
}
