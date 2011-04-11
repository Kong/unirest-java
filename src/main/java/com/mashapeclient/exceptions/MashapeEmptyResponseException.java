package com.mashapeclient.exceptions;

import static com.mashapeclient.exceptions.ExceptionConstants.EXCEPTION_INVALID_REQUEST;
import static com.mashapeclient.exceptions.ExceptionConstants.EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE;

public class MashapeEmptyResponseException extends Exception {

	private static final long serialVersionUID = 8080127885797777452L;

	public MashapeEmptyResponseException() {
		super("Error " + EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE + " - " + EXCEPTION_INVALID_REQUEST);
	}
	
}
