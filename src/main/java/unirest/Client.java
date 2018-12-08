package unirest;

import org.apache.http.client.HttpClient;

import java.util.stream.Stream;

public interface Client {
    HttpClient getClient();

    Stream<Exception> close();
}
