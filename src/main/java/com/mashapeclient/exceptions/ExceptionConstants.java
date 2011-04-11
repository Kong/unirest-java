package com.mashapeclient.exceptions;

public final class ExceptionConstants {

	public static final int EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE = 1003;
	public static final String EXCEPTION_NOTSUPPORTED_HTTPMETHOD = "HTTP method not supported. Only DELETE, GET, POST, PUT are supported";
	
	public static final int EXCEPTION_SYSTEM_ERROR_CODE = 2000;
	public static final String EXCEPTION_EMPTY_REQUEST = "A request attempt was made to the component, but the response was empty. The component's URL may be wrong or the firewall may be blocking your outbound HTTP requests";
	
	public static final String EXCEPTION_INVALID_REQUEST = "The component returned an invalid response";
	
	private ExceptionConstants() {
		// No constructor
	}
	
}
