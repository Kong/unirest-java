/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
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

package kong.unirest.core;

import kong.unirest.core.json.JSONElement;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

class HttpRequestUniBody extends BaseRequest<RequestBodyEntity> implements RequestBodyEntity {

	private BodyPart body;
	private Charset charSet;
	private ProgressMonitor monitor;

	HttpRequestUniBody(HttpRequestBody httpRequest) {
		super(httpRequest);
		this.charSet = httpRequest.getCharset();
	}

	HttpRequestUniBody(HttpRequestUniBody httpRequest) {
		super(httpRequest);
		this.charSet = httpRequest.getCharset();
		this.body = httpRequest.body;
		this.monitor = httpRequest.monitor;
	}

	@Override
	public RequestBodyEntity body(JsonNode jsonBody) {
		return body(jsonBody.toString());
	}

	@Override
	public RequestBodyEntity body(InputStream inputStreamBody) {
		this.body = new InputStreamBody(inputStreamBody);
		return this;
	}

	@Override
	public RequestBodyEntity body(JSONElement jsonBody) {
		return body(jsonBody.toString());
	}

	@Override
	public RequestBodyEntity body(Object objectBody) {
		if(objectBody instanceof String){
			return body((String)objectBody);
		} else if (objectBody instanceof JsonNode){
			return body((JsonNode) objectBody);
		} else if (objectBody instanceof JSONElement){
			return body(objectBody.toString());
		}
		return body(getObjectMapper().writeValue(objectBody));
	}

	@Override
	public RequestBodyEntity body(String bodyAsString) {
		this.body = new UnibodyString(bodyAsString);
		return this;
	}

	@Override
	public RequestBodyEntity body(byte[] bodyBytes) {
		this.body = new UniByteArrayBody(bodyBytes);
		return this;
	}

	@Override
	public RequestBodyEntity charset(Charset charset) {
		this.charSet = charset;
		return this;
	}

	@Override
	public RequestBodyEntity contentType(String type) {
		headers.add("Content-Type", type);
		return this;
	}

	@Override
	public RequestBodyEntity uploadMonitor(ProgressMonitor progressMonitor) {
		this.monitor = progressMonitor;
		return this;
	}

	@Override
	public Optional<Body> getBody() {
		return Optional.of(this);
	}

	@Override
	public Charset getCharset() {
		return charSet;
	}

	@Override
	public boolean isMultiPart() {
		return false;
	}

	@Override
	public boolean isEntityBody() {
		return true;
	}

	@Override
	public BodyPart uniPart() {
		return body;
	}

	@Override
	public ProgressMonitor getMonitor(){
		return monitor;
	}
}
