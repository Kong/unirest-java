package com.mashape.unicorn.http;

import java.util.Map;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import com.mashape.unicorn.request.HttpRequest;


public class HttpClientHelper {

	private static final String USER_AGENT = "mashape-java/3.0";
	private static final int CONNECTION_TIMEOUT = 600000; 
	private static final int SOCKET_TIMEOUT = 600000;
	
	public static <T> HttpResponse<T> request(HttpRequest request, Class<T> responseClass) {
		
		request.header("user-agent", USER_AGENT);
		
		HttpUriRequest reqObj = null;
		
		switch(request.getHttpMethod()) {
		case GET:
			reqObj = new HttpGet(request.getUrl());
			break;
		case POST:
			reqObj = new HttpPost(request.getUrl());
			break;
		case PUT:
			reqObj = new HttpPut(request.getUrl());
			break;
		case DELETE:
			reqObj = new HttpDeleteWithBody(request.getUrl());
			break;
		case PATCH:
			reqObj = new HttpPatchWithBody(request.getUrl());
			break;
		}
		
		for(Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
			reqObj.addHeader(entry.getKey(), entry.getValue());
		}
		
		// Set body
		if (!(request.getHttpMethod() == HttpMethod.GET || request.getHttpMethod() == HttpMethod.DELETE)) {
			if (request.getBody() != null) {
				((HttpEntityEnclosingRequestBase) reqObj).setEntity(request.getBody().getEntity());
			}
		}
		
		setTimeouts(reqObj.getParams());
		
		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		org.apache.http.HttpResponse response;
		try {
			response = client.execute(reqObj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return new HttpResponse<T>(response, responseClass);
	}
	
	private static void setTimeouts(HttpParams params) {
	    params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 
	        CONNECTION_TIMEOUT);
	    params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);
	}
}
