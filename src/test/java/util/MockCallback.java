package util;

import BehaviorTests.BddTest;
import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.JsonNode;
import io.github.openunirest.http.async.Callback;
import io.github.openunirest.http.exceptions.UnirestException;

import java.util.function.Consumer;

public class MockCallback<T> implements Callback<T> {

    public static MockCallback<JsonNode> json(BddTest test){
        return new MockCallback<>(test);
    }

    private BddTest test;
    private Consumer<HttpResponse<T>> onSuccess = r -> {};
    private Consumer<UnirestException> onFail = f -> {};
    private Runnable onCancel = () -> {};

    public MockCallback(BddTest test){
        this.test = test;
    }

    public MockCallback(BddTest test, Consumer<HttpResponse<T>> onSuccess){
        this.test = test;
        this.onSuccess = onSuccess;
    }

    public MockCallback<T> onFail(Consumer<UnirestException> onFail){
        this.onFail = onFail;
        return this;
    }

    public MockCallback<T> onCancel(Consumer<UnirestException> onFail){
        this.onFail = onFail;
        return this;
    }

    @Override
    public void completed(HttpResponse<T> response) {
        onSuccess.accept(response);
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
