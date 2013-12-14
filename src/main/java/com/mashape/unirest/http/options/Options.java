package com.mashape.unirest.http.options;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public class Options {

	public static final long CONNECTION_TIMEOUT = 10000;
	private static final long SOCKET_TIMEOUT = 60000;
	
	private static Map<Option, Object> options = new HashMap<Option, Object>();
	
	public static void setOption(Option option, Object value) {
		options.put(option, value);
	}
	
	public static Object getOption(Option option) {
		return options.get(option);
	}

	static {
		refresh();
	}
	
	public static void refresh() {
		// Load timeouts
		Object connectionTimeout = Options.getOption(Option.CONNECTION_TIMEOUT);
		if (connectionTimeout == null) connectionTimeout = CONNECTION_TIMEOUT;
		Object socketTimeout = Options.getOption(Option.SOCKET_TIMEOUT);
		if (socketTimeout == null) socketTimeout = SOCKET_TIMEOUT;
		
		// Create common default configuration
		RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout(((Long) connectionTimeout).intValue()).setSocketTimeout(((Long) socketTimeout).intValue()).setConnectionRequestTimeout(((Long)socketTimeout).intValue()).build();
		
		// Create clients
		setOption(Option.HTTPCLIENT, HttpClientBuilder.create().setDefaultRequestConfig(clientConfig).build());
		
		CloseableHttpAsyncClient asyncClient = HttpAsyncClientBuilder.create().setDefaultRequestConfig(clientConfig).build();
		setOption(Option.ASYNCHTTPCLIENT, asyncClient);
	}
	
}
