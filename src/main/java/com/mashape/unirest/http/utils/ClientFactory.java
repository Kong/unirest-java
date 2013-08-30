package com.mashape.unirest.http.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.nio.client.HttpAsyncClient;

import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;

public class ClientFactory {

	private static HttpClient httpClient;
	private static HttpAsyncClient asyncHttpClient;
	
	public static HttpClient getClient() {
		if (httpClient == null) {
			httpClient = (HttpClient) Options.getOption(Option.HTTPCLIENT);
		}
		return httpClient;
	}
	
	public static HttpAsyncClient getAsyncClient() {
		if (asyncHttpClient == null) {
			asyncHttpClient = (HttpAsyncClient) Options.getOption(Option.ASYNCHTTPCLIENT);
		}
		return asyncHttpClient;
	}

	public static void setHttpClient(HttpClient httpClient) {
		ClientFactory.httpClient = httpClient;
	}
	
}
