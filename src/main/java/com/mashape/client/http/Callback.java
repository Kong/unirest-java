package com.mashape.client.http;

public interface Callback<T> {

	void completed(HttpResponse<T> response);	
	
}
