package com.mashape.client.exception;

public class InvalidJsonResponseException extends RuntimeException {
	
	private static final long serialVersionUID = 6164574652946278267L;
	
	public InvalidJsonResponseException(String response) {
		super(response);
	}
	
}
