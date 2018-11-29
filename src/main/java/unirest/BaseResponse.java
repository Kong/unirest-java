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

import org.apache.http.StatusLine;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

abstract class BaseResponse<T> implements HttpResponse<T> {

    private final Headers headers;
    private final String statusText;
    private final int statusCode;

    protected BaseResponse(org.apache.http.HttpResponse response){
        headers = new Headers(response.getAllHeaders());
        StatusLine statusLine = response.getStatusLine();
        this.statusCode = statusLine.getStatusCode();
        this.statusText = statusLine.getReasonPhrase();
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
    public Optional<RuntimeException> getParsingError() {
        return Optional.empty();
    }

    @Override
    public <V> V mapBody(Function<T, V> func){
        return func.apply(getBody());
    }

    @Override
    public <V> V mapRawBody(Function<InputStream, V> func) {
        return func.apply(getRawBody());
    }
}
