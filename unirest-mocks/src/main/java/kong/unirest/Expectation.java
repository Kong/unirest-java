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

import kong.unirest.json.JSONElement;

/**
 * A expectation for a particular method/path
 */
public interface Expectation {
    /**
     * A expected header for a request
     * @param key the header key
     * @param value the header value
     * @return this Expectation
     */
    Expectation header(String key, String value);

    /**
     * A expected header for a request
     * @param key the query key
     * @param value the query value
     * @return this Expectation
     */
    Expectation queryString(String key, String value);

    /**
     * A expected body for a request
     * @param body the expected body
     * @return this Expectation
     */
    Expectation body(String body);

    /**
     * A matcher for the body for a request
     * @param matcher the matcher
     * @return this Expectation
     */
    Expectation body(BodyMatcher matcher);

    /**
     * expect a null response
     * @return The ExpectedResponse
     */
    ExpectedResponse thenReturn();

    /**
     * expect a string response
     * @param body the expected response body
     * @return The ExpectedResponse
     */
    ExpectedResponse thenReturn(String body);

    /**
     * expect a json response
     * @param jsonObject the expected response body
     * @return The ExpectedResponse
     */
    ExpectedResponse thenReturn(JSONElement jsonObject);

    /**
     * expect a object response as defined by a pojo using the requests / configuration object mapper
     * @param pojo the expected response body
     * @return The ExpectedResponse
     */
    ExpectedResponse thenReturn(Object pojo);
}
