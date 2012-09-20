package com.mashape.client.authentication;

public class AuthenticationParameter {

	private String name;
	private String value;
	
	public AuthenticationParameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	
}
