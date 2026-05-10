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

/**
 * Abstract base implementation of {@link HttpResponse} that provides common functionality
 * for handling HTTP responses.
 * <p>
 * This class handles the core response data including status code, status text, headers,
 * and cookies. It also provides utility methods for success/failure handling, body mapping,
 * and error parsing.
 * <p>
 * Subclasses must implement {@link #getBody()} and {@link #getRawBody()} to provide
 * the actual response body content.
 *
 * @param <T> the type of the response body
 */
abstract class BaseResponse<T> implements HttpResponse<T> {

    private final Headers headers;
    private final String statusText;
    private final int statusCode;
    private final HttpRequestSummary reqSummary;
    private Optional<UnirestParsingException> parsingerror = Optional.empty();
    private final Config config;
    private Cookies cookies;


    /**
     * Constructs a new BaseResponse from a raw HTTP response.
     * <p>
     * This constructor extracts headers, status code, status text, and configuration
     * from the raw response. It also removes the "Content-Encoding: gzip" header
     * since Unirest automatically decompresses the content.
     *
     * @param response the raw HTTP response to extract data from
     */
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

    /**
     * Copy constructor that creates a new BaseResponse from another BaseResponse.
     * <p>
     * This is used internally when mapping responses to new types.
     *
     * @param other the BaseResponse to copy from
     */
    protected BaseResponse(BaseResponse other) {
        this.headers = other.headers;
        this.statusCode = other.statusCode;
        this.statusText = other.statusText;
        this.config = other.config;
        this.reqSummary = other.reqSummary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return statusCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStatusText() {
        return statusText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Headers getHeaders() {
        return headers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract T getBody();

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UnirestParsingException> getParsingError() {
        return parsingerror;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> V mapBody(Function<T, V> func) {
        return func.apply(getBody());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> HttpResponse<V> map(Function<T, V> func) {
        return new BasicResponse(this, mapBody(func));
    }

    /**
     * Sets a parsing exception that occurred while processing the response body.
     *
     * @param originalBody the original body content that failed to parse
     * @param e the exception that was thrown during parsing
     */
    protected void setParsingException(String originalBody, RuntimeException e) {
        parsingerror = Optional.of(new UnirestParsingException(originalBody, e));
    }

    /**
     * {@inheritDoc}
     * <p>
     * A response is considered successful if the status code is in the 2xx range
     * (200-299) and no parsing error occurred.
     */
    @Override
    public boolean isSuccess() {
        return getStatus() >= 200 && getStatus() < 300 && !getParsingError().isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer) {
        if (isSuccess()) {
            consumer.accept(this);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer) {
        if (!isSuccess()) {
            consumer.accept(this);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Retrieves the error body as a string for error mapping purposes.
     * <p>
     * This method attempts to get the error body from multiple sources:
     * <ol>
     *   <li>From a parsing exception's original body, if present</li>
     *   <li>From the raw body string, if available</li>
     *   <li>By serializing the body object back to a string</li>
     * </ol>
     *
     * @return the error body as a string, or {@code null} if no body is available
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     * <p>
     * Cookies are lazily parsed from the "set-cookie" headers on first access.
     */
    @Override
    public Cookies getCookies() {
        if (cookies == null) {
            cookies = new Cookies(headers.get("set-cookie"));
        }
        return cookies;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestSummary getRequestSummary() {
        return reqSummary;
    }

    /**
     * Returns the raw response body as a string.
     * <p>
     * This method is used internally for error body retrieval when the parsed
     * body is not available or appropriate.
     *
     * @return the raw response body as a string, or {@code null} if not available
     */
    protected abstract String getRawBody();
}
