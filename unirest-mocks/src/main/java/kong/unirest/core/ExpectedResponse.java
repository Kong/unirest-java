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

public interface ExpectedResponse {
    /**
     * adds a header to the expected response
     * @param key the header key
     * @param key the header value
     * @return this ExpectedResponse
     */
    ExpectedResponse withHeader(String key, String value);

    /**
     * adds a collection of headers to the expected response
     * @param headers the headers
     * @return This ExpectedResponse
     */
    ExpectedResponse withHeaders(Headers headers);

    /**
     * sets the status of the expected response
     * @param httpStatus the http status code
     * @return this ExpectedResponse
     */
    ExpectedResponse withStatus(int httpStatus);

    /**
     * sets the status of the expected response
     * @param httpStatus the http status code
     * @param statusMessage the status message
     * @return this ExpectedResponse
     */
    ExpectedResponse withStatus(int httpStatus, String statusMessage);

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

    /**
     * A supplier for the expected body which will get invoked at the time of build the response.
     * @param supplier the expected response body supplier
     * @return The ExpectedResponse
     */
    ExpectedResponse thenReturn(Supplier<String> supplier);
}
