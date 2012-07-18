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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mashape.client.exceptions.MashapeClientException;
import com.mashape.client.http.auth.Auth;
import com.mashape.client.http.callback.MashapeCallback;

class HttpRequestThread extends Thread {
	
	private HttpMethod httpMethod;
	private String url;
	private Map<String, String> parameters;
	private boolean encodeJson;
	private MashapeCallback callback;
	private List<Auth> authHandlers;

	public HttpRequestThread(HttpMethod httpMethod, String url, Map<String, String> parameters, boolean encodeJson, List<Auth> authHandlers, MashapeCallback callback) {
		this.httpMethod = httpMethod;
		this.url = url;
		this.parameters = parameters;
		this.encodeJson = encodeJson;
		this.callback = callback;
		this.authHandlers = authHandlers;
	}
	
	/**
	 * Old - without auth
	 * TODO remove
	 */
	
	public HttpRequestThread(HttpMethod httpMethod, String url, Map<String, String> parameters, boolean encodeJson, MashapeCallback callback) {
		this(httpMethod, url, parameters, encodeJson, new ArrayList<Auth>(), callback);
	}
	
	@Override
	public void run() {
		Object response;
		try {
			response = HttpClient.execRequest(httpMethod, url, parameters, authHandlers, encodeJson, false, null, null);
			if (callback != null) {
				callback.requestCompleted(response);
			}
		} catch (MashapeClientException e) {
			if (callback != null) {
				callback.errorOccurred(e);
			} else {
				throw new RuntimeException(e);
			}
		}
		
	}
	
}
