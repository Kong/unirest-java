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

package kong.unirest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

class HttpRequestBody extends BaseRequest<HttpRequestWithBody> implements HttpRequestWithBody {

	private Charset charSet = StandardCharsets.UTF_8;

	public HttpRequestBody(Config config, HttpMethod method, String url) {
		super(config, method, url);
	}

	@Override
	public MultipartBody field(String name, Collection<?> value) {
		return new HttpRequestMultiPart(this).field(name, value);
	}

	@Override
	public MultipartBody field(String name, File file) {
		return field(name, file, null);
	}

	@Override
	public MultipartBody field(String name, File file, String contentType) {
		return new HttpRequestMultiPart(this).field(name, file, contentType);
	}

	@Override
	public MultipartBody field(String name, Object value) {
		return field(name, value, null);
	}

	@Override
	public MultipartBody field(String name, Object value, String contentType) {
		return new HttpRequestMultiPart(this).field(name, value, contentType);
	}

	@Override
	public MultipartBody fields(Map<String, Object> parameters) {
		return new HttpRequestMultiPart(this).fields(parameters);
	}

	@Override
	public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
		return new HttpRequestMultiPart(this).field(name, stream, contentType, fileName);
	}

	@Override
	public MultipartBody multiPartContent() {
		return new HttpRequestMultiPart(this).forceMultiPart(true);
	}

	@Override
	public MultipartBody field(String name, InputStream stream, String fileName) {
		return field(name, stream, ContentType.APPLICATION_OCTET_STREAM, fileName);
	}

	@Override
	public HttpRequestBody charset(Charset charset) {
		this.charSet = charset;
		return this;
	}

	@Override
	public RequestBodyEntity body(JsonNode body) {
		return body(body.toString());
	}

	@Override
	public RequestBodyEntity body(String body) {
		return new HttpRequestUniBody(this).body(body);
	}

	@Override
	public RequestBodyEntity body(Object body) {
		return body(config.getObjectMapper().writeValue(body));
	}

	@Override
	public RequestBodyEntity body(byte[] body) {
		return new HttpRequestUniBody(this).body(body);
	}

	/**
	 * Sugar method for body operation
	 *
	 * @param body raw org.JSONObject
	 * @return RequestBodyEntity instance
	 */
	@Override
	public RequestBodyEntity body(JSONObject body) {
		return body(body.toString());
	}

	/**
	 * Sugar method for body operation
	 *
	 * @param body raw org.JSONArray
	 * @return RequestBodyEntity instance
	 */
	@Override
	public RequestBodyEntity body(JSONArray body) {
		return body(body.toString());
	}

	@Override
	public Charset getCharset() {
		return charSet;
	}

	void setCharset(Charset charset) {
		this.charSet = charset;
	}

	@Override
	public Optional<Body> getBody() {
		return Optional.empty();
	}
}
