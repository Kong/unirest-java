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

import org.apache.http.HttpHeaders;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

class HttpRequestMultiPart extends BaseRequest<MultipartBody> implements MultipartBody {
    private List<BodyPart> parameters = new ArrayList<>();

    private MultipartMode mode = MultipartMode.BROWSER_COMPATIBLE;
    private Charset charSet;
    private boolean forceMulti = false;
    private ProgressMonitor monitor;

    HttpRequestMultiPart(HttpRequestBody httpRequest) {
        super(httpRequest);
        this.charSet = httpRequest.getCharset();
    }

    @Override
    public MultipartBody field(String name, String value) {
        addPart(new ParamPart(name, value));
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
            addPart(name, current, null);
        }
        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream value, ContentType contentType) {
        addPart(new InputStreamPart(name, value, contentType.toString()));
        return this;
    }

    @Override
    public MultipartBody field(String name, File file) {
        addPart(new FilePart(file, name));
        return this;
    }

    @Override
    public MultipartBody field(String name, File file, String contentType) {
        addPart(new FilePart(file, name, contentType));
        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
        addPart(new InputStreamPart(name, stream, contentType.toString(), fileName));

        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream stream, String fileName) {
        addPart(new InputStreamPart(name, stream, ContentType.APPLICATION_OCTET_STREAM.toString(), fileName));
        return this;
    }

    @Override
    public MultipartBody field(String name, byte[] bytes, ContentType contentType, String fileName) {
        addPart(new ByteArrayPart(name, bytes, contentType, fileName));
        return this;
    }

    @Override
    public MultipartBody field(String name, byte[] bytes, String fileName) {
        addPart(new ByteArrayPart(name, bytes, ContentType.APPLICATION_OCTET_STREAM, fileName));
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
    public MultipartBody mode(MultipartMode value) {
        this.mode = value;
        return this;
    }

    @Override
    public MultipartBody uploadMonitor(ProgressMonitor uploadMonitor) {
        this.monitor  = uploadMonitor;
        return this;
    }

    @Override
    public Charset getCharset() {
        return this.charSet;
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

    public MultipartBody field(String name, Object value, String contentType) {
        addPart(name, value, contentType);
        return this;
    }

    private void addPart(String name, Object value, String contentType) {
        if(value instanceof InputStream){
            addPart(new InputStreamPart(name, (InputStream)value, contentType));
        } else if (value instanceof File) {
            addPart(new FilePart((File)value, name, contentType));
        } else {
            addPart(new ParamPart(name, Util.nullToEmpty(value), contentType));
        }
    }

    private void addPart(BodyPart value) {
        parameters.add(value);
        Collections.sort(parameters);
    }

    @Override
    public Optional<Body> getBody() {
        return Optional.of(this);
    }

    @Override
    public boolean isMultiPart() {
        return forceMulti || multiParts().stream().anyMatch(BodyPart::isFile);
    }

    @Override
    public boolean isEntityBody() {
        return false;
    }

    @Override
    public Collection<BodyPart> multiParts() {
        return new ArrayList<>(parameters);
    }

    @Override
    public MultipartMode getMode() {
        return mode;
    }

    @Override
    public ProgressMonitor getMonitor() {
        return monitor;
    }


    MultipartBody forceMultiPart(boolean value) {
        forceMulti = value;
        return this;
    }
}
