package com.mashape.client.authentication;

import org.apache.http.message.BasicHeader;

import com.mashape.client.authentication.utils.Base64;

public class BasicAuthentication extends HeaderAuthentication {

	public BasicAuthentication(String username, String password) {
		String headerValue = username + ":" + password;
		headers.add(new BasicHeader("Authorization", "Basic " + Base64.encode(headerValue).replace("\r\n", "")));
	}
	
	
	
}
