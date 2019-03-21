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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

public interface HttpRequestWithBody extends HttpRequest<HttpRequestWithBody> {
    HttpRequestWithBody charset(Charset charset);

    /**
     * Forces the request to send as multipart even if all params are simple
     * @return The same MultipartBody
     */
    MultipartBody multiPartContent();

    MultipartBody field(String name, Collection<?> value);

    MultipartBody field(String name, File file);

    MultipartBody field(String name, File file, String contentType);

    MultipartBody field(String name, Object value);

    MultipartBody field(String name, Object value, String contentType);

    MultipartBody fields(Map<String, Object> parameters);

    MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName);

    MultipartBody field(String name, InputStream stream, String fileName);

    RequestBodyEntity body(JsonNode body);

    RequestBodyEntity body(String body);

    RequestBodyEntity body(Object body);

    RequestBodyEntity body(byte[] body);

    RequestBodyEntity body(JSONObject body);

    RequestBodyEntity body(JSONArray body);

    Charset getCharset();
}
