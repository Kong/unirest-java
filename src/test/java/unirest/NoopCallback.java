package unirest;

import unirest.HttpResponse;
import unirest.Callback;
import unirest.UnirestException;

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
