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

package com.mashape.unirest.request.body;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequest;

public class RequestBodyEntity extends BaseRequest implements Body {

	private Object body;

	public RequestBodyEntity(HttpRequest httpRequest) {
		super(httpRequest);
	}

	public RequestBodyEntity body(String body) {
		this.body = body;
		return this;
	}

	public RequestBodyEntity body(JsonNode body) {
		this.body = body.toString();
		return this;
	}

	public Object getBody() {
		return body;
	}

	public HttpEntity getEntity() {
		return new StringEntity(body.toString(), UTF_8);
	}

}
