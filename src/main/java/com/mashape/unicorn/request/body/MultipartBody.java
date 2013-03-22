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

package com.mashape.unicorn.request.body;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

import com.mashape.unicorn.http.utils.MapUtil;
import com.mashape.unicorn.request.BaseRequest;
import com.mashape.unicorn.request.HttpRequest;

public class MultipartBody extends BaseRequest implements Body {

	private Map<String, Object> parameters = new HashMap<String, Object>();

	private boolean hasFile;
	
	public MultipartBody(HttpRequest httpRequest) {
		super(httpRequest);
	}
	
	public MultipartBody field(String name, String value) {
		parameters.put(name, value);
		return this;
	}
	
	public MultipartBody field(String name, File file) {
		this.parameters.put(name, file);
		hasFile = true;
		return this;
	}
	
	public HttpEntity getEntity() {
		if (hasFile) {
			MultipartEntity entity = new MultipartEntity();
			for(Entry<String, Object> part : parameters.entrySet()) {
				if (part.getValue() instanceof File) {
					hasFile = true;
					entity.addPart(part.getKey(), new FileBody((File) part.getValue()));
				} else {
					try {
						entity.addPart(part.getKey(), new StringBody(part.getValue().toString(), Charset.forName("UTF-8")));
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				}
			}
			return entity;
		} else {
			try {
				return new UrlEncodedFormEntity(MapUtil.getList(parameters), HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
