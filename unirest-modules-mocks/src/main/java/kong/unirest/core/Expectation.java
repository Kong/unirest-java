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

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an expectation for a particular HTTP method and path in the mock client.
 * <p>
 * An Expectation defines the criteria that an incoming request must match,
 * as well as the response that should be returned when a match occurs.
 * Expectations are created via {@link MockClient#expect(HttpMethod, String)} and support
 * a fluent API for configuring matching criteria and responses.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Set up an expectation with request matching and response
 * mockClient.expect(HttpMethod.POST, "/api/users")
 *     .header("Content-Type", "application/json")
 *     .queryString("active", "true")
 *     .body("{\"name\":\"John\"}")
 *     .thenReturn("{\"id\":1,\"name\":\"John\"}")
 *     .withStatus(201);
 *
 * // Verify the expectation was met
 * mockClient.expect(HttpMethod.POST, "/api/users")
 *     .verify(Times.exactlyOnce());
 * }</pre>
 *
 * @see MockClient
 * @see ExpectedResponse
 * @see Times
 * @see BodyMatcher
 */
public interface Expectation {

    /**
     * Adds an expected header that must be present in the request.
     *
     * @param key   the header name
     * @param value the expected header value
     * @return this Expectation for method chaining
     */
    Expectation header(String key, String value);

    /**
     * Adds an expected query string parameter that must be present in the request URL.
     *
     * @param key   the query parameter name
     * @param value the expected query parameter value
     * @return this Expectation for method chaining
     */
    Expectation queryString(String key, String value);

    /**
     * Sets the expected request body using exact string matching.
     *
     * @param body the expected body content
     * @return this Expectation for method chaining
     */
    Expectation body(String body);

    /**
     * Sets a custom body matcher for flexible request body matching.
     * <p>
     * Use this method when you need more sophisticated body matching logic,
     * such as JSON comparison, regex matching, or partial content matching.
     * </p>
     *
     * @param matcher the {@link BodyMatcher} implementation to use
     * @return this Expectation for method chaining
     * @see EqualsBodyMatcher
     */
    Expectation body(BodyMatcher matcher);

    /**
     * Configures this expectation to return an empty response.
     * <p>
     * Use the returned {@link ExpectedResponse} to further configure
     * the response status and headers.
     * </p>
     *
     * @return an {@link ExpectedResponse} for configuring the response details
     */
    ExpectedResponse thenReturn();

    /**
     * Configures this expectation to return a response with the specified string body.
     *
     * @param body the response body content
     * @return an {@link ExpectedResponse} for configuring additional response details
     */
    ExpectedResponse thenReturn(String body);

    /**
     * Configures this expectation to return a response with a JSON body.
     * <p>
     * The JSON element will be serialized to a string when the response is built.
     * </p>
     *
     * @param jsonObject the JSON element ({@link kong.unirest.core.json.JSONObject} or {@link kong.unirest.core.json.JSONArray})
     * @return an {@link ExpectedResponse} for configuring additional response details
     */
    ExpectedResponse thenReturn(JSONElement jsonObject);

    /**
     * Configures this expectation to return a response with a serialized POJO body.
     * <p>
     * The object will be serialized using the configured object mapper.
     * </p>
     *
     * @param pojo the object to serialize as the response body
     * @return an {@link ExpectedResponse} for configuring additional response details
     */
    ExpectedResponse thenReturn(Object pojo);

    /**
     * Configures this expectation with a dynamic response body supplier.
     * <p>
     * The supplier is invoked at response time, allowing for dynamic response generation.
     * </p>
     *
     * @param supplier a supplier that provides the response body string
     * @return an {@link ExpectedResponse} for configuring additional response details
     */
    ExpectedResponse thenReturn(Supplier<String> supplier);

    /**
     * Configures this expectation with a function that generates the response based on the request.
     * <p>
     * This allows for full control over response generation based on request details,
     * making it useful for building more complex test doubles that implement service logic.
     * </p>
     *
     * @param fun a function that takes the incoming {@link HttpRequest} and returns an {@link ExpectedResponse}
     */
    void thenReturn(Function<HttpRequest<?>, ExpectedResponse> fun);

    /**
     * Verifies that this expectation was fulfilled at least once.
     * <p>
     * This is equivalent to calling {@code verify(Times.atLeastOnce())}.
     * </p>
     *
     * @throws UnirestAssertion if the expectation was not fulfilled
     */
    void verify();

    /**
     * Verifies that this expectation was fulfilled the specified number of times.
     *
     * @param times the expected invocation count
     * @throws UnirestAssertion if the expectation was not fulfilled the expected number of times
     * @see Times
     */
    void verify(Times times);

    /**
     * Sets the expected number of times this expectation should be invoked.
     * <p>
     * This can be used during setup to define how many times a request is expected,
     * and then verified later with {@link MockClient#verifyAll()}.
     * </p>
     *
     * @param times the expected invocation count
     * @return this Expectation for method chaining
     * @see Times#never()
     * @see Times#exactlyOnce()
     * @see Times#exactly(int)
     */
    Expectation times(Times times);
}
