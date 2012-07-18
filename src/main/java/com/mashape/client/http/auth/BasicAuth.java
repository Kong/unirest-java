package com.mashape.client.http.auth;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * Implementation of HeaderAuth for Basic Authentication (of the form Authorization: username:password)
 */
public class BasicAuth extends HeaderAuth {

	private Header header;
	
	public BasicAuth(String username, String password) {
		String headerValue = username + ":" + password;
		header = new BasicHeader("Authorization", Base64.encodeBase64String(headerValue.getBytes()).replace("\r\n", ""));
	}
	
	public Header handleHeader() {
		return header;
	}
}
