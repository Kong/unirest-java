package com.mashape.unirest.http.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.nio.client.HttpAsyncClient;

import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;

public class ClientFactory {

	public static HttpClient getHttpClient() {
		return (HttpClient) Options.getOption(Option.HTTPCLIENT);
	}
	
	public static HttpAsyncClient getAsyncHttpClient() {
		return (HttpAsyncClient) Options.getOption(Option.ASYNCHTTPCLIENT);
	}
	
}
