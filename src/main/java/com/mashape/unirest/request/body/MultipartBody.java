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

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import com.mashape.unirest.http.utils.MapUtil;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequest;

public class MultipartBody extends BaseRequest implements Body {

	private List<String> keyOrder = new ArrayList<String>();
	private Map<String, List<Object>> parameters = new HashMap<String, List<Object>>();
	private Map<String, ContentType> contentTypes = new HashMap<String, ContentType>();

	private boolean hasFile;
	private HttpRequest httpRequestObj;
	private HttpMultipartMode mode;

	public MultipartBody(HttpRequest httpRequest) {
		super(httpRequest);
		this.httpRequestObj = httpRequest;
	}
	
	public MultipartBody field(String name, String value) {
		return field(name, value, false, null);
	}

	public MultipartBody field(String name, String value, String contentType) {
		return field(name, value, false, contentType);
	}

	public MultipartBody field(String name, Collection<?> collection) {
		for(Object current : collection) {
			boolean isFile = current instanceof File;
			field(name, current, isFile, null);
		}
		return this;
	}
	
	public MultipartBody field(String name, Object value) {
		return field(name, value, false, null);
	}
	
	public MultipartBody field(String name, Object value, boolean file) {
		return field(name, value, file, null);
	}

	public MultipartBody field(String name, Object value, boolean file, String contentType) {
		keyOrder.add(name);
		
		List<Object> list = parameters.get(name);
		if (list == null) list = new LinkedList<Object>();
		list.add(value);
		parameters.put(name, list);
		
		ContentType type = null;
		if (contentType != null && !contentType.isEmpty()) { type = ContentType.parse(contentType); }
		else if (file) { type = ContentType.APPLICATION_OCTET_STREAM; }
		else { type = ContentType.APPLICATION_FORM_URLENCODED.withCharset(UTF_8); }
		contentTypes.put(name, type);

		if (!hasFile && file) {
			hasFile = true;
		}

		return this;
	}

	public MultipartBody field(String name, File file) {
		return field(name, file, true, null);
	}

	public MultipartBody field(String name, File file, String contentType) {
		return field(name, file, true, contentType);
	}
	
	public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
		return field(name, new InputStreamBody(stream, contentType, fileName), true, contentType.getMimeType());
	}
	
	public MultipartBody field(String name, InputStream stream, String fileName) {
		return field(name, new InputStreamBody(stream, ContentType.APPLICATION_OCTET_STREAM, fileName), true, ContentType.APPLICATION_OCTET_STREAM.getMimeType());
	}
	
	public MultipartBody field(String name, byte[] bytes, ContentType contentType, String fileName) {
		return field(name, new ByteArrayBody(bytes, contentType, fileName), true, contentType.getMimeType());
	}
	
	public MultipartBody field(String name, byte[] bytes, String fileName) {
		return field(name, new ByteArrayBody(bytes, ContentType.APPLICATION_OCTET_STREAM, fileName), true, ContentType.APPLICATION_OCTET_STREAM.getMimeType());
	}
	
	public MultipartBody basicAuth(String username, String password) {
		httpRequestObj.basicAuth(username, password);
		return this;
	}

	public MultipartBody mode(String mode) {
		this.mode = HttpMultipartMode.valueOf(mode);
		return this;
	}

	public HttpEntity getEntity() {
		if (hasFile) {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			if (mode != null) { builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE); }
			for(String key: keyOrder) {
				List<Object> value = parameters.get(key);
				ContentType contentType = contentTypes.get(key);
				for(Object cur : value) {
					if (cur instanceof File) {
						File file = (File) cur;
						builder.addPart(key, new FileBody(file, contentType, file.getName()));
					} else if (cur instanceof InputStreamBody) {
						builder.addPart(key, (ContentBody) cur);
					} else if (cur instanceof ByteArrayBody) {
						builder.addPart(key, (ContentBody) cur);
					} else {
						builder.addPart(key, new StringBody(cur.toString(), contentType));
					}
				}
			}
			return builder.build();
		} else {
			try {
				return new UrlEncodedFormEntity(MapUtil.getList(parameters), UTF_8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
