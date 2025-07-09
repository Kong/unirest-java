package kong.unirest.core;

public interface SseListener {
    void onEvent(String name, String data);
    void onComment(String line);
}
