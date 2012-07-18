package com.mashape.client.http.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

public class QueryAuth implements Auth {

	private Map<String, String> params;
	
	public QueryAuth(String queryKey, String queryValue) {
		params = new HashMap<String, String>();
		params.put(queryKey, queryValue);
	}
	
	public Header handleHeader() {
		return null;
	}

	public Map<String, String> handleParams() {
		return params;
	}

}
