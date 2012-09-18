package com.mashape.client.authentication;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

public abstract class Authentication {

	protected List<Header> headers = new LinkedList<Header>();
	protected Map<String, String> queryParameters = new HashMap<String, String>();
	
	public List<Header> getHeaders() {
		return headers;
	}

	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}
	
	
}
