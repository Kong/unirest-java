package util;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.async.Callback;
import io.github.openunirest.http.exceptions.UnirestException;

public class NoopCallback<T> implements Callback<T> {
    @Override
    public void completed(HttpResponse<T> response) {

    }

    @Override
    public void failed(UnirestException e) {

    }

    @Override
    public void cancelled() {

    }
}
