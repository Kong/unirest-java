package com.mashape.client.authentication;

public abstract class OAuthAuthentication extends Authentication {

	public static final String REDIRECT_URL = "redirectUrl";
	public static final String CONSUMER_SECRET = "consumerSecret";
	public static final String CONSUMER_KEY = "consumerKey";
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String ACCESS_SECRET = "accessSecret";

	protected OAuthAuthentication(String consumerKey, String consumerSecret, String redirectUrl) {
		queryParameters.put(CONSUMER_KEY, consumerKey);
		queryParameters.put(CONSUMER_SECRET, consumerSecret);
		queryParameters.put(REDIRECT_URL, redirectUrl);
	}
	
	public void setAccessToken(String accessToken) {
		queryParameters.put(ACCESS_TOKEN, accessToken);
	}
	
}
