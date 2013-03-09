package com.mashape.unicorn.http.async;

import com.mashape.unicorn.http.HttpClientHelper;
import com.mashape.unicorn.http.HttpResponse;
import com.mashape.unicorn.request.HttpRequest;

public class RequestThread<T> extends Thread {

	private HttpRequest httpRequest;
	private Class<T> responseClass;
	private Callback<T> callback;
	
	public RequestThread(HttpRequest httpRequest, Class<T> responseClass, Callback<T> callback) {
		this.httpRequest = httpRequest;
		this.responseClass = responseClass;
		this.callback = callback;
	}
	
	@Override
	public void run() {
		HttpResponse<T> response = HttpClientHelper.request(httpRequest, responseClass);
		if (callback != null) {
			callback.completed(response);
		}
	}
	
}
