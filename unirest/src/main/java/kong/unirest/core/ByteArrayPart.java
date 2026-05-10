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


/**
 * A multipart form body part that wraps a byte array as file content.
 * <p>
 * This class is used to upload in-memory binary data as a file in multipart
 * form requests. The byte array is treated as file content with an associated
 * filename and content type.
 *
 * @see BodyPart
 * @see MultipartBody#field(String, byte[], ContentType, String)
 */
public class ByteArrayPart extends BodyPart<byte[]> {
    private final String fileName;

    /**
     * Constructs a new ByteArrayPart with the specified parameters.
     *
     * @param name the form field name for this part
     * @param bytes the byte array content to upload
     * @param contentType the MIME content type of the data
     * @param fileName the filename to associate with this part
     */
    ByteArrayPart(String name, byte[] bytes, ContentType contentType, String fileName) {
        this(name, bytes, contentType, fileName, null);
    }

    /**
     * Constructs a new ByteArrayPart with the specified parameters and custom headers.
     *
     * @param name the form field name for this part
     * @param bytes the byte array content to upload
     * @param contentType the MIME content type of the data
     * @param fileName the filename to associate with this part
     * @param headers additional headers to include with this part, or {@code null} for none
     */
    ByteArrayPart(String name, byte[] bytes, ContentType contentType, String fileName, Headers headers) {
        super(bytes, name, contentType.toString(), headers);
        this.fileName = fileName;
    }

    /**
     * Returns the filename associated with this byte array part.
     *
     * @return the filename for this part
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /**
     * Indicates that this part represents file content.
     *
     * @return {@code true} always, as byte array parts are treated as file uploads
     */
    @Override
    public boolean isFile() {
        return true;
    }

    /**
     * Returns a string representation of this part in the format "name=fileName".
     *
     * @return a string representation of this part
     */
    @Override
    public String toString() {
        return String.format("%s=%s", getName(), fileName);
    }
}
