package com.mashape.client.exception;

public class InvalidJsonArrayException extends InvalidJsonResponseException {
	
	private static final long serialVersionUID = -4141836406895485477L;

	public InvalidJsonArrayException(String response) {
		super(response);
	}
	
}
