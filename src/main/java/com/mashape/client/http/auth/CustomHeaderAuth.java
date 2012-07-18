package com.mashape.client.http.auth;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class CustomHeaderAuth extends HeaderAuth {
	
	private Header header;
	
	public CustomHeaderAuth(String headerName, String headerValue) {
		this.header = new BasicHeader(headerName, headerValue);
	}
	
	@Override
	public Header handleHeader() {
		return header;
	}

}
