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

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Represents a multi-part body builder for a request.
 */
public interface MultipartBody extends HttpRequest<MultipartBody>, Body {
    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param value: The string value for the field
     * @return The same MultipartBody
     */
    MultipartBody field(String name, String value);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param value: The string value for the field
     * @param contentType: the content type of the value
     * @return The same MultipartBody
     */
    MultipartBody field(String name, String value, String contentType);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param values: A collection of values for the same name.
     * @return The same MultipartBody
     */
    MultipartBody field(String name, Collection<?> values);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param file: A File object.
     * @return The same MultipartBody
     */
    MultipartBody field(String name, File file);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param file: A File object.
     * @param contentType: the content mime-type of the file
     * @return The same MultipartBody
     */
    MultipartBody field(String name, File file, String contentType);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param value: A input stream
     * @param contentType: the content mime-type of the file
     * @return The same MultipartBody
     */
    MultipartBody field(String name, InputStream value, ContentType contentType);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param stream: A input stream
     * @param contentType: the content mime-type of the file
     * @param fileName: the name of the file which will be included in the file part header
     * @return The same MultipartBody
     */
    MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param bytes: The raw bytes for the file
     * @param contentType: the content mime-type of the file
     * @param fileName: the name of the file which will be included in the file part header
     * @return The same MultipartBody
     */
    MultipartBody field(String name, byte[] bytes, ContentType contentType, String fileName);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param stream: The raw bytes for the file
     * @param fileName: the name of the file which will be included in the file part header
     * @return The same MultipartBody
     */
    MultipartBody field(String name, InputStream stream, String fileName);

    /**
     * add a simple field with a name and value
     * @param name: the Name of the form field
     * @param bytes: The raw bytes for the file
     * @param fileName: the name of the file which will be included in the file part header
     * @return The same MultipartBody
     */
    MultipartBody field(String name, byte[] bytes, String fileName);

    /**
     * Set the encoding of the request body
     * @param charset the character set encoding of the body
     * @return The same MultipartBody
     */
    MultipartBody charset(Charset charset);

    /**
     * Set the mime-type of the request body
     * @param mimeType the mime type of the body
     * @return The same MultipartBody
     */
    MultipartBody contentType(String mimeType);

    /**
     * Set the Apache Mode.
     * @param value the value of the mode
     * @return The same MultipartBody
     * */
    MultipartBody mode(MultipartMode value);


    /**
     * Set a file Progress upload monitor suitable for drawing progress bars and whatnot.
     * @param monitor a monitor
     * @return The same MultipartBody
     * */
    MultipartBody uploadMonitor(ProgressMonitor monitor);
}
