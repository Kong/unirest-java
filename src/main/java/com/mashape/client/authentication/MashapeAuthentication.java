package com.mashape.client.authentication;

import org.apache.http.message.BasicHeader;

public class MashapeAuthentication extends HeaderAuthentication {

	public MashapeAuthentication(String mashapeKey) {
		headers.add(new BasicHeader("X-Mashape-Authorization", mashapeKey));
	}

}
