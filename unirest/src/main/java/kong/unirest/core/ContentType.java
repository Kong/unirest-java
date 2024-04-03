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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class ContentType {
    private static final Set<String> BINARY_TYPES = new HashSet<>();

    public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", ISO_8859_1);
    public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", ISO_8859_1);
    public static final ContentType APPLICATION_JSON = create("application/json", StandardCharsets.UTF_8);
    public static final ContentType APPLICATION_JSON_PATCH = create("application/json-patch+json");
    public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", true);
    public static final ContentType BINARY_OCTET_STREAM = create("binary/octet-stream", true);
    public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", ISO_8859_1);
    public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", ISO_8859_1);
    public static final ContentType APPLICATION_XML = create("application/xml", ISO_8859_1);
    public static final ContentType APPLICATION_PDF = create("application/pdf", true);
    public static final ContentType IMAGE_BMP = create("image/bmp", true);
    public static final ContentType IMAGE_GIF = create("image/gif", true);
    public static final ContentType IMAGE_JPEG = create("image/jpeg", true);
    public static final ContentType IMAGE_PNG = create("image/png", true);
    public static final ContentType IMAGE_SVG = create("image/svg+xml");
    public static final ContentType IMAGE_TIFF = create("image/tiff", true);
    public static final ContentType IMAGE_WEBP = create("image/webp", true);
    public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", ISO_8859_1);
    public static final ContentType TEXT_HTML = create("text/html", ISO_8859_1);
    public static final ContentType TEXT_PLAIN = create("text/plain", ISO_8859_1);
    public static final ContentType TEXT_XML = create("text/xml", ISO_8859_1);
    public static final ContentType WILDCARD = create("*/*");

    private final String mimeType;
    private final Charset encoding;
    private final boolean isBinary;

    public static ContentType create(String mimeType) {
        return new ContentType(mimeType, null, false);
    }

    public static ContentType create(String mimeType, Charset charset) {
        return new ContentType(mimeType, charset, false);
    }

    public static ContentType create(String mimeType, boolean isBinary) {
        return new ContentType(mimeType, null, isBinary);
    }

    ContentType(String mimeType, Charset encoding, boolean isBinary) {
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.isBinary = isBinary;
        if(isBinary && !BINARY_TYPES.contains(mimeType)){
            BINARY_TYPES.add(mimeType);
        }
    }

    public static boolean isBinary(String mimeType) {
        if(mimeType == null){
            return false;
        }
        String lc = mimeType.toLowerCase();
        return lc.contains("binary") || BINARY_TYPES.contains(lc);
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

    public ContentType withCharset(Charset charset) {
        return new ContentType(mimeType, charset, isBinary);
    }

    public boolean isBinary() {
        return isBinary;
    }

    public Charset getCharset() {
        return encoding;
    }
}
