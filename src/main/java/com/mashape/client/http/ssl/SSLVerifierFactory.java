package com.mashape.client.http.ssl;

public class SSLVerifierFactory {

	private static CustomSSLVerifier customSSLVerifier;
	
	public static CustomSSLVerifier getCustomSSLVerifier() {
		if (customSSLVerifier == null) {
			customSSLVerifier = new CustomSSLVerifier();
		}
		return customSSLVerifier;
	}
	
}
