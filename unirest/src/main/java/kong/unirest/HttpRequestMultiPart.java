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

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

class HttpRequestMultiPart extends BaseRequest<MultipartBody> implements MultipartBody {
    private List<FormPart> parameters = new ArrayList<>();

    private HttpMultipartMode mode = HttpMultipartMode.BROWSER_COMPATIBLE;
    private Charset charSet;

    HttpRequestMultiPart(HttpRequestBody httpRequest) {
        super(httpRequest);
        this.charSet = httpRequest.getCharset();
    }

    @Override
    public MultipartBody field(String name, String value) {
        addPart(name, value);
        return this;
    }

    @Override
    public MultipartBody field(String name, String value, String contentType) {
        addPart(name, value, contentType);
        return this;
    }

    @Override
    public MultipartBody field(String name, Collection<?> collection) {
        for (Object current: collection) {
            addPart(name, current, (String)null);
        }
        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream value, ContentType contentType) {
        addPart(name, new InputStreamPart(value, contentType.toString()), contentType);
        return this;
    }

    @Override
    public MultipartBody field(String name, File file) {
        addPart(name, file);
        return this;
    }

    @Override
    public MultipartBody field(String name, File file, String contentType) {
        addPart(name, file, contentType);
        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
        addPart(name, new InputStreamPart(stream, contentType.toString(), fileName), contentType);

        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream stream, String fileName) {
        addPart(name, new InputStreamPart(stream, ContentType.APPLICATION_OCTET_STREAM.toString(), fileName), ContentType.APPLICATION_OCTET_STREAM);

        return this;
    }

    @Override
    public MultipartBody field(String name, byte[] bytes, ContentType contentType, String fileName) {
        addPart(name, new ByteArrayPart(bytes, contentType, fileName), contentType);

        return this;
    }

    @Override
    public MultipartBody field(String name, byte[] bytes, String fileName) {
        addPart(name, new ByteArrayPart(bytes, ContentType.APPLICATION_OCTET_STREAM, fileName), ContentType.APPLICATION_OCTET_STREAM);

        return this;
    }

    @Override
    public MultipartBody charset(Charset charset) {
        this.charSet = charset;
        return this;
    }

    @Override
    public MultipartBody contentType(String mimeType) {
        header(HttpHeaders.CONTENT_TYPE, mimeType);
        return this;
    }

    @Override
    public MultipartBody mode(String value) {
        this.mode = HttpMultipartMode.valueOf(value);
        return this;
    }

    @Override
    public MultipartBody mode(HttpMultipartMode value) {
        this.mode = value;
        return this;
    }

    public MultipartBody fields(Map<String, Object> fields) {
        if (fields != null) {
            for (Map.Entry<String, Object> param : fields.entrySet()) {
                if (param.getValue() instanceof File) {
                    field(param.getKey(), (File) param.getValue());
                } else {
                    field(param.getKey(), Util.nullToEmpty(param.getValue()));
                }
            }
        }
        return this;
    }

    @Override
    public HttpEntity getEntity() {
        if (parameters.stream().anyMatch(FormPart::isFile)) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(charSet);
            builder.setMode(mode);
            for (FormPart key : parameters) {
                builder.addPart(key.getName(), key.toApachePart());
            }
            return builder.build();
        } else {
            return new UrlEncodedFormEntity(Util.getList(parameters), charSet);
        }
    }

    @Override
    public Body getBody() {
        return this;
    }

    private void addPart(String name, Object value, ContentType type) {
        addPart(name, value, type.toString());
    }

    private void addPart(String name, Object value, String type) {
        parameters.add(new FormPart(name, value, type));
        Collections.sort(parameters);
    }

    private void addPart(String name, Object value) {
        addPart(name, value, (String)null);
    }

}
