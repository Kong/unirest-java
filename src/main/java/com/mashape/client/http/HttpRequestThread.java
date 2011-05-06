package com.mashape.client.http;

import java.util.Map;

import com.mashape.client.exceptions.MashapeClientException;
import com.mashape.client.http.callback.MashapeCallback;

class HttpRequestThread extends Thread {
	
	private HttpMethod httpMethod;
	private String url;
	private Map<String, String> parameters;
	private String token;
	private MashapeCallback callback;

	public HttpRequestThread(HttpMethod httpMethod, String url, Map<String, String> parameters, String token, MashapeCallback callback) {
		this.httpMethod = httpMethod;
		this.url = url;
		this.parameters = parameters;
		this.token = token;
		this.callback = callback;
	}
	
	@Override
	public void run() {
		Object response;
		try {
			response = HttpClient.execRequest(httpMethod, url, parameters, token);
			if (callback != null) {
				callback.requestCompleted(response);
			}
		} catch (MashapeClientException e) {
			if (callback != null) {
				callback.errorOccurred(e);
			} else {
				throw new RuntimeException(e);
			}
		}
		
	}
	
}
