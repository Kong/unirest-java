package com.mashape.client.http;


public class HttpClient {

	public static HttpRequest get(String url) {
		return new HttpRequest(HttpMethod.GET, url);
	}
	
	public static HttpRequest post(String url) {
		return new HttpRequest(HttpMethod.POST, url);
	}
	
	public static HttpRequest delete(String url) {
		return new HttpRequest(HttpMethod.DELETE, url);
	}
	
	public static HttpRequest patch(String url) {
		return new HttpRequest(HttpMethod.PATCH, url);
	}
	
	public static HttpRequest put(String url) {
		return new HttpRequest(HttpMethod.PUT, url);
	}
	
}
