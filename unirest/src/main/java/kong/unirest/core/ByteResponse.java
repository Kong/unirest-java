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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An HTTP response implementation that provides the response body as a byte array.
 * <p>
 * This class is used when the response content needs to be consumed as raw bytes,
 * such as when downloading binary files. It supports optional progress monitoring
 * for tracking download progress.
 *
 * @see BaseResponse
 * @see HttpResponse
 */
public class ByteResponse extends BaseResponse<byte[]> {
    private final byte[] body;

    /**
     * Creates a new ByteResponse from a raw HTTP response.
     * <p>
     * If a progress monitor is provided, the download progress will be tracked
     * and reported through the monitor. Otherwise, the content is read directly.
     *
     * @param r the raw HTTP response to read the body from
     * @param downloadMonitor the progress monitor for tracking download progress,
     *                        or {@code null} for no monitoring
     * @throws UnirestException if an I/O error occurs while reading the response body
     */
    public ByteResponse(RawResponse r, ProgressMonitor downloadMonitor) {
        super(r);
        if(downloadMonitor == null) {
            this.body = r.getContentAsBytes();
        } else {
            MonitoringInputStream ip = new MonitoringInputStream(r.getContent(), downloadMonitor, (String)null, r);
            try {
                body = getBytes(ip);
            } catch (IOException e){
                throw new UnirestException(e);
            }
        }
    }

    /**
     * Reads all bytes from an input stream into a byte array.
     * <p>
     * This utility method handles both {@link ByteArrayInputStream} (optimized path)
     * and general input streams. The input stream is closed after reading.
     *
     * @param is the input stream to read from
     * @return a byte array containing all bytes read from the stream
     * @throws IOException if an I/O error occurs while reading the stream
     */
    public static byte[] getBytes(InputStream is) throws IOException {
        try {
            int len;
            int size = 1024;
            byte[] buf;

            if (is instanceof ByteArrayInputStream) {
                size = is.available();
                buf = new byte[size];
                len = is.read(buf, 0, size);
            } else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                buf = new byte[size];
                while ((len = is.read(buf, 0, size)) != -1) {
                    bos.write(buf, 0, len);
                }
                buf = bos.toByteArray();
            }
            return buf;
        } finally {
            is.close();
        }
    }

    /**
     * Checks if a content encoding value indicates gzip compression.
     *
     * @param value the content encoding header value to check
     * @return {@code true} if the value indicates gzip encoding, {@code false} otherwise
     */
    public static boolean isGzipped(String value) {
        return "gzip".equalsIgnoreCase(value.toLowerCase().trim());
    }

    /**
     * Returns the response body as a byte array.
     *
     * @return the response body bytes
     */
    @Override
    public byte[] getBody() {
        return body;
    }

    /**
     * Returns the raw body as a string.
     * <p>
     * This implementation always returns {@code null} since the byte response
     * does not retain a string representation of the body.
     *
     * @return {@code null}
     */
    @Override
    protected String getRawBody() {
        return null;
    }
}
