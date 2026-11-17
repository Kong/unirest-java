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

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Defines the expected URL and headers, and the events to return, for a mocked
 * server-sent events (SSE) request.
 * <p>
 * Create expectations with {@link MockClient#expectSSE(String)}, then configure
 * matching query parameters and headers and supply the event stream.
 * </p>
 *
 * @see MockClient#expectSSE(String)
 */
public class SseExpectation {
    private final Path expectedUrl;
    private Headers expectedHeaders = new Headers();
    private MockEventStream expectedEvents = new MockEventStream();

    /**
     * Creates an SSE expectation for the specified URL.
     *
     * @param url the expected request URL
     */
    public SseExpectation(String url) {
        this.expectedUrl = new Path(url);
    }

    /**
     * Sets the events returned when this expectation matches.
     *
     * @param events the events to return
     */
    public void thenReturn(Event... events) {
        this.expectedEvents = MockEventStream.newStream(events);
    }

    /**
     * Sets the event stream returned when this expectation matches.
     *
     * @param events the mock event stream to return
     */
    public void thenReturn(MockEventStream events) {
        this.expectedEvents = events;
    }

    /**
     * Sets the event stream returned when this expectation matches.
     *
     * @param events the stream of events to return
     */
    public void thenReturn(Stream<Event> events) {
        this.expectedEvents.events(events);
    }

    /**
     * Adds an expected query parameter to the request URL.
     *
     * @param key the query parameter name
     * @param value the expected query parameter value
     * @return this expectation for method chaining
     */
    public SseExpectation queryString(String key, String value) {
        expectedUrl.queryString(key, value);
        return this;
    }

    /**
     * Adds an expected request header.
     *
     * @param key the header name
     * @param value the expected header value
     * @return this expectation for method chaining
     */
    public SseExpectation header(String key, String value) {
        expectedHeaders.add(key, value);
        return this;
    }

    /**
     * Checks whether a request has the URL and headers configured by this expectation.
     *
     * @param request the SSE request to check
     * @return {@code true} if both the URL and headers match; otherwise {@code false}
     */
    public boolean matches(SseRequest request) {
        var urlMatch = Objects.equals(expectedUrl.toString(), request.getUrl());
        var headerMatch = request.getHeaders().equals(expectedHeaders);
        return urlMatch && headerMatch;
    }

    /**
     * Delivers the expected events to the supplied handler.
     *
     * @param handler the handler that receives the expected events
     */
    public void handle(SseHandler handler) {
        expectedEvents.handle(handler);
    }

    /**
     * Returns the stream of events configured for this expectation.
     *
     * @return the configured event stream
     */
    public Stream<Event> getEventStream() {
        return expectedEvents.getEventStream();
    }
}
