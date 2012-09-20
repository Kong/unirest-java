package com.mashape.client.authentication;

public class QueryAuthentication extends Authentication {

	public QueryAuthentication(AuthenticationParameter ... parameters) {
		for (AuthenticationParameter parameter : parameters) {
			queryParameters.put(parameter.getName(), parameter.getValue());
		}
	}
	
}
