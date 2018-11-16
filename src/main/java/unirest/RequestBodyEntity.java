/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package unirest;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class RequestBodyEntity extends BaseRequest<HttpRequestUniBody> implements HttpRequestUniBody {

	private final HttpRequestWithBody request;
	private Supplier<HttpEntity> body = () -> new StringEntity("", StandardCharsets.UTF_8);

	RequestBodyEntity(HttpRequestWithBody httpRequest) {
		super(httpRequest);
		request = httpRequest;
	}

	@Override
	public HttpRequestUniBody body(byte[] bodyBytes) {
		this.body = () -> new ByteArrayEntity(bodyBytes);
		return this;
	}

	@Override
	public HttpRequestUniBody body(String bodyAsString) {
		this.body = () -> new StringEntity(bodyAsString, request.getCharset());
		return this;
	}

	@Override
	public HttpRequestUniBody body(JsonNode jsonBody) {
		return body(jsonBody.toString());
	}

	@Override
	public HttpRequestUniBody charset(Charset charset) {
		request.charset(charset);
		return this;
	}

	@Override
	public HttpEntity getEntity() {
		return body.get();
	}
}
