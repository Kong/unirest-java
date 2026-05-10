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
 * Represents a single part of a multipart HTTP request body.
 * <p>
 * Body parts are used when building multipart form requests, such as file uploads
 * or forms with multiple fields. Each part has a name, value, content type, and
 * optional headers.
 * <p>
 * This is an abstract class; concrete implementations handle specific value types
 * such as strings, files, or input streams.
 *
 * @param <T> the type of the body part value (e.g., String, File, InputStream)
 * @see MultipartBody
 */
public abstract class BodyPart<T> implements Comparable { ;
    private final String name;
    private final T value;
    private final String contentType;
    private final Class<?> partType;
    private final Headers headers;

    /**
     * Creates a new body part with the specified value, name, and content type.
     *
     * @param value the value of this body part
     * @param name the name (field name) of this body part
     * @param contentType the MIME content type, or {@code null} to use defaults
     */
    protected BodyPart(T value, String name, String contentType) {
        this(value, name, contentType, null);
    }

    /**
     * Creates a new body part with the specified value, name, content type, and headers.
     *
     * @param value the value of this body part
     * @param name the name (field name) of this body part
     * @param contentType the MIME content type, or {@code null} to use defaults
     * @param headers additional headers for this part, or {@code null} for none
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
     * @return the body part value
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the runtime class of the body part value.
     *
     * @return the {@link Class} object representing the value's type
     */
    public Class<?> getPartType(){
        return partType;
    }

    /**
     * Returns the content type for this body part.
     * <p>
     * If no content type was explicitly set:
     * <ul>
     *   <li>File parts default to {@code application/octet-stream}</li>
     *   <li>Non-file parts default to {@code application/x-www-form-urlencoded; charset=UTF-8}</li>
     * </ul>
     *
     * @return the MIME content type string
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
     * @return the name, or an empty string if no name was set
     */
    public String getName() {
        return name == null ? "" : name;
    }

    /**
     * Returns the file name for this body part.
     * <p>
     * By default, this returns the same value as {@link #getName()}.
     * Subclasses may override this to provide a different file name.
     *
     * @return the file name
     */
    public String getFileName(){
        return name;
    }

    /**
     * Compares this body part to another based on their names.
     * <p>
     * This comparison is used for ordering body parts alphabetically by name.
     *
     * @param o the object to compare to
     * @return a negative integer, zero, or a positive integer if this part's name
     *         is less than, equal to, or greater than the other part's name;
     *         returns 0 if the object is not a {@link BodyPart}
     */
    @Override
    public int compareTo(Object o) {
        if(o instanceof BodyPart){
            return getName().compareTo(((BodyPart)o).getName());
        }
        return 0;
    }

    /**
     * Indicates whether this body part represents a file.
     * <p>
     * File parts are handled differently during multipart encoding,
     * including the use of different default content types.
     *
     * @return {@code true} if this part represents a file, {@code false} otherwise
     */
    abstract public boolean isFile();

    /**
     * Returns the additional headers for this body part.
     *
     * @return the {@link Headers} for this part, or {@code null} if no additional headers were set
     */
    public Headers getHeaders() {
        return headers;
    }
}
