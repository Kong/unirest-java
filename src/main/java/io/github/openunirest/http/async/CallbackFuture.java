package io.github.openunirest.http.async;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.exceptions.UnirestException;

import java.util.concurrent.CompletableFuture;

public class CallbackFuture  {

    public static <T> CompletableFuture<HttpResponse<T>> wrap(Callback<T> source){
        return new CompletableFuture<HttpResponse<T>>(){
            @Override
            public boolean complete(HttpResponse<T> value) {
                source.completed(value);
                return super.complete(value);
            }
            @Override
            public boolean completeExceptionally(Throwable ex) {
                source.failed(new UnirestException(ex));
                return super.completeExceptionally(ex);
            }
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                source.cancelled();
                return super.cancel(mayInterruptIfRunning);
            }
        };
    }
}