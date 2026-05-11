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

import kong.unirest.core.json.JSONElement;

import java.util.function.Supplier;

/**
 * Represents the expected response configuration for a mock HTTP request.
 * <p>
 * This interface provides a fluent API for configuring mock responses including
 * HTTP status codes, headers, and response bodies. It is typically obtained through
 * {@link Expectation#thenReturn()} or {@link MockClient#defaultResponse()}.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Configure a mock response with status, headers, and body
 * mockClient.expect(HttpMethod.GET, "/api/users")
 *     .thenReturn()
 *     .withStatus(200)
 *     .withHeader("Content-Type", "application/json")
 *     .thenReturn("[{\"id\": 1, \"name\": \"John\"}]");
 *
 * // Create a standalone expected response
 * ExpectedResponse response = ExpectedResponse.of(404)
 *     .thenReturn("Not found");
 * }</pre>
 *
 * @see MockClient
 * @see Expectation
 * @see Times
 */
public interface ExpectedResponse {

    /**
     * Creates an independent expected response with the specified status code.
     * <p>
     * This factory method is useful for creating test doubles rather than strict mocks,
     * allowing you to create reusable response configurations outside of the mock client.
     * </p>
     *
     * @param status the HTTP response status code
     * @return a new ExpectedResponse configured with the specified status
     */
    static ExpectedResponse of(int status) {
        return new ExpectedResponseRecord(null).withStatus(status);
    }

    /**
     * Adds a header to the expected response.
     *
     * @param key   the header name
     * @param value the header value
     * @return this ExpectedResponse for method chaining
     */
    ExpectedResponse withHeader(String key, String value);

    /**
     * Adds a collection of headers to the expected response.
     *
     * @param headers the {@link Headers} object containing the headers to add
     * @return this ExpectedResponse for method chaining
     */
    ExpectedResponse withHeaders(Headers headers);

    /**
     * Sets the HTTP status code of the expected response.
     * <p>
     * The status message will be set to a default value based on the status code.
     * </p>
     *
     * @param httpStatus the HTTP status code (e.g., 200, 404, 500)
     * @return this ExpectedResponse for method chaining
     */
    ExpectedResponse withStatus(int httpStatus);

    /**
     * Sets the HTTP status code and status message of the expected response.
     *
     * @param httpStatus    the HTTP status code (e.g., 200, 404, 500)
     * @param statusMessage the status message (e.g., "OK", "Not Found", "Internal Server Error")
     * @return this ExpectedResponse for method chaining
     */
    ExpectedResponse withStatus(int httpStatus, String statusMessage);

    /**
     * Sets the response body as a string.
     *
     * @param body the response body content
     * @return this ExpectedResponse for method chaining
     */
    ExpectedResponse thenReturn(String body);

    /**
     * Sets the response body as a JSON element.
     * <p>
     * The JSON element will be serialized to a string when the response is built.
     * </p>
     *
     * @param jsonObject the JSON element ({@link kong.unirest.core.json.JSONObject} or {@link kong.unirest.core.json.JSONArray})
     * @return this ExpectedResponse for method chaining
     */
    ExpectedResponse thenReturn(JSONElement jsonObject);

    /**
     * Sets the response body as a POJO object.
     * <p>
     * The object will be serialized using the configured object mapper when the response is built.
     * </p>
     *
     * @param pojo the object to serialize as the response body
     * @return this ExpectedResponse for method chaining
     */
    ExpectedResponse thenReturn(Object pojo);

    /**
     * Sets a supplier for the response body.
     * <p>
     * The supplier will be invoked at the time the response is built, allowing for
     * dynamic response generation. This is useful when the response content needs
     * to be determined at request time rather than at setup time.
     * </p>
     *
     * @param supplier a supplier that provides the response body string
     * @return this ExpectedResponse for method chaining
     */
    ExpectedResponse thenReturn(Supplier<String> supplier);

    /**
     * Verifies that the expectation associated with this response was fulfilled at least once.
     * <p>
     * This is equivalent to calling {@code verify(Times.atLeastOnce())}.
     * </p>
     *
     * @throws UnirestAssertion if the expectation was not fulfilled
     */
    void verify();

    /**
     * Verifies that the expectation associated with this response was fulfilled the specified number of times.
     *
     * @param times the expected invocation count (e.g., {@link Times#exactlyOnce()}, {@link Times#never()})
     * @throws UnirestAssertion if the expectation was not fulfilled the expected number of times
     * @see Times
     */
    void verify(Times times);
}
