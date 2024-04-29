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

package BehaviorTests;

import kong.unirest.core.HttpRequest;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.PagedList;
import kong.unirest.core.Unirest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class AsPagedTest extends BddTest {

    @Test
    void canFollowPaging() {
        MockServer.expectedPages(10);

        PagedList<RequestCapture> result = Unirest.get(MockServer.PAGED)
                .header("x-header", "h-value")
                .asPaged(
                        r -> r.asObject(RequestCapture.class),
                        r -> r.getHeaders().getFirst("nextPage")
                );

        assertThat(result)
                .hasSize(10)
                .allMatch(r -> {
                    r.getBody().assertHeader("x-header", "h-value");
                    return true;
                });
    }

    @Test
    void pagedRequestsAreUnique() {
        MockServer.expectedPages(5);

        var result = Unirest.get(MockServer.PAGED)
                .header("x-header", "h-value")
                .asPaged(
                        r -> r.asObject(RequestCapture.class),
                        r -> r.getHeaders().getFirst("nextPage")
                );

        var sums = result.stream()
                .map(HttpResponse::getRequestSummary)
                .collect(Collectors.toList());

        assertThat(sums)
                .hasSize(5)
                .extracting(e -> e.getUrl())
                .containsExactly(
                        "http://localhost:4567/paged",
                        "http://localhost:4567/paged?page=2",
                        "http://localhost:4567/paged?page=3",
                        "http://localhost:4567/paged?page=4",
                        "http://localhost:4567/paged?page=5"
                );

    }

    @Test
    void canFollowPagingForPost() {
        MockServer.expectedPages(10);

        PagedList<RequestCapture> result = Unirest.post(MockServer.PAGED)
                .body("Hi Mom")
                .asPaged(
                        r -> r.asObject(RequestCapture.class),
                        r -> r.getHeaders().getFirst("nextPage")
                );

        assertThat(result)
                .hasSize(10)
                .allMatch(r -> {
                    r.getBody().assertBody("Hi Mom");
                    return true;
                });
    }

    @Test
    void canCapturePagesAsStrings() {
        MockServer.expectedPages(10);

        PagedList<String> result = Unirest.get(MockServer.PAGED)
                .asPaged(
                        r -> r.asString(),
                        r -> r.getHeaders().getFirst("nextPage")
                );

        assertThat(result).hasSize(10);
    }

    @Test
    void willReturnOnePageIfthereWasNoPaging() {
        PagedList<RequestCapture> result = Unirest.get(MockServer.PAGED)
                .asPaged(
                        r -> r.asObject(RequestCapture.class),
                        r -> null
                );

        assertThat(result).hasSize(1);
    }

    @Test
    void asPagedWithRedirects() {
        Unirest.config().followRedirects(false);

        var responses = Unirest.get(MockServer.REDIRECT)
                .asPaged(HttpRequest::asString,
                        response -> List.of(301, 302).contains(response.getStatus())
                                ? "http://localhost:4567/" + response.getHeaders().getFirst("Location")
                                : null);


        assertThat(responses).hasSize(2);
    }
}
