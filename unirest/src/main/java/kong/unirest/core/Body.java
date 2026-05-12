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
import java.util.Collection;
import java.util.Collections;

/**
 * Represents the body of an HTTP request.
 * <p>
 * A body can be either a multipart body (containing multiple parts such as form fields
 * and file uploads) or an entity body (containing a single piece of content like JSON or XML).
 * </p>
 * <p>
 * Implementations of this interface are used internally by Unirest to serialize
 * request content for transmission.
 * </p>
 *
 * @see BodyPart
 * @see MultipartBody
 * @see MultipartMode
 */
public interface Body {

    /**
     * Indicates whether this body is a multipart body.
     * <p>
     * Multipart bodies contain multiple parts separated by boundaries and are typically
     * used for form submissions that include file uploads.
     *
     * @return {@code true} if this is a multipart body, {@code false} otherwise
     */
    boolean isMultiPart();

    /**
     * Indicates whether this body is an entity body.
     * <p>
     * Entity bodies contain a single piece of content such as JSON, XML, or plain text.
     *
     * @return {@code true} if this is an entity body, {@code false} otherwise
     */
    boolean isEntityBody();

    /**
     * Returns the character encoding used for this body.
     *
     * @return the {@link Charset} for encoding the body content; defaults to UTF-8
     */
    default Charset getCharset(){
        return StandardCharsets.UTF_8;
    }

    /**
     * Returns all parts of a multipart body.
     *
     * @return a collection of {@link BodyPart} objects representing each part;
     *         returns an empty collection if this is not a multipart body
     */
    default Collection<BodyPart> multiParts(){
        return Collections.emptyList();
    }

    /**
     * Returns the single body part for an entity body.
     *
     * @return the {@link BodyPart} containing the entity content,
     *         or {@code null} if this is not an entity body
     */
    default BodyPart uniPart(){
        return null;
    }

    /**
     * Returns the multipart mode used for encoding this body.
     *
     * @return the {@link MultipartMode} determining how parts are encoded;
     *         defaults to {@link MultipartMode#BROWSER_COMPATIBLE}
     * @see MultipartMode
     */
    default MultipartMode getMode(){
        return MultipartMode.BROWSER_COMPATIBLE;
    }

    /**
     * Returns the progress monitor for tracking upload progress.
     *
     * @return the {@link ProgressMonitor} for this body, or {@code null} if none is set
     * @see ProgressMonitor
     */
    default ProgressMonitor getMonitor(){
        return null;
    }

    /**
     * Returns the boundary string used to separate parts in a multipart body.
     * <p>
     * The boundary is a unique string that delimits each part in the multipart content.
     *
     * @return the boundary string, or {@code null} if not applicable or auto-generated
     */
    default String getBoundary() {
        return null;
    }

    /**
     * Retrieves a specific body part by its field name.
     *
     * @param name the name of the field to retrieve
     * @return the {@link BodyPart} with the specified name,
     *         or {@code null} if no part with that name exists
     */
    default BodyPart getField(String name){
        return multiParts()
                .stream()
                .filter(part -> part.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
