package com.mashape.client.exceptions;

public class MashapeClientException extends Exception {

	private static final long serialVersionUID = -2524576670609030251L;
	
	private int code;
	
	public MashapeClientException(String message, int code) {
		super(message);
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
}
