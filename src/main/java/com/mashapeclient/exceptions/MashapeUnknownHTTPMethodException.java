package com.mashapeclient.exceptions;

import static com.mashapeclient.exceptions.ExceptionConstants.EXCEPTION_NOTSUPPORTED_HTTPMETHOD;
import static com.mashapeclient.exceptions.ExceptionConstants.EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE;

public class MashapeUnknownHTTPMethodException extends Exception {

	private static final long serialVersionUID = 8080127885797777452L;

	public MashapeUnknownHTTPMethodException() {
		super("Error " + EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE + " - " + EXCEPTION_NOTSUPPORTED_HTTPMETHOD);
	}
	
}
