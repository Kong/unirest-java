package com.mashape.unirest.http.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class ClientFactory {

	private static HttpClient httpClient;
	
	public static HttpClient getClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}
	
}
