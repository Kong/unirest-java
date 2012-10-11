package com.mashape.client.authentication;

public abstract class OAuthAuthentication extends Authentication {

	public static final String CALLBACK_URL = "callbackUrl";
	public static final String CONSUMER_SECRET = "consumerSecret";
	public static final String CONSUMER_KEY = "consumerKey";
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String ACCESS_SECRET = "accessSecret";

	protected OAuthAuthentication(String consumerKey, String consumerSecret, String callbackUrl) {
		queryParameters.put(CONSUMER_KEY, consumerKey);
		queryParameters.put(CONSUMER_SECRET, consumerSecret);
		queryParameters.put(CALLBACK_URL, callbackUrl);
	}
	
	public void setAccessToken(String accessToken) {
		queryParameters.put(ACCESS_TOKEN, accessToken);
	}
	
}
