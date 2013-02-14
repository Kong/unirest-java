package com.mashape.client.http.async;

import com.mashape.client.http.HttpResponse;

public interface Callback<T> {

	void completed(HttpResponse<T> response);	
	
}
