package com.mashape.client.request.body;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonNode;

import com.mashape.client.request.BaseRequest;
import com.mashape.client.request.HttpRequest;

public class RequestBodyEntity extends BaseRequest implements Body {

	private Object body;
	
	public RequestBodyEntity(HttpRequest httpRequest) {
		super(httpRequest);
	}
	
	public RequestBodyEntity body(String body) {
		this.body = body;
		return this;
	}
	
	public RequestBodyEntity body(JsonNode body) {
		this.body = body.toString();
		return this;
	}
	
	public Object getBody() {
		return body;
	}

	public HttpEntity getEntity() {
		try {
			return new StringEntity(body.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
