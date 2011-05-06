package com.mashape.client.http.utils;

import java.util.Map;

public class RequestPrepareResult {

	private String url;
	private Map<String, String> parameters;
	
	RequestPrepareResult(String url, Map<String, String> parameters) {
		this.url = url;
		this.parameters = parameters;
	}
	
	public String getUrl() {
		return url;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	
	
}
