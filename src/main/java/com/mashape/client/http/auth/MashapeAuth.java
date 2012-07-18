package com.mashape.client.http.auth;

import org.apache.http.Header;

import com.mashape.client.http.AuthUtil;

public class MashapeAuth extends HeaderAuth {

	private Header header;
	
	public MashapeAuth(String publicKey, String privateKey) {
		this.header = AuthUtil.generateAuthenticationHeader(publicKey, privateKey);
	}
	
	@Override
	public Header handleHeader() {
		return header;
	}

}
