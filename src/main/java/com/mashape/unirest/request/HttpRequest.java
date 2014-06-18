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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.utils.Base64Coder;
import com.mashape.unirest.http.utils.URLParamEncoder;
import com.mashape.unirest.request.body.Body;

public class HttpRequest extends BaseRequest {

	private HttpMethod httpMethod;
	protected String url;
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	protected Body body;
	
	public HttpRequest(HttpMethod method, String url) {
		this.httpMethod = method;
		this.url = url;
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
	
}
