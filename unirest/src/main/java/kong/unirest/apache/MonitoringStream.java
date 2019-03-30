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

package kong.unirest.apache;

import kong.unirest.ProgressMonitor;

import java.io.IOException;
import java.io.OutputStream;

class MonitoringStream extends OutputStream {

    private final OutputStream out;
    private volatile long written = 0;
    private long length;
    private String fieldName;
    private String fileName;
    private ProgressMonitor monitor;

    public MonitoringStream(OutputStream out, long length, String fieldName, String fileName, ProgressMonitor monitor) {
        this.out = out;
        this.length = length;
        this.fieldName = fieldName;
        this.fileName = fileName;
        this.monitor = monitor;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        written++;
        monitor.accept(fieldName, fileName, written, length);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        written += b.length;
        monitor.accept(fieldName, fileName, written, length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        written += len;
        monitor.accept(fieldName, fileName, written, length);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
