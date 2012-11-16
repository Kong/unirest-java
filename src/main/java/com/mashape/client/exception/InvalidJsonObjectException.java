package com.mashape.client.exception;

public class InvalidJsonObjectException extends InvalidJsonResponseException {
	
	private static final long serialVersionUID = 6958786207766532467L;

	public InvalidJsonObjectException(String response) {
		super(response);
	}
	
}
