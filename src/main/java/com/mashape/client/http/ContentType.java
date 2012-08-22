package com.mashape.client.http;

public enum ContentType {
	JSON("application/json"),
	FORM("application/x-www-form-urlencoded"),
	MULTIPART("multipart/form-data");

	private String value;

	private ContentType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
