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

public class ByteResponse extends BaseResponse<byte[]> {
    private final byte[] body;

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

    public static boolean isGzipped(String value) {
        return "gzip".equalsIgnoreCase(value.toLowerCase().trim());
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    protected String getRawBody() {
        return null;
    }
}
