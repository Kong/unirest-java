package com.mashapeclient.exceptions;


public class MashapeApiKeyException extends Exception {
	
	private static final long serialVersionUID = 7010681844364128264L;
	
	public MashapeApiKeyException(String message, int code) {
		super("Error " + code + " - " + message);
	}
}
