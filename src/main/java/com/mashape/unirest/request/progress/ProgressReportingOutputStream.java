package com.mashape.unirest.request.progress;

import java.io.IOException;
import java.io.OutputStream;

public class ProgressReportingOutputStream extends OutputStream {

	private final OutputStream out;
	private volatile long written = 0;
	private long length;
	private ProgressListener listener;
	
	public ProgressReportingOutputStream(OutputStream out, long length,
			ProgressListener listener) {
		this.out = out;
		this.length = length;
		this.listener = listener;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
		written ++;
		if (listener != null) {
			listener.progress(1, length);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
		written += b.length;
		if (listener != null) {
			listener.progress(b.length, length);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		written += len;
		if (listener != null) {
			listener.progress(len, length);
		}
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
