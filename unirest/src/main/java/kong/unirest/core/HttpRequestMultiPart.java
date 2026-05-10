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

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import static kong.unirest.core.MultiPartBuilder.named;

class HttpRequestMultiPart extends BaseRequest<MultipartBody> implements MultipartBody {
    private List<BodyPart> parameters = new ArrayList<>();

    private MultipartMode mode = MultipartMode.BROWSER_COMPATIBLE;
    private Charset charSet;
    private boolean forceMulti = false;
    private ProgressMonitor monitor;
    private String boundary;

    HttpRequestMultiPart(HttpRequestBody httpRequest) {
        super(httpRequest);
        this.charSet = httpRequest.getCharset();
    }

    HttpRequestMultiPart(HttpRequestMultiPart httpRequest) {
        super(httpRequest);
        this.charSet = httpRequest.getCharset();
        this.parameters = httpRequest.parameters;
        this.forceMulti = httpRequest.forceMulti;
        this.monitor = httpRequest.monitor;
        this.boundary = httpRequest.boundary;
    }

    @Override
    public MultipartBody field(MultiPartBuilder builder) {
        parameters.add(builder.toBodyPart());
        return this;
    }

    @Override
    public MultipartBody field(String name, String value) {
        field(MultiPartBuilder.named(name).value(value));
        return this;
    }

    @Override
    public MultipartBody field(String name, String value, String contentType) {
        field(named(name).value((Object) value).contentType(contentType));
        return this;
    }

    @Override
    public MultipartBody field(String name, String value, ContentType contentType) {
        Objects.requireNonNull(contentType, "contentType is required");
        field(named(name).value((Object) value).contentType(contentType.getMimeType()));
        return this;
    }

    @Override
    public MultipartBody field(String name, Collection<?> collection) {
        for (Object current: collection) {
            field(named(name).value(current));
        }
        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream value, ContentType contentType) {
        return field(named(name).value(value).contentType(contentType));
    }

    @Override
    public MultipartBody field(String name, File file) {
        return field(named(name).value(file));
    }

    @Override
    public MultipartBody field(String name, File file, String contentType) {
        return field(named(name).value(file).contentType(contentType));
    }

    @Override
    public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
        return field(named(name).value(stream).contentType(contentType).fileName(fileName));
    }

    @Override
    public MultipartBody field(String name, InputStream stream, String fileName) {
        return field(named(name).value(stream).contentType(ContentType.APPLICATION_OCTET_STREAM).fileName(fileName));
    }

    @Override
    public MultipartBody field(String name, byte[] bytes, ContentType contentType, String fileName) {
        return field(named(name).value(bytes).contentType(contentType).fileName(fileName));
    }

    @Override
    public MultipartBody field(String name, byte[] bytes, String fileName) {
        return field(named(name).value(bytes).contentType(ContentType.APPLICATION_OCTET_STREAM).fileName(fileName));
    }

    @Override
    public MultipartBody sortFields() {
        Collections.sort(parameters);
        return this;
    }

    @Override
    public MultipartBody charset(Charset charset) {
        this.charSet = charset;
        return this;
    }

    @Override
    public MultipartBody contentType(String mimeType) {
        header(HeaderNames.CONTENT_TYPE, mimeType);
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
    public MultipartBody boundary(String boundaryIdentifier) {
        this.boundary = boundaryIdentifier;
        return this;
    }

    @Override
    public String getBoundary() {
        if(boundary == null){
            boundary = UUID.randomUUID().toString();
        }
        return boundary;
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
        field(named(name).value(value).contentType(contentType));
        return this;
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
        headers.remove("Content-Type");
        return this;
    }
}
