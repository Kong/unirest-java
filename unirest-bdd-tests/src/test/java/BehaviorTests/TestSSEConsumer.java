package BehaviorTests;

import io.javalin.http.sse.SseClient;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public class TestSSEConsumer implements Consumer<SseClient> {
    private static final Queue<SseClient> clients = new ConcurrentLinkedDeque<>();

    public static void sendComment(String message) {
        clients.forEach(c -> {
            c.sendComment(message);
        });
    }

    @Override
    public void accept(SseClient client) {
        client.keepAlive();
        client.sendEvent("connect", "Welcome to Server Side Events");
        clients.add(client);
    }
}
