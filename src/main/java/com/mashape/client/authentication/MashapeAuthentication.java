package com.mashape.client.authentication;

import com.mashape.client.authentication.utils.AuthUtil;

public class MashapeAuthentication extends HeaderAuthentication {

	public MashapeAuthentication(String publicKey, String privateKey) {
		headers.add(AuthUtil.generateAuthenticationHeader(publicKey, privateKey));
	}

}
