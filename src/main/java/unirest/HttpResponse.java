package unirest;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

public interface HttpResponse<T> {
    int getStatus();

    String getStatusText();

    Headers getHeaders();

    InputStream getRawBody();

    T getBody();

    Optional<RuntimeException> getParsingError();

    <V> V mapBody(Function<T, V> func);

    <V> V mapRawBody(Function<InputStream, V> func);
}
