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

/**
 * A Mock Response that can be used in testing.
 * @param <T> the body type
 */
public class MockResponse<T> extends BaseResponse<T> {
    private final MockConfig mockConfig;
    private Optional<UnirestParsingException> ex = Optional.empty();
    private final T body;

    /**
     * Construct a mock Response
     * @param status the status of the response
     * @param statusText the status text
     * @param body the body
     */
    public MockResponse(int status, String statusText, T body){
        this(status, statusText, body, new MockConfig());
    }

    /**
     * Construct a mock Response
     * @param status the status of the response
     * @param statusText the status text
     * @param body the body
     * @param config a mockConfig for post-processing options
     */
    public MockResponse(int status, String statusText, T body, MockConfig config){
        super(new MockRawResponse(
                "", new Headers(), status, statusText, config, null
        ));
        this.mockConfig = config;
        this.body = body;
    }

    /**
     * Construct a simple successful (200 ok) response with a body
     * @param body the body
     * @param <T> the type of body
     * @return a MockResponse;
     */
    public static <T> MockResponse<T> ok(T body) {
        return new MockResponse<>(200, "ok", body);
    }

    /**
     * Construct a simple failed (400 bad request) response with a body
     * @param body the body
     * @param <T> the type of body
     * @return a MockResponse;
     */
    public static <T> MockResponse<T> bad(T body) {
        return new MockResponse<>(400, "bad request", body);
    }

    /**
     * Construct a response with a status and body. The status text is just the string of the status
     * @param status the status
     * @param body the body
     * @param <T> the type of body
     * @return a MockResponse;
     */
    public static <T> MockResponse<T> of(int status, T body) {
        return new MockResponse<>(status, String.valueOf(status), body);
    }

    /**
     * Construct a response with a status and body. The status text is just the string of the status
     * @param status the status
     * @param statusText the status text
     * @param body the body
     * @param <T> the type of body
     * @return a MockResponse;
     */
    public static <T> MockResponse<T> of(int status, String statusText, T body) {
        return new MockResponse<>(status, statusText, body);
    }

    /**
     * get the MockConfig for this MockResponse
     * @return the config
     */
    public MockConfig config(){
        return mockConfig;
    }


    @Override
    public T getBody() {
        return body;
    }

    @Override
    protected String getRawBody() {
        return String.valueOf(body);
    }

    @Override
    public Optional<UnirestParsingException> getParsingError() {
        return ex;
    }

    /**
     * add a header value to the response
     * @param key the header key
     * @param value the header value
     * @return this MockResponse
     */
    public MockResponse<T> withHeader(String key, String value) {
        getHeaders().add(key, value);
        return this;
    }

    /**
     * Flag that there was a post-processing parsing error with the object Mapper
     * this jams the body as a string into the parsing exception and sets a generic oops exception
     * @return this MockResponse
     */
    public MockResponse<T> failedToParse() {
        return failedToParse(new Exception("oops"), String.valueOf(body));
    }

    /**
     * Flag that there was a post-processing parsing error with the object Mapper
     * @param e the exception thrown
     * @param originalBody the original body before the object mapper got involved.
     * @return this MockResponse
     */
    public MockResponse<T> failedToParse(Exception e, String originalBody) {
        ex = Optional.of(new UnirestParsingException(originalBody, e));
        return this;
    }

    /**
     * Set some options on the current MockConfig.
     * @param c a Consumer with options to set on the config
     * @return this MockResponse
     */
    public MockResponse<T> withConfigOptions(Consumer<MockConfig> c) {
        c.accept(mockConfig);
        return this;
    }
}
