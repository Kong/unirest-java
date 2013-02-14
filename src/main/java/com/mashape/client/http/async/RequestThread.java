package com.mashape.client.http.async;

import com.mashape.client.http.HttpClientHelper;
import com.mashape.client.http.HttpResponse;
import com.mashape.client.request.HttpRequest;

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
