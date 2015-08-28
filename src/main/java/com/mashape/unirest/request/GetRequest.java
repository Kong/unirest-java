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

import java.util.Map;

import com.mashape.unirest.http.HttpMethod;

public class GetRequest extends HttpRequest {

	public GetRequest(HttpMethod method, String url) {
		super(method, url);
	}

	public GetRequest routeParam(String name, String value) {
		super.routeParam(name, value);
		return this;
	}

	@Override
	public GetRequest header(String name, String value) {
		return (GetRequest) super.header(name, value);
	}

	@Override
	public GetRequest headers(Map<String, String> headers) {
		return (GetRequest) super.headers(headers);
	}

	@Override
	public GetRequest basicAuth(String username, String password) {
		super.basicAuth(username, password);
		return this;
	}
}
