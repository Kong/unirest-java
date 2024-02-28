package kong.unirest.core;

@FunctionalInterface
public interface Signer {
    void sign(HttpRequest<?> request);
}
