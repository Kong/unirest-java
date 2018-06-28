/*
The MIT License

Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.

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

package io.github.openunirest.request;

import io.github.openunirest.http.HttpMethod;
import io.github.openunirest.http.JsonNode;
import io.github.openunirest.http.ObjectMapper;
import io.github.openunirest.http.exceptions.UnirestConfigException;
import io.github.openunirest.http.options.Option;
import io.github.openunirest.http.options.Options;
import io.github.openunirest.request.body.MultipartBody;
import io.github.openunirest.request.body.RawBody;
import io.github.openunirest.request.body.RequestBodyEntity;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestWithBody extends HttpRequest {

	private Charset charSet = StandardCharsets.UTF_8;

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

	public MultipartBody field(String name, File file) {
		return field(name, file, null);
	}

	public MultipartBody field(String name, File file, String contentType) {
		MultipartBody body = new MultipartBody(this).field(name, file, contentType);
		this.body = body;
		return body;
	}

	public MultipartBody field(String name, Object value) {
		return field(name, value, null);
	}

	public MultipartBody field(String name, Object value, String contentType) {
		MultipartBody body = new MultipartBody(this).field(name, nullToEmpty(value), contentType);
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
					body.field(param.getKey(), nullToEmpty(param.getValue()));
				}
			}
		}
		this.body = body;
		return body;
	}

	private String nullToEmpty(Object v) {
		if(v == null){
			return "";
		}
		return v.toString();
	}

	public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
		MultipartBody body = new MultipartBody(this).field(name, stream, contentType, fileName);
		this.body = body;
		return body;
	}

	public MultipartBody field(String name, InputStream stream, String fileName) {
		MultipartBody body = field(name, stream, ContentType.APPLICATION_OCTET_STREAM, fileName);
		this.body = body;
		return body;
	}

	public HttpRequestWithBody charset(Charset charset) {
		this.charSet = charset;
		return this;
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
			throw new UnirestConfigException("Serialization Impossible. Can't find an ObjectMapper implementation.");
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

	public Charset getCharset() {
		return charSet;
	}

	void setCharset(Charset charset) {
		this.charSet = charset;
	}
}
