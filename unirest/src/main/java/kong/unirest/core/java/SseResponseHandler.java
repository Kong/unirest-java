package kong.unirest.core.java;

import kong.unirest.core.SseListener;

import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.stream.Stream;

class SseResponseHandler implements Consumer<HttpResponse<Stream<String>>> {
    private final SseListener listener;

    public SseResponseHandler(SseListener listener) {
        this.listener = listener;
    }

    @Override
    public void accept(HttpResponse<Stream<String>> response) {
            response.body().forEach(line -> {
                if (!line.isBlank()) {
                    listener.onComment(line);
                }
            });
    }
}
