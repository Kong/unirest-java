/*
 *
 * Mashape Java Client library.
 * Copyright (C) 2011 Mashape, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * The author of this software is Mashape, Inc.
 * For any question or feedback please contact us at: support@mashape.com
 *
 */

package com.mashape.client.http;

import java.util.List;
import java.util.Map;

import com.mashape.client.authentication.Authentication;

class HttpRequestThread<T> extends Thread {

	private HttpMethod httpMethod;
	private Class<T> clazz;
	private String url;
	private Map<String, Object> parameters;
	private ContentType contentType;
	private ResponseType responseType;
	private List<Authentication> authenticationHandlers;
	private MashapeCallback<T> callback;

	public HttpRequestThread(Class<T> clazz, HttpMethod httpMethod, String url,
			Map<String, Object> parameters, ContentType contentType,
			ResponseType responseType,
			List<Authentication> authenticationHandlers,
			MashapeCallback<T> callback) {
		this.httpMethod = httpMethod;
		this.clazz = clazz;
		this.url = url;
		this.parameters = parameters;
		this.contentType = contentType;
		this.responseType = responseType;
		this.authenticationHandlers = authenticationHandlers;
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			MashapeResponse<T> response = HttpClient.doRequest(clazz, httpMethod, url,
					parameters, contentType, responseType,
					authenticationHandlers);
			if (callback != null) {
				callback.requestCompleted(response);
			}
		} catch (Exception e) {
			if (callback != null) {
				callback.errorOccurred(e);
			} else {
				throw new RuntimeException(e);
			}
		}
	}

}
