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

package com.mashape.unicorn.request;

import java.io.InputStream;

import com.mashape.unicorn.http.HttpClientHelper;
import com.mashape.unicorn.http.HttpResponse;
import com.mashape.unicorn.http.JsonNode;
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
