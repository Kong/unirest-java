package com.mashape.client.request;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonNode;

import com.mashape.client.http.HttpClientHelper;
import com.mashape.client.http.HttpRequest;
import com.mashape.client.http.HttpResponse;

public class RequestBodyEntity implements Body {

	private Object body;
	private HttpRequest httpRequest;
	
	public RequestBodyEntity(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
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
	
	public HttpResponse<String> asString() {
		return HttpClientHelper.request(httpRequest, String.class);
	}
	
	public HttpResponse<JsonNode> asJson() {
		return HttpClientHelper.request(httpRequest, JsonNode.class);
	}
	
	public HttpResponse<InputStream> asBinary() {
		return HttpClientHelper.request(httpRequest, InputStream.class);
	}
	
}
