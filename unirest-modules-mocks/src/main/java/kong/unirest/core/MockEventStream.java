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

import kong.unirest.core.java.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds a sequence of mock server-sent events and comments for an SSE response.
 * <p>
 * Add events and comments with {@link #events(Event...)} and {@link #comments(String...)},
 * then deliver them to an {@link SseHandler} with {@link #handle(SseHandler)}.
 * </p>
 *
 * @see SseExpectation
 */
public class MockEventStream {
    private List<Either> expectedStream = new ArrayList<>();

    /**
     * Creates a mock event stream containing the supplied events.
     *
     * @param events the events to add to the stream
     * @return a new stream containing the supplied events
     */
    public static MockEventStream newStream(Event... events) {
        return new MockEventStream().events(events);
    }

    /**
     * Adds events to this stream in the order supplied.
     *
     * @param event the events to add
     * @return this stream for method chaining
     */
    public MockEventStream events(Event... event) {
        return events(Stream.of(event));
    }

    /**
     * Adds events from the supplied stream to this stream in encounter order.
     *
     * @param events the stream of events to add
     * @return this stream for method chaining
     */
    public MockEventStream events(Stream<Event> events) {
        expectedStream.
                addAll(events.map(Either::new)
                        .collect(Collectors.toList()));
        return this;
    }

    /**
     * Adds comments to this stream in the order supplied.
     *
     * @param comments the comments to add
     * @return this stream for method chaining
     */
    public MockEventStream comments(String... comments) {
        for (String com : comments) {
            expectedStream.add(new Either(com));
        }
        return this;
    }

    /**
     * Delivers each configured event or comment to the supplied handler in order.
     *
     * @param handler the handler that receives the events and comments
     */
    public void handle(SseHandler handler) {
        expectedStream.forEach(e -> {
            if(e.comment != null){
                handler.onComment(e.comment);
            } else if(e.event != null) {
                handler.onEvent(e.event);
            }
        });
    }

    /**
     * Returns the configured events, excluding comments.
     *
     * @return a stream of configured events in encounter order
     */
    public Stream<Event> getEventStream() {
        return expectedStream.stream()
                .map(e -> e.event)
                .filter(Objects::nonNull);
    }

    private static class Either {
        private Event event;
        private String comment;

        Either(Event e) {
            this.event = e;
        }

        Either(String message) {
            this.comment = message;
        }
    }
}
