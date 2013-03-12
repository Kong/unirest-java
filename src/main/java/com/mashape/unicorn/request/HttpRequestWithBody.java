package com.mashape.unicorn.request;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;

import com.mashape.unicorn.http.HttpMethod;
import com.mashape.unicorn.request.body.MultipartBody;
import com.mashape.unicorn.request.body.RequestBodyEntity;

public class HttpRequestWithBody extends HttpRequest {

	public HttpRequestWithBody(HttpMethod method, String url) {
		super(method, url);
	}

	public MultipartBody field(String name, File file) {
		return field(name, file);
	}
	
	@Override
	public HttpRequestWithBody header(String name, String value) {
		return (HttpRequestWithBody) super.header(name, value);
	}
	
	@Override
	public HttpRequest headers(Map<String, String> headers) {
		return super.headers(headers);
	}
	
	public MultipartBody field(String name, Object value) {
		MultipartBody body =  new MultipartBody(this).field(name, value.toString());
		this.body = body;
		return body;
	}
	
	public MultipartBody fields(Map<String, Object> parameters) {
		MultipartBody body =  new MultipartBody(this);
		if (parameters != null) {
			for(Entry<String, Object> param : parameters.entrySet()) {
				if (param.getValue() instanceof File) {
					body.field(param.getKey(), (File)param.getValue());
				} else {
					body.field(param.getKey(), param.getValue().toString());
				}
			}
		}
		this.body = body;
		return body;
	}
	
	public RequestBodyEntity body(JsonNode body) {
		return body(body.toString());
	}
	
	public RequestBodyEntity body(String body) {
		RequestBodyEntity b =  new RequestBodyEntity(this).body(body);
		this.body = b;
		return b;
	}
	
}
