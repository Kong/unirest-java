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
 * Represents an HTTP Content-Type header value, consisting of a MIME type,
 * an optional character encoding, and a binary indicator.
 * <p>
 * This class provides predefined constants for common content types and factory
 * methods for creating custom content types. It is used throughout Unirest for
 * specifying the content type of request bodies and multipart form fields.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Using a predefined constant
 * Unirest.post("/api/data")
 *     .contentType(ContentType.APPLICATION_JSON)
 *     .body("{\"key\": \"value\"}")
 *     .asString();
 *
 * // Creating a custom content type
 * ContentType custom = ContentType.create("application/vnd.api+json", StandardCharsets.UTF_8);
 * }</pre>
 */
public class ContentType {
    private static final Set<String> BINARY_TYPES = new HashSet<>();

    /** Content type for Atom XML feeds: {@code application/atom+xml} with ISO-8859-1 charset. */
    public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", ISO_8859_1);

    /** Content type for URL-encoded form data: {@code application/x-www-form-urlencoded} with ISO-8859-1 charset. */
    public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", ISO_8859_1);

    /** Content type for JSON data: {@code application/json} with UTF-8 charset. */
    public static final ContentType APPLICATION_JSON = create("application/json", StandardCharsets.UTF_8);

    /** Content type for JSON Patch documents: {@code application/json-patch+json}. */
    public static final ContentType APPLICATION_JSON_PATCH = create("application/json-patch+json");

    /** Content type for arbitrary binary data: {@code application/octet-stream}. Marked as binary. */
    public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", true);

    /** Content type for binary octet stream: {@code binary/octet-stream}. Marked as binary. */
    public static final ContentType BINARY_OCTET_STREAM = create("binary/octet-stream", true);

    /** Content type for SVG XML images: {@code application/svg+xml} with ISO-8859-1 charset. */
    public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", ISO_8859_1);

    /** Content type for XHTML documents: {@code application/xhtml+xml} with ISO-8859-1 charset. */
    public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", ISO_8859_1);

    /** Content type for XML data: {@code application/xml} with ISO-8859-1 charset. */
    public static final ContentType APPLICATION_XML = create("application/xml", ISO_8859_1);

    /** Content type for PDF documents: {@code application/pdf}. Marked as binary. */
    public static final ContentType APPLICATION_PDF = create("application/pdf", true);

    /** Content type for BMP images: {@code image/bmp}. Marked as binary. */
    public static final ContentType IMAGE_BMP = create("image/bmp", true);

    /** Content type for GIF images: {@code image/gif}. Marked as binary. */
    public static final ContentType IMAGE_GIF = create("image/gif", true);

    /** Content type for JPEG images: {@code image/jpeg}. Marked as binary. */
    public static final ContentType IMAGE_JPEG = create("image/jpeg", true);

    /** Content type for PNG images: {@code image/png}. Marked as binary. */
    public static final ContentType IMAGE_PNG = create("image/png", true);

    /** Content type for SVG images: {@code image/svg+xml}. */
    public static final ContentType IMAGE_SVG = create("image/svg+xml");

    /** Content type for TIFF images: {@code image/tiff}. Marked as binary. */
    public static final ContentType IMAGE_TIFF = create("image/tiff", true);

    /** Content type for WebP images: {@code image/webp}. Marked as binary. */
    public static final ContentType IMAGE_WEBP = create("image/webp", true);

    /** Content type for multipart form data: {@code multipart/form-data} with ISO-8859-1 charset. */
    public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", ISO_8859_1);

    /** Content type for HTML documents: {@code text/html} with ISO-8859-1 charset. */
    public static final ContentType TEXT_HTML = create("text/html", ISO_8859_1);

    /** Content type for plain text: {@code text/plain} with ISO-8859-1 charset. */
    public static final ContentType TEXT_PLAIN = create("text/plain", ISO_8859_1);

    /** Content type for XML text: {@code text/xml} with ISO-8859-1 charset. */
    public static final ContentType TEXT_XML = create("text/xml", ISO_8859_1);

    /** Content type for Server-Sent Events: {@code text/event-stream} with UTF-8 charset. */
    public static final ContentType EVENT_STREAMS = create("text/event-stream", StandardCharsets.UTF_8);

    /** Wildcard content type that matches any MIME type: {@code *}{@code /*}. */
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
     * @param isBinary true if this content type represents binary data
     * @return a new ContentType instance
     */
    public static ContentType create(String mimeType, boolean isBinary) {
        return new ContentType(mimeType, null, isBinary);
    }

    /**
     * Constructs a new ContentType with all parameters.
     *
     * @param mimeType the MIME type string
     * @param encoding the character encoding, or null if not specified
     * @param isBinary true if this content type represents binary data
     */
    ContentType(String mimeType, Charset encoding, boolean isBinary) {
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.isBinary = isBinary;
        if (isBinary && !BINARY_TYPES.contains(mimeType)) {
            BINARY_TYPES.add(mimeType);
        }
    }

    /**
     * Determines if the given MIME type represents binary content.
     * <p>
     * A MIME type is considered binary if it contains "binary" in the string
     * or if it has been registered as a binary type (e.g., image types, PDF).
     * </p>
     *
     * @param mimeType the MIME type string to check
     * @return true if the MIME type represents binary content, false otherwise
     */
    public static boolean isBinary(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        String lc = mimeType.toLowerCase();
        return lc.contains("binary") || BINARY_TYPES.contains(lc);
    }

    /**
     * Returns the string representation of this content type.
     * <p>
     * If a charset is specified, it will be included in the format:
     * {@code mimeType; charset=encoding}
     * </p>
     *
     * @return the content type string suitable for use in HTTP headers
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(mimeType);
        if (encoding != null) {
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
     * @return true if this is a binary content type, false otherwise
     */
    public boolean isBinary() {
        return isBinary;
    }

    /**
     * Returns the character encoding for this content type.
     *
     * @return the charset, or null if not specified
     */
    public Charset getCharset() {
        return encoding;
    }
}
