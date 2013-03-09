package com.mashape.unicorn.request;

import java.io.InputStream;

import org.codehaus.jackson.JsonNode;

import com.mashape.unicorn.http.HttpClientHelper;
import com.mashape.unicorn.http.HttpResponse;
import com.mashape.unicorn.http.async.Callback;
import com.mashape.unicorn.http.async.RequestThread;

public abstract class BaseRequest {

	protected HttpRequest httpRequest;
	
	protected BaseRequest(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	
	protected BaseRequest() {
		super();
	}
	
	public HttpResponse<String> asString() {
		return HttpClientHelper.request(httpRequest, String.class);
	}

	public Thread asString(Callback<String> callback) {
		Thread thread = new RequestThread<String>(httpRequest, String.class, callback);
		thread.start();
		return thread;
	}

	public HttpResponse<JsonNode> asJson() {
		return HttpClientHelper.request(httpRequest, JsonNode.class);
	}

	public Thread asJson(Callback<JsonNode> callback) {
		Thread thread = new RequestThread<JsonNode>(httpRequest, JsonNode.class, callback);
		thread.start();
		return thread;
	}

	public HttpResponse<InputStream> asBinary() {
		return HttpClientHelper.request(httpRequest, InputStream.class);
	}

	public Thread asBinary(Callback<InputStream> callback) {
		Thread thread = new RequestThread<InputStream>(httpRequest, InputStream.class, callback);
		thread.start();
		return thread;
	}

}
