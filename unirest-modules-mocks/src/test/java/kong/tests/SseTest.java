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

package kong.tests;

import kong.unirest.core.MockEventStream;
import kong.unirest.core.SseHandler;
import kong.unirest.core.Unirest;
import kong.unirest.core.java.Event;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SseTest extends Base {

    @Test
    void whenNoMatchActLikeAFailure() {
        assertThatThrownBy(() -> Unirest.sse("http://wikipedia.org").connect())
                .hasMessage("SSE connection failed no matching expectations");

    }

    @Test
    void canMockSse() {
        var event = new Event("1", "message", "hello world");

        client.expectSSE("http://wikipedia.org")
                .thenReturn(event);

        assertThat(Unirest.sse("http://wikipedia.org").connect())
                .contains(event);
    }

    @Test
    void withQueryParams() {
        var event = new Event("1", "message", "hello world");

        client.expectSSE("http://wikipedia.org")
                .queryString("foo", "bar")
                .thenReturn(event);

        assertThat(Unirest.sse("http://wikipedia.org")
                .queryString("foo", "bar").connect())
                .contains(event);
    }

    @Test
    void bestMatchWhenMultipleExist() {
        var e1 = new Event("1", "message", "hello world");
        var e2 = new Event("2", "message", "I like cheese");

        client.expectSSE("http://wikipedia.org")
                .thenReturn(e1);

        client.expectSSE("http://wikipedia.org")
                .header("foo", "bar")
                .thenReturn(e2);

        assertThat(Unirest.sse("http://wikipedia.org").connect()).
                contains(e1);

        assertThat(Unirest.sse("http://wikipedia.org")
                .header("foo", "bar").connect()).
                contains(e2);
    }

    @Test
    void asyncExpects() throws Exception {
        var event = new Event("1", "message", "hello world");

        client.expectSSE("http://wikipedia.org")
                .thenReturn(event);

        var handler = new TestSseHandler();

        Unirest.sse("http://wikipedia.org").connect(handler).get();

        assertThat(handler.event).containsExactly(event);
    }

    @Test
    void canAlsoGetComments() throws Exception {
        var event1 = new Event("1", "message", "hello world");
        var event2 = new Event("2", "message", "hello cheese");
        var comment = "comment 1";

        client.expectSSE("http://wikipedia.org")
                .thenReturn(
                        MockEventStream.newStream(event1)
                        .comments(comment)
                        .events(event2));

        var handler = new TestSseHandler();

        Unirest.sse("http://wikipedia.org").connect(handler).get();

        assertThat(handler.event)
                .containsExactly(event1, comment, event2);

    }

    private class TestSseHandler implements SseHandler {
        private List<Object> event = new ArrayList<>();


        @Override
        public void onEvent(Event event) {
            this.event.add(event);
        }

        @Override
        public void onComment(String line) {
            this.event.add(line);
        }
    }
}
