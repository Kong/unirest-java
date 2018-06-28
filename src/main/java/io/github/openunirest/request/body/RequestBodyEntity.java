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

package io.github.openunirest.request.body;

import io.github.openunirest.http.JsonNode;
import io.github.openunirest.request.BaseRequest;
import io.github.openunirest.request.HttpRequestWithBody;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;

public class RequestBodyEntity extends BaseRequest implements Body {

	private final HttpRequestWithBody request;
	private Object body;

	public RequestBodyEntity(HttpRequestWithBody httpRequest) {
		super(httpRequest);
		request = httpRequest;
	}

	public RequestBodyEntity body(String bodyAsString) {
		this.body = bodyAsString;
		return this;
	}

	public RequestBodyEntity body(JsonNode jsonBody) {
		this.body = jsonBody.toString();
		return this;
	}

	public Object getBody() {
		return body;
	}

	public HttpEntity getEntity() {
		return new StringEntity(body.toString(), request.getCharset());
	}

	public RequestBodyEntity charset(Charset charset) {
		request.charset(charset);
		return this;
	}
}
