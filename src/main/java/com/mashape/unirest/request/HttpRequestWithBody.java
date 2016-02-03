/*
The MIT License

Copyright (c) 2013 Mashape (http://mashape.com)

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mashape.unirest.request;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.InputStreamBody;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;
import com.mashape.unirest.request.body.MultipartBody;
import com.mashape.unirest.request.body.RawBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestWithBody extends HttpRequest {

	public HttpRequestWithBody(HttpMethod method, String url) {
		super(method, url);
	}

	@Override
	public HttpRequestWithBody routeParam(String name, String value) {
		super.routeParam(name, value);
		return this;
	}

	@Override
	public HttpRequestWithBody header(String name, String value) {
		return (HttpRequestWithBody) super.header(name, value);
	}

	@Override
	public HttpRequestWithBody headers(Map<String, String> headers) {
		return (HttpRequestWithBody) super.headers(headers);
	}

	@Override
	public HttpRequestWithBody basicAuth(String username, String password) {
		super.basicAuth(username, password);
		return this;
	}

	@Override
	public HttpRequestWithBody queryString(Map<String, Object> parameters) {
		return (HttpRequestWithBody) super.queryString(parameters);
	}

	@Override
	public HttpRequestWithBody queryString(String name, Object value) {
		return (HttpRequestWithBody) super.queryString(name, value);
	}

	public MultipartBody field(String name, Collection<?> value) {
		MultipartBody body = new MultipartBody(this).field(name, value);
		this.body = body;
		return body;
	}

	public MultipartBody field(String name, Object value) {
		return field(name, value, null);
	}

	public MultipartBody field(String name, File file) {
		return field(name, file, null);
	}

	public MultipartBody field(String name, Object value, String contentType) {
		MultipartBody body = new MultipartBody(this).field(name, (value == null) ? "" : value.toString(), contentType);
		this.body = body;
		return body;
	}

	public MultipartBody field(String name, File file, String contentType) {
		MultipartBody body = new MultipartBody(this).field(name, file, contentType);
		this.body = body;
		return body;
	}

	public MultipartBody fields(Map<String, Object> parameters) {
		MultipartBody body = new MultipartBody(this);
		if (parameters != null) {
			for (Entry<String, Object> param : parameters.entrySet()) {
				if (param.getValue() instanceof File) {
					body.field(param.getKey(), (File) param.getValue());
				} else {
					body.field(param.getKey(), (param.getValue() == null) ? "" : param.getValue().toString());
				}
			}
		}
		this.body = body;
		return body;
	}

	public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
		InputStreamBody inputStreamBody = new InputStreamBody(stream, contentType, fileName);
		MultipartBody body = new MultipartBody(this).field(name, inputStreamBody, true, contentType.toString());
		this.body = body;
		return body;
	}

	public MultipartBody field(String name, InputStream stream, String fileName) {
		MultipartBody body = field(name, stream, ContentType.APPLICATION_OCTET_STREAM, fileName);
		this.body = body;
		return body;
	}

	public RequestBodyEntity body(JsonNode body) {
		return body(body.toString());
	}

	public RequestBodyEntity body(String body) {
		RequestBodyEntity b = new RequestBodyEntity(this).body(body);
		this.body = b;
		return b;
	}

	public RequestBodyEntity body(Object body) {
		ObjectMapper objectMapper = (ObjectMapper) Options.getOption(Option.OBJECT_MAPPER);

		if (objectMapper == null) {
			throw new RuntimeException("Serialization Impossible. Can't find an ObjectMapper implementation.");
		}

		return body(objectMapper.writeValue(body));
	}

	public RawBody body(byte[] body) {
		RawBody b = new RawBody(this).body(body);
		this.body = b;
		return b;
	}

	/**
	 * Sugar method for body operation
	 *
	 * @param body raw org.JSONObject
	 * @return RequestBodyEntity instance
	 */
	public RequestBodyEntity body(JSONObject body) {
		return body(body.toString());
	}

	/**
	 * Sugar method for body operation
	 *
	 * @param body raw org.JSONArray
	 * @return RequestBodyEntity instance
	 */
	public RequestBodyEntity body(JSONArray body) {
		return body(body.toString());
	}
}