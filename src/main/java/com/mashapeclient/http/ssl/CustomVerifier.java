package com.mashapeclient.http.ssl;

import javax.net.ssl.SSLException;

import org.apache.http.conn.ssl.AbstractVerifier;

public class CustomVerifier extends AbstractVerifier {

	public void verify(String arg0, String[] arg1, String[] arg2)
			throws SSLException {
		// Accepts any SSL certificate
	}
	
}
