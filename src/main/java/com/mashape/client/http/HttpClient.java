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

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.client.exceptions.ExceptionConstants;
import com.mashape.client.exceptions.MashapeClientException;
import com.mashape.client.http.auth.Auth;
import com.mashape.client.http.auth.HeaderAuth;
import com.mashape.client.http.auth.QueryAuth;
import com.mashape.client.http.callback.MashapeCallback;
import com.mashape.client.http.response.MashapeResponse;
import com.mashape.client.http.ssl.SSLVerifierFactory;
import com.mashape.client.http.utils.MapUtil;
import com.mashape.client.http.utils.RequestPrepareResult;
import com.mashape.client.http.utils.StreamUtils;
import com.mashape.client.http.utils.UrlUtils;

public class HttpClient {

	public static Thread doRequest(HttpMethod httpMethod, String url, Map<String, Object> parameters, ContentType contentType, boolean encodeJson, List<Auth> authHandlers, MashapeCallback callback) {
		Thread t = new HttpRequestThread(httpMethod, url, parameters, contentType, encodeJson, authHandlers, callback);
		t.start();
		return t;
	}

	@SuppressWarnings("rawtypes")
	public static MashapeResponse doRequest(HttpMethod httpMethod, String url, Map<String, Object> parameters, ContentType contentType, boolean encodeJson, List<Auth> authHandlers) throws MashapeClientException {
		return execRequest(httpMethod, url, parameters, authHandlers, contentType, encodeJson, false, null, null);
	}

	@SuppressWarnings("rawtypes")
	public static MashapeResponse doRequest(HttpMethod httpMethod, String url, Map<String, Object> parameters, ContentType contentType, String clientName, String clientVersion, List<Auth> authHandlers) throws MashapeClientException {
		return execRequest(httpMethod, url, parameters, authHandlers, contentType, false, true, clientName, clientVersion);
	}

	@SuppressWarnings("rawtypes")
	static MashapeResponse execRequest(HttpMethod httpMethod, String url, Map<String, Object> parameters, List<Auth> authHandlers, ContentType contentType, boolean encodeJson, boolean isConsole, String clientName, String clientVersion) throws MashapeClientException {
		if (authHandlers == null) {
			authHandlers = new ArrayList<Auth>();
		}
		if (parameters == null) {
			parameters = new HashMap<String, Object>();
		}
		List<Header> clientHeaders = new LinkedList<Header>();
		String boundary = null;
		if (contentType.equals(ContentType.MULTIPART)) {
			boundary = "mashape-" + UUID.randomUUID().toString();
		}
		clientHeaders = UrlUtils.generateClientHeaders(contentType, boundary);

		// Handle all other auths
		for (Auth authHandler : authHandlers) {
			if (authHandler instanceof HeaderAuth) {
				clientHeaders.add(authHandler.handleHeader());
			} else if (authHandler instanceof QueryAuth) {
				parameters.putAll(authHandler.handleParams());
			}
		}

		RequestPrepareResult prepareRequest = null;
		try {
			prepareRequest = UrlUtils.prepareRequest(url, parameters, (httpMethod == HttpMethod.GET) ? false : true);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		HttpUriRequest request;

		switch (httpMethod) {
		case GET:
			request = new HttpGet(prepareRequest.getUrl());
			break;
		case POST:
			request = new HttpPost(prepareRequest.getUrl());
			break;
		case PUT:
			request = new HttpPut(prepareRequest.getUrl());
			break;
		case DELETE:
			request = new HttpDeleteWithBody(prepareRequest.getUrl());
			break;
		default:
			throw new MashapeClientException(ExceptionConstants.EXCEPTION_NOTSUPPORTED_HTTPMETHOD, ExceptionConstants.EXCEPTION_NOTSUPPORTED_HTTPMETHOD_CODE);
		}

		for (Header header : clientHeaders) {
			request.addHeader(header);
		}

		if (httpMethod != HttpMethod.GET) {
			try {
				if (contentType.equals(ContentType.MULTIPART)) {
					MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, boundary, Charset.forName("UTF-8"));
					for (String key : parameters.keySet()) {
						Object value = parameters.get(key);
						if (value instanceof String) {
							entity.addPart(key, new StringBody(value.toString()));
						} else if (value instanceof File) {
							entity.addPart(key, new FileBody((File) value));
						}
					}
					((HttpEntityEnclosingRequestBase) request).setEntity(entity);
				} else {
					((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(MapUtil.getList(parameters), HTTP.UTF_8));
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		configureSSLHttpClient(client);

		HttpResponse httpResponse;
		try {
			httpResponse = client.execute(request);
		} catch (Exception e2) {
			throw new RuntimeException(e2);
		}
		HttpEntity entity = httpResponse.getEntity();

		if (entity != null) {
			InputStream instream;
			try {
				instream = entity.getContent();
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
			String rawResponse = StreamUtils.convertStreamToString(instream);
			if (!encodeJson) {
				return new MashapeResponse<InputStream>(httpResponse, rawResponse, instream);
			}
			try {
				// It may be an object
				return new MashapeResponse<JSONObject>(httpResponse, rawResponse, new JSONObject(rawResponse));
			} catch (JSONException e) {
				try {
					// or an array
					return new MashapeResponse<JSONArray>(httpResponse, rawResponse, new JSONArray(rawResponse));
				} catch (JSONException e1) {
					throw new MashapeClientException(String.format(ExceptionConstants.EXCEPTION_INVALID_REQUEST, rawResponse),ExceptionConstants.EXCEPTION_SYSTEM_ERROR_CODE);
				}
			}

		}
		return null;
	}

	private static void configureSSLHttpClient(org.apache.http.client.HttpClient client) {
		// Accept any SSL certificate
		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		try {
			sslContext.init(null, new TrustManager[] { SSLVerifierFactory.getCustomSSLVerifier() }, new SecureRandom());
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
		SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Scheme https = new Scheme("https", 443, socketFactory);
		client.getConnectionManager().getSchemeRegistry().register(https);
	}



}
