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

/**
 * A ProgressMonitor is a functional interface which can be passed to unirest for the purposes of
 * monitoring file uploads. A common use case is for drawing upload progress bars.
 * If the upload contains multiple files each one is called individually and the file name is provided.
 *
 * note that you will not receive a total for ALL files together at once.
 * If you wanted this you could keep track of the total bytes of files you planned to upload and then
 * have your ProgressMonitor aggregate the results.
 */
@FunctionalInterface
public interface ProgressMonitor {
    /**
     * Accept stats about the current file upload chunk for a file.
     * @param field the field name
     * @param fileName the name of the file in question if available (InputStreams and byte arrays may not have file names)
     * @param bytesWritten the number of bytes that have been uploaded so far
     * @param totalBytes the total bytes that will be uploaded. Note this this may be an estimate if an InputStream was used
     * */
    void accept(String field, String fileName, Long bytesWritten, Long totalBytes);
}
