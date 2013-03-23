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

package com.mashape.unicorn.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class HttpResponse<T> {

	private int code;
	private Map<String, String> headers;
	private InputStream rawBody;
	private T body;

	@SuppressWarnings("unchecked")
	public HttpResponse(org.apache.http.HttpResponse response, Class<T> responseClass) {
		HttpEntity responseEntity = response.getEntity();
		
		Header[] allHeaders = response.getAllHeaders();
		this.headers = new HashMap<String, String>();
		for(Header header : allHeaders) {
			headers.put(header.getName(), header.getValue());
		}
		this.code = response.getStatusLine().getStatusCode();
		
		if (responseEntity != null) {
			try {
				byte[] rawBody;
				try {
					rawBody = getBytes(responseEntity.getContent());
				} catch (IOException e2) {
					throw new RuntimeException(e2);
				}
				InputStream inputStream = new ByteArrayInputStream(rawBody);
				this.rawBody = inputStream;

				if (JsonNode.class.equals(responseClass)) {
					String jsonString = new String(rawBody).trim();
					this.body = (T) new JsonNode(jsonString);
				} else if (String.class.equals(responseClass)) {
					this.body = (T) new String(rawBody);
				} else if (InputStream.class.equals(responseClass)) {
					this.body = (T) this.rawBody;
				} else {
					throw new Exception("Unknown result type. Only String, JsonNode and InputStream are supported.");
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static byte[] getBytes(InputStream is) throws IOException {
		int len;
		int size = 1024;
		byte[] buf;

		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			while ((len = is.read(buf, 0, size)) != -1)
				bos.write(buf, 0, len);
			buf = bos.toByteArray();
		}
		return buf;
	}

	public int getCode() {
		return code;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public InputStream getRawBody() {
		return rawBody;
	}

	public T getBody() {
		return body;
	}

}
