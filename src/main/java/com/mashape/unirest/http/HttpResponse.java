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

package com.mashape.unirest.http;

import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;
import com.mashape.unirest.http.utils.ResponseUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class HttpResponse<T> {

	private int statusCode;
	private String statusText;
	private Headers headers = new Headers();
	private InputStream rawBody;
	private T body;

	@SuppressWarnings("unchecked")
	public HttpResponse(org.apache.http.HttpResponse response, Class<T> responseClass) {
		HttpEntity responseEntity = response.getEntity();
		ObjectMapper objectMapper = (ObjectMapper) Options.getOption(Option.OBJECT_MAPPER);

		Header[] allHeaders = response.getAllHeaders();
		for (Header header : allHeaders) {
			String headerName = header.getName();
			List<String> list = headers.get(headerName);
			if (list == null)
				list = new ArrayList<String>();
			list.add(header.getValue());
			headers.put(headerName, list);
		}
		StatusLine statusLine = response.getStatusLine();
		this.statusCode = statusLine.getStatusCode();
		this.statusText = statusLine.getReasonPhrase();

		if (responseEntity != null) {
			String charset = "UTF-8";

			Header contentType = responseEntity.getContentType();
			if (contentType != null) {
				String responseCharset = ResponseUtils.getCharsetFromContentType(contentType.getValue());
				if (responseCharset != null && !responseCharset.trim().equals("")) {
					charset = responseCharset;
				}
			}

			try {
				InputStream responseInputStream = ResponseUtils.isGzipped(responseEntity.getContentEncoding())
					? new GZIPInputStream(responseEntity.getContent())
					: responseEntity.getContent();

				if (InputStream.class.equals(responseClass)) {
					this.body = (T) responseInputStream;
					this.rawBody = responseInputStream;
				} else {
					byte[] rawBytes = ResponseUtils.getBytes(responseInputStream);
					this.rawBody = new ByteArrayInputStream(rawBytes);
					if (JsonNode.class.equals(responseClass)) {
						String jsonString = new String(rawBytes, charset).trim();
						this.body = (T) new JsonNode(jsonString);
					} else if (String.class.equals(responseClass)) {
						this.body = (T) new String(rawBytes, charset);
					} else if (objectMapper != null) {
						this.body = objectMapper.readValue(new String(rawBytes, charset), responseClass);
					} else {
						throw new Exception("Only String, JsonNode and InputStream are supported, or an ObjectMapper implementation is required.");
					}
					EntityUtils.consumeQuietly(responseEntity);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
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
}
