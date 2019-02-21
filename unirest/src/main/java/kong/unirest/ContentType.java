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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public enum ContentType {
    APPLICATION_ATOM_XML("application/atom+xml", ISO_8859_1),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded", ISO_8859_1),
    APPLICATION_JSON("application/json", StandardCharsets.UTF_8),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_SVG_XML("application/svg+xml", ISO_8859_1),
    APPLICATION_XHTML_XML("application/xhtml+xml", ISO_8859_1),
    APPLICATION_XML("application/xml", ISO_8859_1),
    IMAGE_BMP("image/bmp"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_SVG("image/svg+xml"),
    IMAGE_TIFF("image/tiff"),
    IMAGE_WEBP("image/webp"),
    MULTIPART_FORM_DATA("multipart/form-data", ISO_8859_1),
    TEXT_HTML("text/html", ISO_8859_1),
    TEXT_PLAIN("text/plain", ISO_8859_1),
    TEXT_XML("text/xml", ISO_8859_1),
    WILDCARD("*/*");

    private final String mimeType;
    private final Charset encoding;

    ContentType(String mimeType){
        this(mimeType, null);
    }

    ContentType(String mimeType, Charset encoding) {
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(mimeType);
        if(encoding != null){
            sb.append("; charset=").append(encoding);
        }
        return sb.toString();
    }

    public String getMimeType() {
        return mimeType;
    }
}
