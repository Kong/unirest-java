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

import java.nio.charset.Charset;

public interface RequestBodyEntity extends HttpRequest<RequestBodyEntity>, Body {
    /**
     * Set a byte array as the body of the request
     * @param bodyBytes the byte[]
     * @return this request builder
     */
    RequestBodyEntity body(byte[] bodyBytes);

    /**
     * Set a String as the body of the request
     * @param bodyAsString the String
     * @return this request builder
     */
    RequestBodyEntity body(String bodyAsString);

    /**
     * Set JSON on the body
     * @param jsonBody the JsonNode
     * @return this request builder
     */
    RequestBodyEntity body(JsonNode jsonBody);

    /**
     * Set JSON on the body
     * @param body the JSONElement
     * @return this request builder
     */
    RequestBodyEntity body(JSONElement body);

    /**
     * Set a Object as the body of the request. This will be serialized with one of the following methods:
     *      - Strings are native
     *      - JSONElements use their native toString
     *      - Everything else will pass through the supplied ObjectMapper
     * @param body the Object
     * @return this request builder
     */
    RequestBodyEntity body(Object body);

    /**
     * Set the Charset encoding for the Content-Type. This is appended to the Content-Type Header
     * (e.g. application/x-www-form-urlencoded; charset=US-ASCII)
     * Default is UTF-8
     * @param charset the charset
     * @return this request builder
     */
    RequestBodyEntity charset(Charset charset);

    /**
     * Removes any Charset for the Content-Type for when servers cannot process it.
     * (e.g. application/x-www-form-urlencoded)
     * @return this request builder
     */
    default RequestBodyEntity noCharset() {
        return charset(null);
    }
}
