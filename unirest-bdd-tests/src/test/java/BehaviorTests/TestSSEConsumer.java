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

import io.javalin.http.sse.SseClient;
import kong.unirest.core.java.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public class TestSSEConsumer implements Consumer<SseClient> {
    private static final Queue<SseClient> clients = new ConcurrentLinkedDeque<>();
    private static final List<Event> queuedEvents = new ArrayList<>();
    private static RequestCapture lastRequest;
    private static boolean keepAlive = true;

    public static void reset(){
        clients.clear();
        queuedEvents.clear();
        keepAlive = true;
    }

    public static RequestCapture getLastRequest() {
        return lastRequest;
    }

    public static void sendComment(String message) {
        clients.forEach(c -> c.sendComment(message));
    }

    public static void sendEvent(String data) {
        clients.forEach(c -> c.sendEvent(data));
    }

    public static void sendEvent(String id, String event, String content) {
        clients.forEach(c -> c.sendEvent(event, content, id));
    }

    public static void sendEvent(String event, String content) {
        clients.forEach(c -> c.sendEvent(event, content));
    }

    public static void queueEvent(String id, String event, String content) {
        queuedEvents.add(new Event(id, event, content, null));
    }

    public static void keepAlive(boolean value) {
        keepAlive = value;
    }

    @Override
    public void accept(SseClient client) {
        lastRequest = new RequestCapture(client.ctx());
        if(keepAlive) {
            client.keepAlive();
        }
        client.sendEvent("connect", "Welcome to Server Sent Events");
        clients.add(client);
        queuedEvents.forEach(e -> {
            client.sendEvent(e.event(), e.data(), e.id());
        });
    }
}
