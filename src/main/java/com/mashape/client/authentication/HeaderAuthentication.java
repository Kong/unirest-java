package com.mashape.client.authentication;

import java.util.AbstractMap;

import org.apache.http.message.BasicHeader;

public abstract class HeaderAuthentication extends Authentication {
	
	public void addHeader(AbstractMap.SimpleEntry<String, String> ... headers) {
		for (AbstractMap.SimpleEntry<String, String> header : headers) {
			super.headers.add(new BasicHeader(header.getKey(), header.getValue()));
		}
	}
	
}
