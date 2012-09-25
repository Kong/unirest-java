package com.mashape.client.http.utils;

import java.math.BigInteger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtils {

	public static String getHMAC_SHA1(String value, String key) {
		if (value == null || key == null || value.trim() == "" || key.trim() == "") {
			throw new RuntimeException("Please enter your Mashape keys in the constructor.");
		}
		try {
			// Get an hmac_sha1 key from the raw key bytes
			byte[] keyBytes = key.getBytes();			
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
			 
			// Get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);

			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(value.getBytes());
			
			String hmac = "";
			BigInteger hash = new BigInteger(1, rawHmac);
		    hmac = hash.toString(16);
		    if (hmac.length() % 2 != 0) {
		        hmac = "0" + hmac;
		    }
		    return hmac;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
