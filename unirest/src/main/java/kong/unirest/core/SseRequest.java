package kong.unirest.core;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SseRequest {
    private final Config config;
    private final Path url;
    protected Headers headers = new Headers();


    public SseRequest(Config config, String url) {
        Objects.requireNonNull(config, "Config cannot be null");
        Objects.requireNonNull(url, "URL cannot be null");

        this.config = config;
        this.url = new Path(url, config.getDefaultBaseUrl());
        headers.putAll(config.getDefaultHeaders());
    }

    public CompletableFuture<Void> connect(SseListener listener) {
        return config.getClient().sse(this, listener);
    }

    public Path getPath() {
        return url;
    }
}
