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

package kong.unirest.core.java;

import kong.unirest.core.SseListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SseResponseHandlerTest {

    SseResponseHandler handler;
    TestListener listener;

    @BeforeEach
    void setUp() {
        listener = new TestListener();
        handler = new SseResponseHandler(listener);
    }

    @Test
    void sendComment(){
        handle(": Hello World");
        listener.assertComment("Hello World");
    }

    @Test
    void dontSendDataWithoutADispatch() {
        handle("data: foo");
        listener.assertEventCount(0);
    }

    @Test
    void sendSimpleEvent() {
        handle("data: foo", "");
        listener.assertEventCount(1)
                .assertEvent("", "", "foo");
    }

    @Test
    void dataWithJson(){
        handle("data: \"foo\": 1", "");
        listener.assertEventCount(1)
                .assertEvent("", "", "\"foo\": 1");
    }

    @Test
    void multipleDataElementEvent() {
        handle("data: YHOO",
                "data: +2",
                "data: 10",
                "");

        listener.assertEvent("", "", "YHOO\n+2\n10");
    }

    @Test
    void eventWithId() {
        handle("data: foo", "id: 1", "");
        listener.assertEvent("1", "", "foo");
    }

    @Test
    void dataEventsWithIds() {
        handle(": test stream",
                "",
                "data: first event",
                "id: 1",
                "",
                "data:second event",
                "id",
                "",
                "data:  third event");

        listener.assertComment("test stream");
        listener.assertEvent("1", "", "first event");
        listener.assertEvent("",  "", "second event");
        listener.assertNoEventContaining("third event");
    }

    private class TestListener implements SseListener {

        private List<SseEvent> events = new ArrayList<>();
        private List<String>   comments = new ArrayList<>();

        @Override
        public void onEvent(SseEvent event) {
            events.add(event);
        }
        @Override
        public void onComment(String line) {
            comments.add(line);
        }

        public void assertEvent(String id, String event, String value) {
            SseEvent expected = new SseEvent(id, event, value);
            assertThat(events)
                    .contains(expected);

        }

        public TestListener assertComment(String comment) {
            assertThat(comments)
                    .contains(comment);
            return this;
        }

        public TestListener assertNoEventContaining(String value) {
            assertThat(events)
                    .extracting(e -> e.data())
                    .doesNotContain(value);

            return this;
        }

        public TestListener assertEventCount(int count) {
            assertThat(events)
                    .hasSize(count);
            return this;
        }
    }

    private void handle(String... lines) {
        var res = mock(HttpResponse.class);
        when(res.body()).thenReturn(Stream.of(lines));
        handler.accept(res);
    }

}