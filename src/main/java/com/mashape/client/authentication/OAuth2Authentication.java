package com.mashape.client.authentication;

public class OAuth2Authentication extends OAuthAuthentication {

	public OAuth2Authentication(String consumerKey, String consumerSecret,
			String redirectUrl) {
		super(consumerKey, consumerSecret, redirectUrl);
	}

}
