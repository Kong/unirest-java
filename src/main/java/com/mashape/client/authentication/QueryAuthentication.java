package com.mashape.client.authentication;

import java.util.AbstractMap;


public class QueryAuthentication extends Authentication {

	public QueryAuthentication(AbstractMap.SimpleEntry<String, String> ... headers) {
		for (AbstractMap.SimpleEntry<String, String> header : headers) {
			queryParameters.put(header.getKey(), header.getValue());
		}
	}
	
}
