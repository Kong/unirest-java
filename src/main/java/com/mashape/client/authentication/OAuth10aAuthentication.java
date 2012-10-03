package com.mashape.client.authentication;

public class OAuth10aAuthentication extends OAuthAuthentication {

	public OAuth10aAuthentication(String consumerKey, String consumerSecret,
			String redirectUrl) {
		super(consumerKey, consumerSecret, redirectUrl);
	}

	public void setAccessSecret(String accessSecret) {
		queryParameters.put(ACCESS_SECRET, accessSecret);
	}
	
}
