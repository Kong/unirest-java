package com.mashape.unirest.request.progress;

public interface ProgressListener {
	
	public void progress(long deltaBytes, long totalLength);
	
}
