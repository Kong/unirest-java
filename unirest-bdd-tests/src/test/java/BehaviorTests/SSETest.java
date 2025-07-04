package BehaviorTests;


import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SSETest extends BddTest {

    @Test
    void example() throws Exception {
        Queue<String> receivedMessages = new ConcurrentLinkedQueue<>();

        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Accept", "text/event-stream")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .uri(URI.create(MockServer.SSE))
                .build();

        var process = new Thread(() -> {
            var client = HttpClient.newBuilder().build();
            CompletableFuture<Void> fut = client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                    .thenAccept(response -> {
                        response.body().forEach(line -> {
                            if (!line.isBlank()) {
                                System.out.println("line = " + line);
                                receivedMessages.add(line);
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        System.err.println("Error: " + ex.getMessage());
                        return null;
                    });

            while(!fut.isDone()){}
        });

        process.start();
        Thread.sleep(1000);

        TestSSEConsumer.sendMessage("hey1");
        TestSSEConsumer.sendMessage("hey2");

        // Wait for messages to be received (simple sleep or use Awaitility for better control)
        Thread.sleep(1000);

        assertTrue(receivedMessages.stream().anyMatch(msg -> msg.contains("hey1")));
        assertTrue(receivedMessages.stream().anyMatch(msg -> msg.contains("hey2")));

    }


}