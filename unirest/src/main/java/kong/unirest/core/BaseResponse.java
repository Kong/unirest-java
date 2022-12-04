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

package kong.unirest.core;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class BaseResponse<T> implements HttpResponse<T> {

    private final Headers headers;
    private final String statusText;
    private final int statusCode;
    private final HttpRequestSummary reqSummary;
    private Optional<UnirestParsingException> parsingerror = Optional.empty();
    private final Config config;
    private Cookies cookies;


    protected BaseResponse(RawResponse response) {
        this.headers = response.getHeaders();
        // Unirest decompresses the content, so this should be removed as it is
        // no longer encoded
        this.headers.remove("Content-Encoding", "gzip");
        this.statusCode = response.getStatus();
        this.statusText = response.getStatusText();
        this.config = response.getConfig();
        this.reqSummary = response.getRequestSummary();
    }

    protected BaseResponse(BaseResponse other) {
        this.headers = other.headers;
        this.statusCode = other.statusCode;
        this.statusText = other.statusText;
        this.config = other.config;
        this.reqSummary = other.reqSummary;
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
    public abstract T getBody();

    @Override
    public Optional<UnirestParsingException> getParsingError() {
        return parsingerror;
    }

    @Override
    public <V> V mapBody(Function<T, V> func) {
        return func.apply(getBody());
    }

    @Override
    public <V> HttpResponse<V> map(Function<T, V> func) {
        return new BasicResponse(this, mapBody(func));
    }

    protected void setParsingException(String originalBody, RuntimeException e) {
        parsingerror = Optional.of(new UnirestParsingException(originalBody, e));
    }

    @Override
    public boolean isSuccess() {
        return getStatus() >= 200 && getStatus() < 300 && !getParsingError().isPresent();
    }

    @Override
    public HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer) {
        if (isSuccess()) {
            consumer.accept(this);
        }
        return this;
    }

    @Override
    public HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer) {
        if (!isSuccess()) {
            consumer.accept(this);
        }
        return this;
    }

    @Override
    public <E> E mapError(Class<? extends E> errorClass) {
        if (!isSuccess()) {
            String errorBody = getErrorBody();
            if(String.class.equals(errorClass)){
                return (E) errorBody;
            }
            try {
                return config.getObjectMapper().readValue(errorBody, errorClass);
            } catch (RuntimeException e) {
                setParsingException(errorBody, e);
            }
        }
        return null;
    }

    private String getErrorBody() {
        if (getParsingError().isPresent()) {
            return getParsingError().get().getOriginalBody();
        } else if (getRawBody() != null) {
            return getRawBody();
        }
        T body = getBody();
        if (body == null) {
            return null;
        }
        try {
            if(body instanceof byte[]){
                return new String((byte[])body, StandardCharsets.UTF_8);
            }
            return config.getObjectMapper().writeValue(body);
        } catch (Exception e) {
            return String.valueOf(body);
        }
    }

    @Override
    public <E> HttpResponse<T> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer) {
        if (!isSuccess()) {
            E error = mapError(errorClass);
            BasicResponse br = new BasicResponse(this, error);
            getParsingError().ifPresent(p -> br.setParsingException(p.getOriginalBody(), p));
            consumer.accept(br);
        }
        return this;
    }

    @Override
    public Cookies getCookies() {
        if (cookies == null) {
            cookies = new Cookies(headers.get("set-cookie"));
        }
        return cookies;
    }

    @Override
    public HttpRequestSummary getRequestSummary() {
        return reqSummary;
    }

    protected abstract String getRawBody();
}
