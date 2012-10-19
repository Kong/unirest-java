package com.mashape.client.authentication;

import org.apache.http.message.BasicHeader;

public class HeaderAuthentication extends Authentication {
	
	public HeaderAuthentication(AuthenticationParameter ... headers) {
		for (AuthenticationParameter header : headers) {
			super.headers.add(new BasicHeader(header.getName(), header.getValue()));
		}
	}
	
}
