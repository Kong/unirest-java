package com.mashapeclient.http.ssl;

import javax.net.ssl.SSLException;

import org.apache.http.conn.ssl.AbstractVerifier;

public class CustomVerifier extends AbstractVerifier {

	@Override
	public void verify(String host, String[] cns, String[] subjectAlts)
			throws SSLException {
		// Accepts any SSL certificate
	}
}
