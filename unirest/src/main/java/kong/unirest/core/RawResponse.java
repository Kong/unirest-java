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

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Raw Response represents the response before any mapping or consumption of the content.
 */
public interface RawResponse {
    /**
     * Returns the status code for this response.
     *
     * @return the response code
     */
    int getStatus();

    /**
     * Returns the status text for this response.
     *
     * @return the response text
     */
    String getStatusText();

    /**
     * Returns the received response headers.
     *
     * @return the response headers
     */
    Headers getHeaders();

    /**
     * Returns the body content of the response as a InputStream.
     * Like most InputStreams it can only be read once. If you read
     * the response though some other method like getContentAsBytes()
     * or getBodyAsString() it will read this method and consume the InputStream
     *
     * @return the content
     */
    InputStream getContent();

    /**
     * Returns the body as bytes. This consumes the entire InputStream.
     * Warning: Calling this on very large responses will place all data in
     * memory and could create OutOfMemory errors
     *
     * @return the content as bytes
     */
    byte[] getContentAsBytes();

    /**
     * Returns the body as UTF-8 String. This consumes the entire InputStream.
     * Warning: Calling this on very large responses will place all data in
     * memory and could create OutOfMemory errors
     *
     * Using this method with a binary response will make you sad
     *
     * @return the content as a UTF-8 String
     */
    String getContentAsString();

    /**
     * Returns the body as UTF-8 String. This consumes the entire InputStream.
     * Warning: Calling this on very large responses will place all data in
     * memory and could create OutOfMemory errors
     *
     * Using this method with a binary response will make you sad
     *
     * @param charset the charset for the String
     * @return the content as a string in the provided charset.
     */
    String getContentAsString(String charset);

    /**
     * Returns the body content of the response as a InputStreamReader.
     * Like most InputStreams it can only be read once. If you read
     * the response though some other method like getContentAsBytes()
     * or getBodyAsString() it will read this method and consume the InputStream
     *
     * @return the content
     */
    InputStreamReader getContentReader();

    /**
     * Indicates that the response has content
     * @return boolean indicating that the response has content.
     */
    boolean hasContent();

    /**
     * Returns the mime type of the response content as indicated by
     * the Content-Type header or a empty string if none is supplied
     * (e.g. application/json)
     * @return the Content-Type
     */
    String getContentType();

    /**
     * Returns the encoding of the response as indicated by the Content-Encoding header
     * or returns a empty string if none provided.
     *
     * @return the encoding
     */
    String getEncoding();

    /**
     * Returns the current config for this request/response
     * @return the config
     */
    Config getConfig();

    /**
     * returns a lightweight read only summary of the request.
     *
     * @return the request summary
     */
    HttpRequestSummary getRequestSummary();

    /**
     * returns a lightweight read only summary of the response.
     * @return the response summary
     */
    HttpResponseSummary toSummary();
}
