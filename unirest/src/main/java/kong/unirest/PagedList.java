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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PagedList<T> extends ArrayList<HttpResponse<T>> {

    /**
     * @return Returns all successful bodies
     */
    public List<T> getBodies() {
        return stream()
                .filter(HttpResponse::isSuccess)
                .map(HttpResponse::getBody)
                .collect(Collectors.toList());
    }

    /**
     * For each successful response If the response was a 200-series response. Invoke this consumer
     * can be chained with ifFailure
     * @param consumer a function to consume a HttpResponse
     * @return the same paged list
     */
    public PagedList<T> ifSuccess(Consumer<HttpResponse<T>> consumer) {
        stream().filter(HttpResponse::isSuccess).forEach(consumer);
        return this;
    }

    /**
     * For each failed response if the response was NOT a 200-series response or a mapping exception happened. Invoke this consumer
     * can be chained with ifSuccess
     * @param consumer a function to consume a HttpResponse
     * @return the same paged list
     */
    public PagedList<T> ifFailure(Consumer<HttpResponse<T>> consumer) {
        stream().filter(r -> !r.isSuccess()).forEach(consumer);
        return this;
    }
}
