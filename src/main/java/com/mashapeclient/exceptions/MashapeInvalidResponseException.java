package com.mashapeclient.exceptions;

import static com.mashapeclient.exceptions.ExceptionConstants.EXCEPTION_EMPTY_REQUEST;
import static com.mashapeclient.exceptions.ExceptionConstants.EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE;

public class MashapeInvalidResponseException extends Exception {

	private static final long serialVersionUID = 8080127885797777452L;

	public MashapeInvalidResponseException() {
		super("Error " + EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE + " - " + EXCEPTION_EMPTY_REQUEST);
	}
	
}
