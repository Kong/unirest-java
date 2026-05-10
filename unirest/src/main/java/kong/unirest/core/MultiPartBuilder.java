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

/**
 * A builder class for constructing multipart form data parts with customizable
 * content type, headers, and file names.
 * <p>
 * This builder supports various value types including strings, input streams,
 * files, and byte arrays. Use the fluent API to configure the part and then
 * call {@link #toBodyPart()} to create the final {@link BodyPart}.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * BodyPart<?> part = MultiPartBuilder.named("file")
 *     .value(new File("/path/to/file.txt"))
 *     .contentType(ContentType.TEXT_PLAIN)
 *     .fileName("custom-name.txt")
 *     .header("X-Custom-Header", "value")
 *     .toBodyPart();
 * }</pre>
 */
public class MultiPartBuilder {
    private final String partName;
    private Object fieldValue;
    private ContentType partContentType;
    private Headers headers = new Headers();
    private String fName;

    /**
     * Creates a new MultiPartBuilder with the specified part name.
     *
     * @param name the name of the multipart field
     * @return a new MultiPartBuilder instance
     */
    public static MultiPartBuilder named(String name) {
        return new MultiPartBuilder(name);
    }

    /**
     * Constructs a new MultiPartBuilder with the specified part name.
     *
     * @param partName the name of the multipart field
     */
    public MultiPartBuilder(String partName) {
        this.partName = partName;
    }

    /**
     * Sets the value of this part to a string.
     *
     * @param value the string value for this part
     * @return this builder for method chaining
     */
    public MultiPartBuilder value(String value) {
        this.fieldValue = value;
        return this;
    }

    /**
     * Sets the value of this part to an input stream.
     *
     * @param inputStream the input stream containing the part data
     * @return this builder for method chaining
     */
    public MultiPartBuilder value(InputStream inputStream) {
        this.fieldValue = inputStream;
        return this;
    }

    /**
     * Sets the value of this part to a file.
     *
     * @param file the file to upload
     * @return this builder for method chaining
     */
    public MultiPartBuilder value(File file) {
        this.fieldValue = file;
        return this;
    }

    /**
     * Sets the value of this part to a byte array.
     *
     * @param content the byte array containing the part data
     * @return this builder for method chaining
     */
    public MultiPartBuilder value(byte[] content) {
        this.fieldValue = content;
        return this;
    }

    /**
     * Sets the value of this part to an object.
     * First class types supported include File, InputStream and byte[]
     * all other types will be converted to a string
     *
     * @param content the object being set for a value.
     * @return this builder for method chaining
     */
    public MultiPartBuilder value(Object content) {
        this.fieldValue = content;
        return this;
    }

    /**
     * Sets the file name for this part.
     * <p>
     * This is useful when uploading input streams or byte arrays
     * where the original file name is not available.
     * </p>
     *
     * @param fileName the file name to use for this part
     * @return this builder for method chaining
     */
    public MultiPartBuilder fileName(String fileName) {
        this.fName = fileName;
        return this;
    }

    /**
     * Sets the content type for this part using a string representation.
     *
     * @param contentType the MIME type string (e.g., "application/json")
     * @return this builder for method chaining
     */
    public MultiPartBuilder contentType(String contentType) {
        return contentType(ContentType.create(contentType));
    }

    /**
     * Sets the content type for this part.
     *
     * @param type the content type for this part
     * @return this builder for method chaining
     */
    public MultiPartBuilder contentType(ContentType type) {
        this.partContentType = type;
        return this;
    }

    /**
     * Adds a custom header to this part.
     *
     * @param name  the header name
     * @param value the header value
     * @return this builder for method chaining
     */
    public MultiPartBuilder header(String name, String value) {
        this.headers.add(name, value);
        return this;
    }

    /**
     * Builds and returns the appropriate {@link BodyPart} based on the configured value type.
     * <p>
     * The returned type depends on the value set:
     * <ul>
     *   <li>{@link InputStream} → {@link InputStreamPart}</li>
     *   <li>{@link File} → {@link FilePart}</li>
     *   <li>{@code byte[]} → {@link ByteArrayPart}</li>
     *   <li>Other (including String) → {@link ParamPart}</li>
     * </ul>
     *
     * @return a BodyPart instance configured with the builder's settings
     */
    public BodyPart<?> toBodyPart() {
        if (fieldValue instanceof InputStream) {
            return new InputStreamPart(partName, (InputStream) fieldValue, getMimeType(), fName, headers);
        } else if (fieldValue instanceof File) {
            return new FilePart((File) fieldValue, partName, getMimeType(), headers);
        } else if (fieldValue instanceof byte[]) {
            return new ByteArrayPart(partName, (byte[]) fieldValue, partContentType, fName, headers);
        } else {
            return new ParamPart(partName, String.valueOf(fieldValue), getMimeType(), headers);
        }
    }

    private String getMimeType() {
        return partContentType == null ? null : partContentType.getMimeType();
    }
}
