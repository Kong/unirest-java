package com.mashape.client.http.callback;

import com.mashape.client.exceptions.MashapeClientException;

public interface MashapeCallback {

	void requestCompleted(Object response);
	void errorOccurred(MashapeClientException exception);
	
}
