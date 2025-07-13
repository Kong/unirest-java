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
    void eventWithId() {
        handle("data: foo", "id: 1", "");
        listener.assertEvent("1", "", "foo");
    }

    @Test //https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream
    void specExample1() {
        //The following event stream, once followed by a blank line:
        handle("data: YHOO",
                "data: +2",
                "data: 10",
                "");

        //...would cause an event message with the interface MessageEvent to be dispatched on the EventSource object.
        // The event's data attribute would contain the string "YHOO\n+2\n10" (where "\n" represents a newline).
        listener.assertEvent("", "", "YHOO\n+2\n10");
    }

    @Test
    void specExample2() {
        //The following stream contains four blocks. The first block has just a comment, and will fire nothing.
        //
        // The second block has two fields with names "data" and "id" respectively; an event will be fired for this block,
        // with the data "first event", and will then set the last event ID to "1" so that if the connection died between this block and the next,
        // the server would be sent a `Last-Event-ID` header with the value `1`.
        //
        // The third block fires an event with data "second event", and also has an "id" field,
        // this time with no value, which resets the last event ID to the empty string
        // (meaning no `Last-Event-ID` header will now be sent in the event of a reconnection being attempted).
        // Finally, the last block just fires an event with the data " third event" (with a single leading space character).
        // Note that the last still has to end with a blank line,
        //  the end of the stream is not enough to trigger the dispatch of the last event.
        handle(": test stream",
                "",
                "data: first event",
                "id: 1",
                "",
                "data:second event",
                "id",
                "",
                "data:  third event",
                "",
                "data: fourth event");


        listener.assertComment("test stream")
                .assertEvent("1", "", "first event")
                .assertEvent("",  "", "second event")
                .assertEvent("", "", " third event")
                .assertNoEventContaining("fourth event");
    }

    @Test
    void specExample3() {
        //The following stream fires two events:
        //The first block fires events with the data set to the empty string, as would the last block if it was followed by a blank line. The middle block fires an event with the data set to a single newline character.
        // The last block is discarded because it is not followed by a blank line.
        handle("data",
                "",
                "data",
                "data",
                "",
                "data:");

        listener.assertEvent("", "", "")
                .assertEvent("", "", "\n");

    }

    @Test
    void specExample4() {
        //The following stream fires two identical events:
        //This is because the space after the colon is ignored if present.
        handle("data:test",
                "",
                "data: test",
                "");

        listener.assertEvent("", "", "test");
    }

    private class TestListener implements SseListener {

        private List<Event> events = new ArrayList<>();
        private List<String>   comments = new ArrayList<>();

        @Override
        public void onEvent(Event event) {
            events.add(event);
        }
        @Override
        public void onComment(String line) {
            comments.add(line);
        }

        public TestListener assertEvent(String id, String event, String value) {
            Event expected = new Event(id, event, value);
            assertThat(events)
                    .contains(expected);
            return this;

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