package com.mashape.client.http.response;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

public class MashapeResponse<T> {
	private int code;
	private Map<String, String> headers;
	private InputStream rawBody;
	private T body;

	public MashapeResponse(HttpResponse response, InputStream rawBody, T body) {
		Header[] allHeaders = response.getAllHeaders();
		this.headers = new HashMap<String, String>();
		for(Header header : allHeaders) {
			headers.put(header.getName(), header.getValue());
		}
		this.code = response.getStatusLine().getStatusCode();
		this.rawBody = rawBody;
		this.body = body;
	}

	public int getCode() {
		return code;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public InputStream getRawBody() {
		return rawBody;
	}
	public T getBody() {
		return body;
	}
}
