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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.StringUtils;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.utils.Base64Coder;
import com.mashape.unirest.http.utils.URLParamEncoder;
import com.mashape.unirest.request.body.Body;

public class HttpRequest extends BaseRequest {

	private HttpMethod httpMethod;
	protected String url;
	Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
	protected Body body;
	String methodName;
	
	public HttpRequest(HttpMethod method, String url) {
		this.httpMethod = method;
		this.url = url;
		super.httpRequest = this;
	}

	public HttpRequest(HttpMethod method, String methodName, String url) {
		this.httpMethod = method;
		this.url = url;
		this.methodName = methodName;
		super.httpRequest = this;
	}
	
	public HttpRequest routeParam(String name, String value) {
		Matcher matcher = Pattern.compile("\\{" + name + "\\}").matcher(url);
		int count = 0;
		while(matcher.find()) {
			count++;
		}
		if (count == 0) {
			throw new RuntimeException("Can't find route parameter name \"" + name + "\"");
		}
		this.url = url.replaceAll("\\{" + name + "\\}", URLParamEncoder.encode(value));
		return this;
	}
	
	public HttpRequest basicAuth(String username, String password) {
		header("Authorization", "Basic " + Base64Coder.encodeString(username+ ":" + password));
		return this;
	}
	
	public HttpRequest header(String name, String value) {
		List<String> list = this.headers.get(name.trim());
		if (list == null) {
			list = new ArrayList<String>();
		}
		list.add(value);
		this.headers.put(name.trim(), list);
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
	
	public HttpRequest queryString(String name, Collection<?> value) {
		for(Object cur : value) {
			queryString(name, cur);
		}
		return this;
	}
	
	public HttpRequest queryString(String name, Object value) {
		StringBuilder queryString  = new StringBuilder();
		if (this.url.contains("?")) {
			queryString.append("&");
		} else {
			queryString.append("?");
		}
		try {
			queryString.append(name).append("=").append(URLEncoder.encode((value == null) ? "" : value.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		this.url += queryString.toString();
		return this;
	}
	
	public HttpRequest queryString(Map<String, Object> parameters) {
		if (parameters != null) {
			for(Entry<String, Object> param : parameters.entrySet()) {
				if (param.getValue() instanceof String || param.getValue() instanceof Number || param.getValue() instanceof Boolean) {
					queryString(param.getKey(), param.getValue());
				} else {
					throw new RuntimeException("Parameter \"" + param.getKey() + "\" can't be sent with a GET request because of type: " + param.getValue().getClass().getName());
				}
			}
		}
		return this;
	}
	
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, List<String>> getHeaders() {
		if (headers == null) return new HashMap<String, List<String>>();
		return headers;
	}

	public Body getBody() {
		return body;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
}
