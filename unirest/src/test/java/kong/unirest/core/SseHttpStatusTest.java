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

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SseHttpStatusTest {
    @Test
    void successfulResponseDeliversEvents() throws Exception {
        checkStatus(200);
    }

    @Test
    void unauthorizedResponseFailsBothConnections() throws Exception {
        checkStatus(401);
    }

    @Test
    void serverErrorFailsBothConnections() throws Exception {
        checkStatus(500);
    }

    private void checkStatus(int status) throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/sse", exchange -> {
            var body = (status == 200 ? "data: hello\n\n" : "{\"error\":\"failed\"}\n")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type",
                    status == 200 ? "text/event-stream" : "application/json");
            exchange.sendResponseHeaders(status, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        try (var unirest = Unirest.spawnInstance()) {
            var url = "http://127.0.0.1:" + server.getAddress().getPort() + "/sse";
            var events = new AtomicInteger();
            if (status == 200) {
                try (var stream = unirest.sse(url).connect()) {
                    assertEquals("hello", stream.findFirst().orElseThrow().data());
                }
                unirest.sse(url).connect(event -> events.incrementAndGet()).get(10, TimeUnit.SECONDS);
                assertEquals(1, events.get());
            } else {
                var syncError = assertThrows(UnirestException.class, () -> unirest.sse(url).connect());
                assertTrue(syncError.getMessage().contains(Integer.toString(status)));
                var future = unirest.sse(url).connect(event -> events.incrementAndGet());
                var asyncError = assertThrows(ExecutionException.class, () -> future.get(10, TimeUnit.SECONDS));
                assertTrue(asyncError.getCause().getMessage().contains(Integer.toString(status)));
                assertEquals(0, events.get());
            }
        } finally {
            server.stop(0);
        }
    }
}
