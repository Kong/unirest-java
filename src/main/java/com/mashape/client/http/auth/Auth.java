package com.mashape.client.http.auth;

import java.util.Map;

import org.apache.http.Header;

/**
 * Interface for both header-based and parameter-based authentication.
 */
public interface Auth {
	/**
	 * Build and return a header to be added to the request.
	 * @return A header that will be appended to the request.
	 */
	Header handleHeader();
	
	/**
	 * Build and return a map of parameters.
	 * @return A map of parameters to be added to the request.
	 */
	Map<String, String> handleParams();
}
