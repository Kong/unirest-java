package com.mashape.client.request;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;

import com.mashape.client.http.HttpMethod;
import com.mashape.client.request.body.Body;
import com.mashape.client.request.body.MultipartBody;
import com.mashape.client.request.body.RequestBodyEntity;

public class HttpRequest extends BaseRequest {

	private HttpMethod httpMethod;
	private String url;
	private Map<String, String> headers = new HashMap<String, String>();
	private Body body;
	
	private URL parseUrl(String s) throws Exception {
	     URL u = new URL(s);
	     return new URI(
	            u.getProtocol(), 
	            u.getAuthority(), 
	            u.getPath(),
	            u.getQuery(), 
	            u.getRef()).
	            toURL();
	}
	
	public HttpRequest(HttpMethod method, String url) {
		this.httpMethod = method;
		try {
			this.url = parseUrl(url).toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		super.httpRequest = this;
	}
	
	public HttpRequest header(String name, String value) {
		this.headers.put(name.toLowerCase(), value);
		return this;
	}
	
	public HttpRequest headers(Map<String, String> headers) {
		if (headers != null) {
			for(Map.Entry<String, String> entry : headers.entrySet()) {
				header(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}
	
	public MultipartBody field(String name, File file) {
		return field(name, file);
	}
	
	public MultipartBody field(String name, Object value) {
		MultipartBody body =  new MultipartBody(this).field(name, value.toString());
		this.body = body;
		return body;
	}
	
	public MultipartBody parameters(Map<String, Object> parameters) {
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
	
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, String> getHeaders() {
		if (headers == null) return new HashMap<String, String>();
		return headers;
	}

	public Body getBody() {
		return body;
	}
	
}
