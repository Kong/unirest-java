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

import java.nio.charset.StandardCharsets;

/**
 * Represents a single part in a multipart HTTP request body.
 * <p>
 * This abstract class encapsulates the data and metadata for one part of a multipart
 * request, including the part's name, value, content type, and optional headers.
 * Body parts can represent either form fields or file uploads.
 * </p>
 * <p>
 * Subclasses must implement the {@link #isFile()} method to indicate whether
 * the part represents a file upload or a regular form field.
 * </p>
 *
 * @param <T> the type of the value contained in this body part
 * @see MultipartBody
 * @see ContentType
 */
public abstract class BodyPart<T> implements Comparable { ;
    private final String name;
    private final T value;
    private final String contentType;
    private final Class<?> partType;
    private final Headers headers;

    /**
     * Constructs a new BodyPart with the specified value, name, and content type.
     *
     * @param value       the value of this body part
     * @param name        the name (field name) of this body part
     * @param contentType the MIME content type of this body part, or {@code null} to use defaults
     */
    protected BodyPart(T value, String name, String contentType) {
        this(value, name, contentType, null);
    }

    /**
     * Constructs a new BodyPart with the specified value, name, content type, and headers.
     *
     * @param value       the value of this body part
     * @param name        the name (field name) of this body part
     * @param contentType the MIME content type of this body part, or {@code null} to use defaults
     * @param headers     additional headers for this body part, or {@code null} if none
     */
    protected BodyPart(T value, String name, String contentType, Headers headers) {
        this.name = name;
        this.value = value;
        this.contentType = contentType;
        this.partType = value.getClass();
        this.headers = headers;
    }

    /**
     * Returns the value of this body part.
     *
     * @return the value contained in this body part
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the runtime class of the value.
     *
     * @return the {@link Class} object representing the type of the value
     */
    public Class<?> getPartType(){
        return partType;
    }

    /**
     * Returns the content type of this body part.
     * <p>
     * If no content type was explicitly set:
     * <ul>
     *   <li>For file parts: returns {@code application/octet-stream}</li>
     *   <li>For non-file parts: returns {@code application/x-www-form-urlencoded; charset=UTF-8}</li>
     * </ul>
     *
     * @return the MIME content type string for this body part
     */
    public String getContentType() {
        if(contentType == null){
            if(isFile()){
                return ContentType.APPLICATION_OCTET_STREAM.toString();
            }
            return ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8).toString();
        }
        return contentType;
    }

    /**
     * Returns the name (field name) of this body part.
     *
     * @return the name of this body part, or an empty string if no name was set
     */
    public String getName() {
        return name == null ? "" : name;
    }

    /**
     * Returns the file name for this body part.
     * <p>
     * For file uploads, this typically represents the original file name.
     * For regular form fields, this returns the same value as {@link #getName()}.
     *
     * @return the file name of this body part
     */
    public String getFileName(){
        return name;
    }

    /**
     * Compares this body part with another for ordering purposes.
     * <p>
     * Body parts are compared by their names in natural string order.
     * If the other object is not a {@link BodyPart}, returns 0.
     *
     * @param o the object to compare with
     * @return a negative integer, zero, or a positive integer as this body part's
     *         name is less than, equal to, or greater than the other's name
     */
    @Override
    public int compareTo(Object o) {
        if(o instanceof BodyPart){
            return getName().compareTo(((BodyPart)o).getName());
        }
        return 0;
    }

    /**
     * Indicates whether this body part represents a file upload.
     *
     * @return {@code true} if this part represents a file, {@code false} for regular form fields
     */
    abstract public boolean isFile();

    /**
     * Returns any additional headers associated with this body part.
     *
     * @return the headers for this body part, or {@code null} if none were set
     */
    public Headers getHeaders() {
        return headers;
    }
}
