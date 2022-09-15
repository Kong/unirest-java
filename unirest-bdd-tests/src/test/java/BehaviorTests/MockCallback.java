/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package BehaviorTests;

import kong.unirest.Callback;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.UnirestException;

import java.util.function.Consumer;

public class MockCallback<T> implements Callback<T> {

    public static MockCallback<JsonNode> json(BddTest test){
        return new MockCallback<>(test);
    }

    private final BddTest test;
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
