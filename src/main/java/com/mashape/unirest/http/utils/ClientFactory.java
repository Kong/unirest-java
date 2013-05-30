package com.mashape.unirest.http.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.nio.client.AbstractHttpAsyncClient;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;

public class ClientFactory {

	private static HttpClient httpClient;
	private static AbstractHttpAsyncClient asyncHttpClient;
	
	public static HttpClient getClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}
	
	public static AbstractHttpAsyncClient getAsyncClient() {
		if (asyncHttpClient == null) {
			try {
				asyncHttpClient = new DefaultHttpAsyncClient();
				asyncHttpClient.start();
			} catch (IOReactorException e) {
				throw new RuntimeException(e);
			}
		}
		return asyncHttpClient;
	}
	
}
