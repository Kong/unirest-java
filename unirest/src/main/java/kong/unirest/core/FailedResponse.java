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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A failed response you COULD return if you want to live in a house of lies.
 * This can be returned by a interceptor rather than throwing an exception.
 * It's possible if not handled correctly this could be more confusing than the exception
 */
public class FailedResponse<T> implements HttpResponse<T> {
    private final Exception failureReason;

    /**
     * Build a elaborate lie from a failure.
     * Just like what you're going to do at thanksgiving dinner.
     * @param e where it all went wrong.
     */
    public FailedResponse(Exception e) {
        this.failureReason = e;
    }

    /**
     * Returns a 542, which is nothing and a lie.
     * The remove server in this case returned nothing all all.
     * As far as we know you aren't even on the internet.
     * So we  made up this code, because a 500+ status is better than 0
     * @return 542
     */
    @Override
    public int getStatus() {
        return 542;
    }

    /**
     * a error message of the exception
     * @return a 'status' message
     */
    @Override
    public String getStatusText() {
        return failureReason.getMessage();
    }

    /**
     * @return a empty headers object because none was returned because there was no return
     */
    @Override
    public Headers getHeaders() {
        return new Headers();
    }

    /**
     * @return null, because there was no response
     */
    @Override
    public T getBody() {
        return null;
    }

    /**
     * @return a parsing exception with the exception.
     */
    @Override
    public Optional<UnirestParsingException> getParsingError() {
        return Optional.of(new UnirestParsingException(failureReason.getMessage(), failureReason));
    }

    /**
     * @param func a function to transform a body type to something else.
     * @param <V> always null
     * @return another object
     */
    @Override
    public <V> V mapBody(Function<T, V> func) {
        return func.apply(null);
    }

    /**
     * @param func a function to transform a body type to something else.
     * @param <V> always null
     * @return another response
     */
    @Override
    public <V> HttpResponse<V> map(Function<T, V> func) {
        return (HttpResponse<V>) func.apply(null);
    }

    /**
     * @param consumer a function to consume a successful HttpResponse.
     *                This is never called in this case.
     * @return this HttpResponse.
     */
    @Override
    public HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer) {
        return this;
    }

    /**
     * @param consumer a function to consume a failed HttpResponse
     *                 always called in this case
     * @return this HttpResponse
     */
    @Override
    public HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * @param errorClass the class to transform the body to. However as the body is null
     *                   in this case it will also be null
     * @param consumer a function to consume a failed HttpResponse
     *                 always called in this case
     * @return this HttpResponse
     */
    @Override
    public <E> HttpResponse<T> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer) {
        consumer.accept(null);
        return this;
    }

    /**
     * is this a success?  Obvs no!
     * @return false
     */
    @Override
    public boolean isSuccess() {
        return false;
    }

    /**
     * Map the body to an error object, however because the body in this case is always
     * null this will always return null
     * @param errorClass the class for the error
     * @param <E> the error type
     * @return null
     */
    @Override
    public <E> E mapError(Class<? extends E> errorClass) {
        return null;
    }

    @Override
    public Cookies getCookies() {
        return new Cookies();
    }

    @Override
    public HttpRequestSummary getRequestSummary() {
        return null;
    }

}
