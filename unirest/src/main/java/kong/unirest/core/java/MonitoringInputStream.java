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

package kong.unirest.core.java;

import kong.unirest.core.ProgressMonitor;

import java.io.IOException;
import java.io.InputStream;

class MonitoringInputStream extends InputStream {
    private final InputStream in;
    private volatile long totalNumBytesRead = 0;

    private ProgressMonitor monitor;

    public MonitoringInputStream(InputStream value, ProgressMonitor monitor) {
        this.in = value;
        this.monitor = monitor;
    }

    @Override
    public int read() throws IOException {
        return (int)updateProgress(in.read());
    }

    @Override
    public int read(byte[] b) throws IOException {
        return (int)updateProgress(in.read(b));
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return (int)updateProgress(in.read(b, off, len));
    }

    @Override
    public long skip(long n) throws IOException {
        return updateProgress(in.skip(n));
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    private long updateProgress(long numBytesRead) {
        if (numBytesRead > 0) {
            this.totalNumBytesRead += numBytesRead;
            monitor.accept("body", null, numBytesRead, totalNumBytesRead);
        }

        return numBytesRead;
    }
}
