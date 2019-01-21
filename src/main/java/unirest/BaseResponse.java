/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class BaseResponse<T> implements HttpResponse<T> {

    private final Headers headers;
    private final String statusText;
    private final int statusCode;
    private Optional<UnirestParsingException> parsingerror = Optional.empty();

    protected BaseResponse(RawResponse response){
        headers = response.getHeaders();
        this.statusCode = response.getStatus();
        this.statusText = response.getStatusText();
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public String getStatusText() {
        return statusText;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public abstract InputStream getRawBody();

    @Override
    public abstract T getBody();

    @Override
    public Optional<UnirestParsingException> getParsingError() {
        return parsingerror;
    }

    @Override
    public <V> V mapBody(Function<T, V> func){
        return func.apply(getBody());
    }

    @Override
    public <V> V mapRawBody(Function<InputStream, V> func) {
        return func.apply(getRawBody());
    }

    protected void setParsingException(String originalBody, RuntimeException e) {
        parsingerror = Optional.of(new UnirestParsingException(originalBody, e));
    }

    @Override
    public HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer) {
        if(isSuccess()){
            consumer.accept(this);
        }
        return this;
    }

    @Override
    public HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer) {
        if(!isSuccess()){
            consumer.accept(this);
        }
        return this;
    }

    @Override
    public boolean isSuccess() {
        return getStatus() >= 200 && getStatus() < 300 && !getParsingError().isPresent();
    }
}
