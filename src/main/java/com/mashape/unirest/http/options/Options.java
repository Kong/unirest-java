package com.mashape.unirest.http.options;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public class Options {

	private static final int CONNECTION_TIMEOUT = 600000;
	private static final int SOCKET_TIMEOUT = 600000;
	
	private static Map<Option, Object> options = new HashMap<Option, Object>();
	
	public static void setOption(Option option, Object value) {
		options.put(option, value);
	}
	
	public static Object getOption(Option option) {
		return options.get(option);
	}

	static {
		RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		
		setOption(Option.HTTPCLIENT, HttpClientBuilder.create().setDefaultRequestConfig(clientConfig).build());
		
		CloseableHttpAsyncClient asyncClient = HttpAsyncClientBuilder.create().setDefaultRequestConfig(clientConfig).build();
		asyncClient.start();
		
		setOption(Option.ASYNCHTTPCLIENT, asyncClient);
	}
	
}
