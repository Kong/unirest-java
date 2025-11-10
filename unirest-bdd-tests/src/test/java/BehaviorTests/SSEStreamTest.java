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

import kong.unirest.core.HttpMethod;
import kong.unirest.core.Unirest;
import kong.unirest.core.java.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SSEStreamTest extends BddTest {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        MockServer.Sse.keepAlive(false);
    }

    @Test
    void simpleStreamdEvents() {
        MockServer.Sse.queueEvent("1", "message", "hi mom");
        MockServer.Sse.queueEvent("2", "message", "i like cheese");

        var events = Unirest.sse(MockServer.SSE)
                .connect()
                .collect(Collectors.toList());

        assertThat(events)
                .hasSize(3)
                .containsExactly(
                        new Event("", "connect", "Welcome to Server Sent Events"),
                        new Event("1", "message", "hi mom"),
                        new Event("2", "message", "i like cheese")
                );
    }

    @Test
    void sendQueryParams() {
        Unirest.sse(MockServer.SSE)
                .queryString("foo", "bar")
                .queryString(Map.of("fruit", "apple", "number", 1))
                .queryString("droid", List.of("C3PO", "R2D2"))
                .connect()
                .collect(Collectors.toList());

        MockServer.lastRequest()
                .assertParam("foo", "bar")
                .assertParam("fruit", "apple")
                .assertParam("number", "1")
                .assertParam("droid", "C3PO")
                .assertParam("droid", "R2D2");
    }

    @Test
    void sendHeaders() {
        Unirest.sse(MockServer.SSE)
                .header("foo", "bar")
                .header("qux", "zip")
                .headerReplace("qux", "ok")
                .headers(Map.of("fruit", "apple", "number", "1"))
                .cookie("snack", "snickerdoodle")
                .connect()
                .collect(Collectors.toList());

        MockServer.lastRequest()
                .assertHeader("Accept", "text/event-stream")
                .assertHeader("foo", "bar")
                .assertHeader("qux", "ok")
                .assertHeader("number", "1")
                .assertHeader("fruit", "apple")
                .assertCookie("snack", "snickerdoodle");
    }

    @Test
    void canSendLastEventIdHeader() {
        Unirest.sse(MockServer.SSE)
                .lastEventId("42")
                .connect()
                .collect(Collectors.toList());

        MockServer.lastRequest()
                .assertHeader("Last-Event-ID", "42");
    }

    @Test
    void sendSSEAsAPost() {
        MockServer.Sse.queueEvent("1", "message", "hi mom");

        var events = Unirest.sse(MockServer.SSE, HttpMethod.POST)
                .connect()
                .collect(Collectors.toList());

        assertThat(events)
                .hasSize(2)
                .containsExactly(
                        new Event("", "connect", "Welcome to Server Sent Events"),
                        new Event("1", "message", "hi mom")
                );

    }
}
