package io.github.openunirest.http.async;

import io.github.openunirest.http.BddTest;
import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.exceptions.UnirestException;

public class MockCallback<T> implements Callback<T> {
    private BddTest test;

    public MockCallback(BddTest test){
        this.test = test;
    }

    @Override
    public void completed(HttpResponse<T> response) {
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
