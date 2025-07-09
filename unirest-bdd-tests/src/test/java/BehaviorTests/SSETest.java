package BehaviorTests;


import kong.unirest.core.SseListener;
import kong.unirest.core.Unirest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SSETest extends BddTest {

    @Test
    void example() throws Exception {

        Listener listener = new Listener();

        TestUtil.run(() -> {
            var future = Unirest.sse(MockServer.SSE).connect(listener);
            TestUtil.blockUntil(() -> future.isDone());
        });

        Thread.sleep(1000);

        TestSSEConsumer.sendComment("hey1");
        TestSSEConsumer.sendComment("hey2");

        // Wait for messages to be received (simple sleep or use Awaitility for better control)
        Thread.sleep(1000);

        assertTrue(listener.stream().anyMatch(msg -> msg.contains("hey1")));
        assertTrue(listener.stream().anyMatch(msg -> msg.contains("hey2")));

    }

    public class Listener implements SseListener {
        Queue<String> receivedMessages = new ConcurrentLinkedQueue<>();

        public void add(String message){
            receivedMessages.add(message);
        }

        public Stream<String> stream() {
            return receivedMessages.stream();
        }

        @Override
        public void onEvent(String name, String data) {

        }

        @Override
        public void onComment(String line) {
            receivedMessages.add(line);
        }
    }

}