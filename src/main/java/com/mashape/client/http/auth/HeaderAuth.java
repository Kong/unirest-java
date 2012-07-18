package com.mashape.client.http.auth;

import java.util.Map;

import org.apache.http.Header;

public abstract class HeaderAuth implements Auth {
	
	public abstract Header handleHeader();

	public final Map<String, String> handleParams() {
		return null;
	}
}
