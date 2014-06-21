package com.mashape.unirest.http.async;

public abstract class AbstractCallback<T> implements Callback<T> {

	@Override
	public void uploadProgress(long bytesSent, long totalLength) {
		// Stub method
	}

}
