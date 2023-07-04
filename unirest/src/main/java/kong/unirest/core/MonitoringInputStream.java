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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class MonitoringInputStream extends InputStream {
    private final InputStream content;
    private final ProgressMonitor downloadMonitor;
    private final long totalSize;
    private final String fileName;
    private long byteCount = 0;

    public MonitoringInputStream(InputStream content, ProgressMonitor downloadMonitor, Path target, RawResponse rawResponse) {
        this(content, downloadMonitor, target.getFileName().toString(), rawResponse);
    }

    public MonitoringInputStream(InputStream content, ProgressMonitor downloadMonitor, String fileName, RawResponse rawResponse) {
        Objects.requireNonNull(content, "Original InputStream is required");
        Objects.requireNonNull(downloadMonitor, "ProgressMonitor is required");
        Objects.requireNonNull(rawResponse, "RawResponse is required");

        this.content = wrap(content, rawResponse);
        this.downloadMonitor = downloadMonitor;
        this.fileName = fileName;
        this.totalSize = getBodySize(rawResponse);
    }

    private InputStream wrap(InputStream is , RawResponse rawResponse) {
        try {
            if (is.available() > 0 && "gzip".equalsIgnoreCase(rawResponse.getContentType())) {
                return new GZIPInputStream(is);
            } else {
                return is;
            }
        }catch (Exception e){
            throw new UnirestException(e);
        }
    }

    private Long getBodySize(RawResponse r) {
        String header = r.getHeaders().getFirst("Content-Length");
        if (header != null && header.length() > 0) {
            return Long.valueOf(header);
        }
        return 0L;
    }

    @Override
    public int read() throws IOException {
        return content.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = super.read(b);
        monitor(read);
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        monitor(read);
        return read;
    }

    private void monitor(int bytesRead) {
        byteCount = byteCount + bytesRead;
        downloadMonitor.accept("body", fileName, byteCount, totalSize);
    }

    @Override
    public void close() throws IOException {
        content.close();
    }

    @Override
    public long skip(long n) throws IOException {
        return  content.skip(n);
    }

    @Override
    public int available() throws IOException {
        return content.available();
    }

    @Override
    public synchronized void mark(int readlimit) {
        content.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return content.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        content.reset();
    }
}
