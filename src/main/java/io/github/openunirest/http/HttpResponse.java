/*
The MIT License

Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.

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

package io.github.openunirest.http;

import io.github.openunirest.http.options.Option;
import io.github.openunirest.http.options.Options;
import io.github.openunirest.http.utils.ResponseUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

public class HttpResponse<T> {

	private final Headers headers;
	private final int statusCode;
	private final String statusText;

	private InputStream rawBody;
	private Optional<RuntimeException> parsingError = Optional.empty();
	private T body;

	private HttpResponse(org.apache.http.HttpResponse response){
		headers = new Headers(response.getAllHeaders());
		StatusLine statusLine = response.getStatusLine();
		this.statusCode = statusLine.getStatusCode();
		this.statusText = statusLine.getReasonPhrase();
	}

	public HttpResponse(org.apache.http.HttpResponse response, BodyData<T> data){
		this(response);
		this.rawBody = data.getRawInput();
		this.body = data.getTransFormedBody();
		this.parsingError = Optional.ofNullable(data.getParseEx());
	}

	public HttpResponse(org.apache.http.HttpResponse response, Class<T> responseClass) {
		this(response);

		ObjectMapper objectMapper = (ObjectMapper) Options.getOption(Option.OBJECT_MAPPER);
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
			String charset = ResponseUtils.getCharsetfromResponse(responseEntity);

			try {
				byte[] rawBody = ResponseUtils.getRawBody(responseEntity);
				this.rawBody = new ByteArrayInputStream(rawBody);
				
				if (JsonNode.class.equals(responseClass)) {
					tryParseAsJson(charset, rawBody);
				} else if (String.class.equals(responseClass)) {
					this.body = (T) new String(rawBody, charset);
				} else if (InputStream.class.equals(responseClass)) {
					this.body = (T) this.rawBody;
				} else if (objectMapper != null) {
					this.body = objectMapper.readValue(new String(rawBody, charset), responseClass);
				} else {
					throw new Exception("Only String, JsonNode and InputStream are supported, or an ObjectMapper implementation is required.");
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		EntityUtils.consumeQuietly(responseEntity);
	}

	private void tryParseAsJson(String charset, byte[] rawBody) throws UnsupportedEncodingException {
		try {
			String jsonString = new String(rawBody, charset).trim();
			this.body = (T) new JsonNode(jsonString);
		}catch (RuntimeException e){
			this.body = null;
			this.parsingError = Optional.of(e);
		}
	}

	public int getStatus() {
		return statusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	/**
	 * @return Response Headers (map) with <b>same case</b> as server response.
	 * For instance use <code>getHeaders().getFirst("Location")</code> and not <code>getHeaders().getFirst("location")</code> to get first header "Location"
	 */
	public Headers getHeaders() {
		return headers;
	}

	public InputStream getRawBody() {
		return rawBody;
	}

	public T getBody() {
		return body;
	}

	public Optional<RuntimeException> getParsingError() {
		return parsingError;
	}
}
