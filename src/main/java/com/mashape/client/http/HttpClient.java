package com.mashape.client.http;

import com.mashape.client.request.HttpRequest;
import com.mashape.client.request.HttpRequestWithBody;


public class HttpClient {

	public static HttpRequest get(String url) {
		return new HttpRequest(HttpMethod.GET, url);
	}
	
	public static HttpRequestWithBody post(String url) {
		return new HttpRequestWithBody(HttpMethod.POST, url);
	}
	
	public static HttpRequest delete(String url) {
		return new HttpRequest(HttpMethod.DELETE, url);
	}
	
	public static HttpRequestWithBody patch(String url) {
		return new HttpRequestWithBody(HttpMethod.PATCH, url);
	}
	
	public static HttpRequestWithBody put(String url) {
		return new HttpRequestWithBody(HttpMethod.PUT, url);
	}
	
}
