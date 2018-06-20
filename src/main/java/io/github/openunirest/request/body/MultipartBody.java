/*
The MIT License

Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.openunirest.request.body;

import io.github.openunirest.http.utils.MapUtil;
import io.github.openunirest.request.BaseRequest;
import io.github.openunirest.request.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MultipartBody extends BaseRequest implements Body {
    private List<FormPart> parameters = new ArrayList<>();

    private boolean hasFile;
    private HttpRequest httpRequestObj;
    private HttpMultipartMode mode;

    public MultipartBody(HttpRequest httpRequest) {
        super(httpRequest);
        this.httpRequestObj = httpRequest;
    }

    public MultipartBody field(String name, String value) {
        return field(name, value, false, null);
    }

    public MultipartBody field(String name, String value, String contentType) {
        return field(name, value, false, contentType);
    }

    public MultipartBody field(String name, Collection<?> collection) {
        for (Object current : collection) {
            boolean isFile = current instanceof File;
            field(name, current, isFile, null);
        }
        return this;
    }

    private MultipartBody field(String name, Object value, boolean file, String contentType) {
        ContentType type = null;
        if (contentType != null && contentType.length() > 0) {
            type = ContentType.parse(contentType);
        } else if (file) {
            type = ContentType.APPLICATION_OCTET_STREAM;
        } else {
            type = ContentType.APPLICATION_FORM_URLENCODED.withCharset(UTF_8);
        }

        addPart(name, value, type);

        toggleFile(file);

        return this;
    }

    public MultipartBody field(String name, Object value, ContentType contentType) {
        addPart(name, value, contentType);
        toggleFile(true);
        return this;
    }

    private void toggleFile(boolean file) {
        if (!hasFile && file) {
            hasFile = true;
        }
    }

    private void addPart(String name, Object value, ContentType type) {
        parameters.add(new FormPart(name, value, type));
        Collections.sort(parameters);
    }

    public MultipartBody field(String name, File file) {
        return field(name, file, true, null);
    }

    public MultipartBody field(String name, File file, String contentType) {
        return field(name, file, true, contentType);
    }

    public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
        return field(name, new InputStreamBody(stream, contentType, fileName), true, contentType.getMimeType());
    }

    public MultipartBody field(String name, InputStream stream, String fileName) {
        return field(name, new InputStreamBody(stream, ContentType.APPLICATION_OCTET_STREAM, fileName), true, ContentType.APPLICATION_OCTET_STREAM.getMimeType());
    }

    public MultipartBody field(String name, byte[] bytes, ContentType contentType, String fileName) {
        return field(name, new ByteArrayBody(bytes, contentType, fileName), true, contentType.getMimeType());
    }

    public MultipartBody field(String name, byte[] bytes, String fileName) {
        return field(name, new ByteArrayBody(bytes, ContentType.APPLICATION_OCTET_STREAM, fileName), true, ContentType.APPLICATION_OCTET_STREAM.getMimeType());
    }

    public MultipartBody basicAuth(String username, String password) {
        httpRequestObj.basicAuth(username, password);
        return this;
    }

    public MultipartBody mode(String value) {
        this.mode = HttpMultipartMode.valueOf(value);
        return this;
    }

    public HttpEntity getEntity() {
        if (hasFile) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            if (mode != null) {
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            }
            for (FormPart key : parameters) {
                builder.addPart(key.getName(), key.toApachePart());
            }
            return builder.build();
        } else {
            return new UrlEncodedFormEntity(MapUtil.getList(parameters), UTF_8);
        }
    }

}
