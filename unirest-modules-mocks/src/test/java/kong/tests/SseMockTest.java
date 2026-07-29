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

import kong.unirest.core.HttpMethod;
import kong.unirest.core.Unirest;
import kong.unirest.core.java.Event;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SseMockTest extends Base {

    @Test
    void shouldReturnMockedSseEventsFromExpectedResponse() {
        client.expect(HttpMethod.GET, "http://events")
                .thenReturn(
                        "id: 1\n" +
                                "event: greeting\n" +
                                "data: hello\n" +
                                "\n" +
                                "id: 2\n" +
                                "event: greeting\n" +
                                "data: world\n" +
                                "\n")
                .withHeader("Content-Type", "text/event-stream");

        List<Event> events = Unirest.sse("http://events")
                .connect()
                .collect(Collectors.toList());

        assertEquals(2, events.size());

        assertEquals("1", events.get(0).id());
        assertEquals("greeting", events.get(0).event());
        assertEquals("hello", events.get(0).data());

        assertEquals("2", events.get(1).id());
        assertEquals("greeting", events.get(1).event());
        assertEquals("world", events.get(1).data());

        client.verifyAll();
    }
}