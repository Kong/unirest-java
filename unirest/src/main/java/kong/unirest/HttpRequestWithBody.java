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

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

/**
 * A request Builder for POST and PUT operations with a body.
 * will switch to a MultipartBody once http form variables are introduced.
 * or to a RequestBodyEntity
 */
public interface HttpRequestWithBody extends HttpRequest<HttpRequestWithBody> {
    /**
     * Forces the request to send as multipart even if all params are simple
     * @return The same MultipartBody
     */
    MultipartBody multiPartContent();

    /**
     * Sets a field param on the body.
     * @param name the name of the field
     * @param value a values
     * @return this request builder
     */
    MultipartBody field(String name, Object value);

    /**
     * Sets multiple field params on the body each with the same name.
     * @param name the name of the field
     * @param value a Collection of values
     * @return this request builder
     */
    MultipartBody field(String name, Collection<?> value);

    /**
     * Sets a field param on the body with a specified content-type.
     * @param name the name of the field
     * @param value the object
     * @param contentType contentType (i.e. application/xml)
     * @return this request builder
     */
    MultipartBody field(String name, Object value, String contentType);

    /**
     * Sets multiple field params on the body from a map of key/value pairs.
     * @param parameters the map of field params
     * @return this request builder
     */
    MultipartBody fields(Map<String, Object> parameters);

    /**
     * Sets a File on the body.
     * @param name the name of the file field
     * @param file the file
     * @return this request builder
     */
    MultipartBody field(String name, File file);

    /**
     * Sets a File on the body with a specified content-type.
     * @param name the name of the file field
     * @param file the file
     * @param contentType contentType (i.e. image/png)
     * @return this request builder
     */
    MultipartBody field(String name, File file, String contentType);

    /**
     * Sets a File on the body from a raw InputStream requires a file name.
     * @param name the name of the file field
     * @param stream the inputStream
     * @param fileName the name for the file
     * @return this request builder
     */
    MultipartBody field(String name, InputStream stream, String fileName);

    /**
     * Sets a File on the body from a raw InputStream requires a specified content-type and file name.
     * @param name the name of the file field
     * @param stream the inputStream
     * @param contentType contentType (i.e. image/png)
     * @param fileName the name for the file
     * @return this request builder
     */
    MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName);


    /**
     * Set the Charset encoding for the Content-Type. This is appended to the Content-Type Header
     * (e.g. application/x-www-form-urlencoded; charset=US-ASCII)
     * Default is UTF-8
     * @param charset the charset
     * @return this request builder
     */
    HttpRequestWithBody charset(Charset charset);

    /**
     * Removes any Charset for the Content-Type for when servers cannot process it.
     * (e.g. application/x-www-form-urlencoded)
     * @return this request builder
     */
    default HttpRequestWithBody noCharset() {
        return charset(null);
    }

    /**
     * Set a String as the body of the request
     * @param body the String
     * @return this request builder
     */
    RequestBodyEntity body(String body);

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
     * Set a byte array as the body of the request
     * @param body the byte[]
     * @return this request builder
     */
    RequestBodyEntity body(byte[] body);

    /**
     * Set JSON on the body
     * @param body the JsonNode
     * @return this request builder
     */
    RequestBodyEntity body(JsonNode body);

    /**
     * Set JSON on the body
     * @param body the JSONElement
     * @return this request builder
     */
    RequestBodyEntity body(JSONElement body);

    /**
     * get the current default charset
     * @return the Charset
     */
    Charset getCharset();

    /**
     * @param type The content mime type
     * @return this request builder
     */
    HttpRequestWithBody contentType(String type);
}
