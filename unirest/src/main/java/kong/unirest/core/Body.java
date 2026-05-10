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
 * This interface provides access to request body content, which can be either:
 * <ul>
 *   <li>A multipart body containing multiple {@link BodyPart} elements (e.g., file uploads with form fields)</li>
 *   <li>A single entity body (e.g., JSON, XML, or raw content)</li>
 * </ul>
 * Implementations handle the serialization and encoding of body content
 * for transmission in HTTP requests.
 *
 * @see BodyPart
 * @see MultipartBody
 */
public interface Body {

    /**
     * Indicates whether this body is a multipart body.
     * <p>
     * Multipart bodies contain multiple parts, typically used for file uploads
     * combined with form fields.
     *
     * @return {@code true} if this is a multipart body, {@code false} otherwise
     */
    boolean isMultiPart();

    /**
     * Indicates whether this body is a single entity body.
     * <p>
     * Entity bodies contain a single piece of content, such as JSON, XML,
     * or raw binary data.
     *
     * @return {@code true} if this is an entity body, {@code false} otherwise
     */
    boolean isEntityBody();

    /**
     * Returns the character set used for encoding the body content.
     * <p>
     * The default implementation returns {@link StandardCharsets#UTF_8}.
     *
     * @return the {@link Charset} for body encoding
     */
    default Charset getCharset(){
        return StandardCharsets.UTF_8;
    }

    /**
     * Returns the collection of body parts for a multipart body.
     * <p>
     * The default implementation returns an empty collection. Multipart body
     * implementations should override this to return their parts.
     *
     * @return a {@link Collection} of {@link BodyPart} elements, or an empty collection
     *         if this is not a multipart body
     */
    default Collection<BodyPart> multiParts(){
        return Collections.emptyList();
    }

    /**
     * Returns the single body part for a non-multipart body.
     * <p>
     * The default implementation returns {@code null}. Entity body
     * implementations should override this to return their content.
     *
     * @return the single {@link BodyPart}, or {@code null} if not applicable
     */
    default BodyPart uniPart(){
        return null;
    }

    /**
     * Returns the multipart mode for encoding.
     * <p>
     * The mode determines how multipart content is formatted and encoded.
     * The default implementation returns {@link MultipartMode#BROWSER_COMPATIBLE}.
     *
     * @return the {@link MultipartMode} for this body
     */
    default MultipartMode getMode(){
        return MultipartMode.BROWSER_COMPATIBLE;
    }

    /**
     * Returns the progress monitor for tracking upload progress.
     * <p>
     * The default implementation returns {@code null}, indicating no progress monitoring.
     *
     * @return the {@link ProgressMonitor}, or {@code null} if progress monitoring is not enabled
     */
    default ProgressMonitor getMonitor(){
        return null;
    }

    /**
     * Returns the boundary string used to separate parts in a multipart body.
     * <p>
     * The boundary is a unique string that delimits each part in the multipart
     * message. The default implementation returns {@code null}, allowing the
     * system to generate a boundary automatically.
     *
     * @return the boundary string, or {@code null} to use an auto-generated boundary
     */
    default String getBoundary() {
        return null;
    }

    /**
     * Finds and returns a body part by its field name.
     * <p>
     * Searches through the multipart body parts and returns the first part
     * whose name matches the specified field name.
     *
     * @param name the field name to search for
     * @return the matching {@link BodyPart}, or {@code null} if no part with the given name exists
     */
    default BodyPart getField(String name){
        return multiParts()
                .stream()
                .filter(part -> part.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
