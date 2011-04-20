package com.mashapeclient.exceptions;


public class MashapeDeveloperKeyException extends Exception {
	
	private static final long serialVersionUID = 7010681844364128264L;
	
	public MashapeDeveloperKeyException(String message, int code) {
		super("Error " + code + " - " + message);
	}
}
