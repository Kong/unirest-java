package io.github.openunirest.http.async;

import io.github.openunirest.http.BddTest;
import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.exceptions.UnirestException;

import java.util.function.Consumer;

public class MockCallback<T> implements Callback<T> {
    private BddTest test;
    private Consumer<HttpResponse<T>> completionAsserts;

    public MockCallback(BddTest test, Consumer<HttpResponse<T>> completionAsserts){
        this.test = test;
        this.completionAsserts = completionAsserts;
    }

    @Override
    public void completed(HttpResponse<T> response) {
        completionAsserts.accept(response);
        test.asyncSuccess();
    }

    @Override
    public void failed(UnirestException e) {
        test.asyncFail(e.getMessage());
    }

    @Override
    public void cancelled() {
        test.asyncFail("canceled");
    }
}
