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

/**
 * Represents an HTTP Content-Type header value, consisting of a MIME type and optional character encoding.
 * <p>
 * This class provides constants for commonly used content types and factory methods for creating
 * custom content types. It also tracks binary content types for proper handling of response bodies.
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Using predefined constants
 * Unirest.post(url)
 *     .contentType(ContentType.APPLICATION_JSON)
 *     .body(jsonString)
 *     .asString();
 *
 * // Creating a custom content type
 * ContentType custom = ContentType.create("application/vnd.api+json", StandardCharsets.UTF_8);
 *
 * // Creating a content type with a different charset
 * ContentType xmlUtf8 = ContentType.APPLICATION_XML.withCharset(StandardCharsets.UTF_8);
 * }</pre>
 *
 * @see <a href="https://www.iana.org/assignments/media-types/media-types.xhtml">IANA Media Types</a>
 */
public class ContentType {
    private static final Set<String> BINARY_TYPES = new HashSet<>();

    /** {@code application/atom+xml} with ISO-8859-1 charset. */
    public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", ISO_8859_1);

    /** {@code application/x-www-form-urlencoded} with ISO-8859-1 charset. Used for HTML form submissions. */
    public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", ISO_8859_1);

    /** {@code application/json} with UTF-8 charset. Used for JSON data. */
    public static final ContentType APPLICATION_JSON = create("application/json", StandardCharsets.UTF_8);

    /** {@code application/json-patch+json} for JSON Patch documents (RFC 6902). */
    public static final ContentType APPLICATION_JSON_PATCH = create("application/json-patch+json");

    /** {@code application/octet-stream} for arbitrary binary data. Marked as binary type. */
    public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", true);

    /** {@code binary/octet-stream} for binary data. Marked as binary type. */
    public static final ContentType BINARY_OCTET_STREAM = create("binary/octet-stream", true);

    /** {@code application/svg+xml} with ISO-8859-1 charset. Used for SVG images in XML format. */
    public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", ISO_8859_1);

    /** {@code application/xhtml+xml} with ISO-8859-1 charset. Used for XHTML documents. */
    public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", ISO_8859_1);

    /** {@code application/xml} with ISO-8859-1 charset. Used for XML data. */
    public static final ContentType APPLICATION_XML = create("application/xml", ISO_8859_1);

    /** {@code application/pdf} for PDF documents. Marked as binary type. */
    public static final ContentType APPLICATION_PDF = create("application/pdf", true);

    /** {@code image/bmp} for BMP images. Marked as binary type. */
    public static final ContentType IMAGE_BMP = create("image/bmp", true);

    /** {@code image/gif} for GIF images. Marked as binary type. */
    public static final ContentType IMAGE_GIF = create("image/gif", true);

    /** {@code image/jpeg} for JPEG images. Marked as binary type. */
    public static final ContentType IMAGE_JPEG = create("image/jpeg", true);

    /** {@code image/png} for PNG images. Marked as binary type. */
    public static final ContentType IMAGE_PNG = create("image/png", true);

    /** {@code image/svg+xml} for SVG images. */
    public static final ContentType IMAGE_SVG = create("image/svg+xml");

    /** {@code image/tiff} for TIFF images. Marked as binary type. */
    public static final ContentType IMAGE_TIFF = create("image/tiff", true);

    /** {@code image/webp} for WebP images. Marked as binary type. */
    public static final ContentType IMAGE_WEBP = create("image/webp", true);

    /** {@code multipart/form-data} with ISO-8859-1 charset. Used for file uploads and form data with binary content. */
    public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", ISO_8859_1);

    /** {@code text/html} with ISO-8859-1 charset. Used for HTML documents. */
    public static final ContentType TEXT_HTML = create("text/html", ISO_8859_1);

    /** {@code text/plain} with ISO-8859-1 charset. Used for plain text content. */
    public static final ContentType TEXT_PLAIN = create("text/plain", ISO_8859_1);

    /** {@code text/xml} with ISO-8859-1 charset. Used for XML content. */
    public static final ContentType TEXT_XML = create("text/xml", ISO_8859_1);

    /** {@code text/event-stream} with UTF-8 charset. Used for Server-Sent Events (SSE). */
    public static final ContentType EVENT_STREAMS = create("text/event-stream", StandardCharsets.UTF_8);

    /** {@code *}{@code /*} wildcard content type that matches any MIME type. */
    public static final ContentType WILDCARD = create("*/*");

    private final String mimeType;
    private final Charset encoding;
    private final boolean isBinary;

    /**
     * Creates a new ContentType with the specified MIME type and no charset.
     *
     * @param mimeType the MIME type string (e.g., "application/json")
     * @return a new ContentType instance
     */
    public static ContentType create(String mimeType) {
        return new ContentType(mimeType, null, false);
    }

    /**
     * Creates a new ContentType with the specified MIME type and charset.
     *
     * @param mimeType the MIME type string (e.g., "application/json")
     * @param charset  the character encoding for this content type
     * @return a new ContentType instance
     */
    public static ContentType create(String mimeType, Charset charset) {
        return new ContentType(mimeType, charset, false);
    }

    /**
     * Creates a new ContentType with the specified MIME type and binary flag.
     *
     * @param mimeType the MIME type string (e.g., "image/png")
     * @param isBinary {@code true} if this content type represents binary data
     * @return a new ContentType instance
     */
    public static ContentType create(String mimeType, boolean isBinary) {
        return new ContentType(mimeType, null, isBinary);
    }

    /**
     * Constructs a new ContentType with the specified MIME type, encoding, and binary flag.
     * <p>
     * If {@code isBinary} is {@code true}, the MIME type is registered in the internal
     * set of known binary types for future lookups via {@link #isBinary(String)}.
     * </p>
     *
     * @param mimeType the MIME type string
     * @param encoding the character encoding, or {@code null} if not specified
     * @param isBinary {@code true} if this content type represents binary data
     */
    ContentType(String mimeType, Charset encoding, boolean isBinary) {
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.isBinary = isBinary;
        if(isBinary && !BINARY_TYPES.contains(mimeType)){
            BINARY_TYPES.add(mimeType);
        }
    }

    /**
     * Determines if the given MIME type represents binary content.
     * <p>
     * A MIME type is considered binary if:
     * </p>
     * <ul>
     *   <li>It contains the word "binary"</li>
     *   <li>It has been registered as a binary type (via {@link #create(String, boolean)} with {@code isBinary=true})</li>
     * </ul>
     *
     * @param mimeType the MIME type to check
     * @return {@code true} if the MIME type represents binary content, {@code false} otherwise or if {@code mimeType} is {@code null}
     */
    public static boolean isBinary(String mimeType) {
        if(mimeType == null){
            return false;
        }
        String lc = mimeType.toLowerCase();
        return lc.contains("binary") || BINARY_TYPES.contains(lc);
    }

    /**
     * Returns the string representation of this content type.
     * <p>
     * The format is {@code mimeType} if no charset is specified, or
     * {@code mimeType; charset=encoding} if a charset is present.
     *
     * @return the content type string suitable for use in HTTP headers
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(mimeType);
        if(encoding != null){
            sb.append("; charset=").append(encoding);
        }
        return sb.toString();
    }

    /**
     * Returns the MIME type portion of this content type.
     *
     * @return the MIME type string (e.g., "application/json")
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Creates a new ContentType with the same MIME type but a different charset.
     *
     * @param charset the new character encoding
     * @return a new ContentType instance with the specified charset
     */
    public ContentType withCharset(Charset charset) {
        return new ContentType(mimeType, charset, isBinary);
    }

    /**
     * Returns whether this content type represents binary data.
     *
     * @return {@code true} if this content type is binary
     */
    public boolean isBinary() {
        return isBinary;
    }

    /**
     * Returns the character encoding for this content type.
     *
     * @return the charset, or {@code null} if no charset is specified
     */
    public Charset getCharset() {
        return encoding;
    }
}
