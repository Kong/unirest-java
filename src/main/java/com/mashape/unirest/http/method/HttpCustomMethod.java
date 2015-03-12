package com.mashape.unirest.http.method;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpCustomMethod extends HttpEntityEnclosingRequestBase {

	private String methodName;

	public HttpCustomMethod() {
		super();
	}

	public HttpCustomMethod(final URI uri, String methodName) {
		super();
		setURI(uri);
		this.methodName = methodName;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the uri is invalid.
	 */
	public HttpCustomMethod(final String uri,  String methodName) {
		super();
		setURI(URI.create(uri));
		this.methodName = methodName;
	}

	@Override
	public String getMethod() {
		return methodName;
	}

}
