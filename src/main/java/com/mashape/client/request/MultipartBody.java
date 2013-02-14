package com.mashape.client.request;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.JsonNode;

import com.mashape.client.http.HttpClientHelper;
import com.mashape.client.http.HttpRequest;
import com.mashape.client.http.HttpResponse;
import com.mashape.client.http.utils.MapUtil;

public class MultipartBody implements Body {

	private Map<String, Object> parameters = new HashMap<String, Object>();

	private HttpRequest httpRequest;
	
	private boolean hasFile;
	
	public MultipartBody(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	
	public MultipartBody field(String name, File file) {
		this.parameters.put(name, file);
		hasFile = true;
		return this;
	}
	
	public MultipartBody field(String name, String value) {
		parameters.put(name, value);
		return this;
	}

	public HttpEntity getEntity() {
		if (hasFile) {
			MultipartEntity entity = new MultipartEntity();
			for(Entry<String, Object> part : parameters.entrySet()) {
				if (part.getValue() instanceof File) {
					hasFile = true;
					entity.addPart(part.getKey(), new FileBody((File) part.getValue()));
				} else {
					try {
						entity.addPart(part.getKey(), new StringBody(part.getValue().toString(), Charset.forName("UTF-8")));
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				}
			}
			return entity;
		} else {
			try {
				return new UrlEncodedFormEntity(MapUtil.getList(parameters), HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
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
