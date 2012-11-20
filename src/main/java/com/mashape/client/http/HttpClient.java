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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;
import com.mashape.client.authentication.Authentication;
import com.mashape.client.authentication.HeaderAuthentication;
import com.mashape.client.authentication.OAuth10aAuthentication;
import com.mashape.client.authentication.OAuth2Authentication;
import com.mashape.client.authentication.OAuthAuthentication;
import com.mashape.client.authentication.QueryAuthentication;
import com.mashape.client.http.utils.HttpUtils;
import com.mashape.client.http.utils.MapUtil;

public class HttpClient {
	
	private static final String USER_AGENT = "mashape-java/2.0";
	public static final String JSON_PARAM_BODY = "88416847677069008618"; // Just a random value
	
	private static Gson gson;
	
	static {
		gson = new Gson();
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
		    new X509TrustManager() {
		        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		        }
		        public void checkClientTrusted(
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		        public void checkServerTrusted(
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		    }
		};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}
	}
	
	public static <T> Thread doRequest(Class<T> clazz, HttpMethod httpMethod, String url, Map<String, Object> parameters, ContentType contentType, ResponseType responseType, List<Authentication> authenticationHandlers, MashapeCallback<T> callback) {
		Thread t = new HttpRequestThread<T>(clazz, httpMethod, url, parameters, contentType, responseType, authenticationHandlers, callback);
		t.start();
		return t;
	}
	
	public static <T> MashapeResponse<T> doRequest (Class<T> clazz, HttpMethod httpMethod, String url, Map<String, Object> parameters, ContentType contentType, ResponseType responseType, List<Authentication> authenticationHandlers) {
		if (authenticationHandlers == null) authenticationHandlers = new ArrayList<Authentication>();
		if (parameters == null) parameters = new HashMap<String, Object>();
		
		List<Header> headers = new LinkedList<Header>();
		
		// Handle authentications
		for (Authentication authentication : authenticationHandlers) {
			if (authentication instanceof HeaderAuthentication) {
				headers.addAll(authentication.getHeaders());
			} else {
				Map<String, String> queryParameters = authentication.getQueryParameters();
				if (authentication instanceof QueryAuthentication) {
					parameters.putAll(queryParameters);
				} else if (authentication instanceof OAuth10aAuthentication) {
					if (url.endsWith("/oauth_url") == false && (queryParameters == null || 
							queryParameters.get(OAuthAuthentication.ACCESS_SECRET) == null || 
							queryParameters.get(OAuthAuthentication.ACCESS_TOKEN) == null)) {
						throw new RuntimeException("Before consuming OAuth endpoint, invoke authorize('access_token','access_secret') with not null values");
					}
					headers.add(new BasicHeader("x-mashape-oauth-consumerkey", queryParameters.get(OAuthAuthentication.CONSUMER_KEY)));
					headers.add(new BasicHeader("x-mashape-oauth-consumersecret", queryParameters.get(OAuthAuthentication.CONSUMER_SECRET)));
					headers.add(new BasicHeader("x-mashape-oauth-accesstoken", queryParameters.get(OAuthAuthentication.ACCESS_TOKEN)));
					headers.add(new BasicHeader("x-mashape-oauth-accesssecret", queryParameters.get(OAuthAuthentication.ACCESS_SECRET)));
					
				} else if (authentication instanceof OAuth2Authentication) {
					if (url.endsWith("/oauth_url") == false && (queryParameters == null || 
							queryParameters.get(OAuthAuthentication.ACCESS_TOKEN) == null)) {
						throw new RuntimeException("Before consuming OAuth endpoint, invoke authorize('access_token') with a not null value");
					}
					parameters.put("access_token", queryParameters.get(OAuthAuthentication.ACCESS_TOKEN));
				}
			}
		}
		
		// Sanitize null parameters
		Set<String> keySet = new HashSet<String>(parameters.keySet());
		for (String key : keySet) {
			if (parameters.get(key) == null) {
				parameters.remove(key);
			}
		}
		
		headers.add(new BasicHeader("User-Agent", USER_AGENT));
		
		HttpUriRequest request = null;
		
		switch(httpMethod) {
		case GET:
			request = new HttpGet(url + "?" + HttpUtils.getQueryString(parameters));
			break;
		case POST:
			request = new HttpPost(url);
			break;
		case PUT:
			request = new HttpPut(url);
			break;
		case DELETE:
			request = new HttpDeleteWithBody(url);
			break;
		case PATCH:
			request = new HttpPatchWithBody(url);
			break;
		}
		
		for(Header header : headers) {
			request.addHeader(header);
		}

		if (httpMethod != HttpMethod.GET) {
			switch(contentType) {
			case BINARY:
				MultipartEntity entity = new MultipartEntity();
				for(Entry<String, Object> parameter : parameters.entrySet()) {
					if (parameter.getValue() instanceof File) {
						entity.addPart(parameter.getKey(), new FileBody((File) parameter.getValue()));
					} else {
						try {
							entity.addPart(parameter.getKey(), new StringBody(parameter.getValue().toString(), Charset.forName("UTF-8")));
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						}
					}
				}
				((HttpEntityEnclosingRequestBase) request).setEntity(entity);
				break;
			case FORM:
				try {
					((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(MapUtil.getList(parameters), HTTP.UTF_8));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				break;
			case JSON:
				String jsonBody = null;
				
				if((parameters.get(JSON_PARAM_BODY) == null)) {
					String jsonParamBody =  parameters.get(JSON_PARAM_BODY).toString();
					jsonBody = (HttpUtils.isJson(jsonParamBody)) ? jsonParamBody : gson.toJson(jsonParamBody);
				}

				try {
					((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(jsonBody, "UTF-8"));
					((HttpEntityEnclosingRequestBase) request).setHeader(new BasicHeader("Content-Type", "application/json"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		
		HttpResponse response;
		try {
			response = client.execute(request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		MashapeResponse<T> mashapeResponse = HttpUtils.getResponse(responseType, response);
		return mashapeResponse;
	}

}
