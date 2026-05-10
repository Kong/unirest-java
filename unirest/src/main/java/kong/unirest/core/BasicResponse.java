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

/**
 * A basic implementation of {@link HttpResponse} that holds a pre-parsed response body.
 * <p>
 * This class wraps an HTTP response along with its parsed body content. It is typically
 * used internally by Unirest after transforming the raw response into the desired type.
 *
 * @param <T> the type of the response body
 * @see BaseResponse
 * @see HttpResponse
 */
public class BasicResponse<T> extends BaseResponse<T> {
    private final T body;

    /**
     * Creates a new BasicResponse by copying metadata from another response.
     *
     * @param response the source response to copy metadata from (status, headers, etc.)
     * @param body the parsed response body
     */
    BasicResponse(BaseResponse response, T body) {
        super(response);
        this.body = body;
    }

    /**
     * Creates a new BasicResponse from a raw HTTP response with a parsed body.
     *
     * @param httpResponse the raw HTTP response containing status, headers, and other metadata
     * @param body the parsed response body
     */
    public BasicResponse(RawResponse httpResponse, T body) {
        super(httpResponse);
        this.body = body;
    }

    /**
     * Creates a new BasicResponse from a raw HTTP response with no body.
     * <p>
     * This constructor is useful for responses that have no content or when
     * the body is not needed.
     *
     * @param httpResponse the raw HTTP response containing status, headers, and other metadata
     */
    public BasicResponse(RawResponse httpResponse) {
        super(httpResponse);
        this.body = null;
    }

    /**
     * Creates a new BasicResponse that captures a parsing exception.
     * <p>
     * This constructor is used when the response body could not be parsed into the
     * expected type. The original body content and exception are preserved for
     * debugging and error handling.
     *
     * @param httpResponse the raw HTTP response
     * @param ogBody the original response body as a string before parsing failed
     * @param ex the exception that occurred during parsing
     */
    public BasicResponse(RawResponse httpResponse, String ogBody, RuntimeException ex) {
        this(httpResponse, null);
        setParsingException(ogBody, ex);
    }

    /**
     * Returns the parsed response body.
     *
     * @return the response body, or {@code null} if no body was set or parsing failed
     */
    @Override
    public T getBody() {
        return body;
    }

    /**
     * Returns the raw unparsed response body.
     * <p>
     * This implementation always returns {@code null} as the raw body is not
     * retained after parsing. To access the original body when a parsing error
     * occurs, use {@link #getParsingError()}.
     *
     * @return always {@code null}
     */
    @Override
    protected String getRawBody() {
        return null;
    }
}
