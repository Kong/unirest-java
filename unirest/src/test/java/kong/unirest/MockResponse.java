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

package kong.unirest;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

class MockResponse<T> implements HttpResponse<T> {
    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getStatusText() {
        return null;
    }

    @Override
    public Headers getHeaders() {
        return null;
    }

    @Override
    public T getBody() {
        return null;
    }

    @Override
    public Optional<UnirestParsingException> getParsingError() {
        return Optional.empty();
    }

    @Override
    public <V> V mapBody(Function<T, V> func) {
        return null;
    }

    @Override
    public <V> HttpResponse<V> map(Function<T, V> func) {
        return null;
    }

    @Override
    public HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer) {
        return null;
    }

    @Override
    public HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer) {
        return null;
    }

    @Override
    public <E> HttpResponse<T> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer) {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public <E> E mapError(Class<? extends E> errorClass) {
        return null;
    }

    @Override
    public Cookies getCookies() {
        return null;
    }
}
