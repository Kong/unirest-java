package com.mashape.client.exceptions;

public class ExceptionConstants {

	public static final int EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE = 1003;
	public static final String EXCEPTION_NOTSUPPORTED_HTTPMETHOD = "HTTP method not supported. Only DELETE, GET, POST, PUT are supported";
	
	public static final int EXCEPTION_SYSTEM_ERROR_CODE = 2000;
	public static final String EXCEPTION_INVALID_REQUEST = "The component returned an invalid response: %s";
	
	private ExceptionConstants() {
		// No constructor
	}
	
}
