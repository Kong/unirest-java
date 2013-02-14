package com.mashape.client.request;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.codehaus.jackson.JsonNode;

import com.mashape.client.http.HttpResponse;

public interface Body {

	HttpEntity getEntity();
	
	HttpResponse<String> asString();
	
	HttpResponse<JsonNode> asJson();
	
	HttpResponse<InputStream> asBinary();
	
}
