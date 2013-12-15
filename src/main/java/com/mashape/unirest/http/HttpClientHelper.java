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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;

import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;
import com.mashape.unirest.http.utils.ClientFactory;
import com.mashape.unirest.request.HttpRequest;

public class HttpClientHelper {

	private static final String USER_AGENT = "unirest-java/1.1";

	private static <T> FutureCallback<org.apache.http.HttpResponse> prepareCallback(final Class<T> responseClass,
			final Callback<T> callback) {
		if (callback == null)
			return null;

		return new FutureCallback<org.apache.http.HttpResponse>() {

			public void cancelled() {
				callback.cancelled();
			}

			public void completed(org.apache.http.HttpResponse arg0) {
				callback.completed(new HttpResponse<T>(arg0, responseClass));
			}

			public void failed(Exception arg0) {
				callback.failed(new UnirestException(arg0));
			}

		};
	}

	public static <T> Future<HttpResponse<T>> requestAsync(HttpRequest request, final Class<T> responseClass, Callback<T> callback) {
		return requestAsync(request, responseClass, callback, null);
	}
	
	public static <T> Future<HttpResponse<T>> requestAsync(HttpRequest request, final Class<T> responseClass, Callback<T> callback, final String charset) {
		HttpUriRequest requestObj = prepareRequest(request);

		CloseableHttpAsyncClient asyncHttpClient = ClientFactory.getAsyncHttpClient();
		if (!asyncHttpClient.isRunning()) {
			asyncHttpClient.start();
		}

		final Future<org.apache.http.HttpResponse> future = asyncHttpClient.execute(requestObj,
				prepareCallback(responseClass, callback));

		return new Future<HttpResponse<T>>() {

			public boolean cancel(boolean mayInterruptIfRunning) {
				return future.cancel(mayInterruptIfRunning);
			}

			public boolean isCancelled() {
				return future.isCancelled();
			}

			public boolean isDone() {
				return future.isDone();
			}

			public HttpResponse<T> get() throws InterruptedException, ExecutionException {
				org.apache.http.HttpResponse httpResponse = future.get();
				return new HttpResponse<T>(httpResponse, responseClass, charset);
			}

			public HttpResponse<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
					TimeoutException {
				org.apache.http.HttpResponse httpResponse = future.get(timeout, unit);
				return new HttpResponse<T>(httpResponse, responseClass, charset);
			}
		};
	}

	public static <T> HttpResponse<T> request(HttpRequest request, Class<T> responseClass) throws UnirestException {
		return request(request, responseClass, null);
	}
	
	public static <T> HttpResponse<T> request(HttpRequest request, Class<T> responseClass, String charset) throws UnirestException {
		HttpRequestBase requestObj = prepareRequest(request);
		HttpClient client = ClientFactory.getHttpClient(); // The
															// DefaultHttpClient
															// is thread-safe
		
		org.apache.http.HttpResponse response;
		try {
			response = client.execute(requestObj);
			HttpResponse<T> httpResponse = new HttpResponse<T>(response, responseClass, charset);
			requestObj.releaseConnection();
			return httpResponse;
		} catch (Exception e) {
			throw new UnirestException(e);
		} finally {
			requestObj.releaseConnection();
		}
	}

	private static HttpRequestBase prepareRequest(HttpRequest request) {

		request.header("user-agent", USER_AGENT);
		request.header("accept-encoding", "gzip");

		Object defaultHeaders = Options.getOption(Option.DEFAULT_HEADERS);
		if (defaultHeaders != null) {
			@SuppressWarnings("unchecked")
			Set<Entry<String, String>> entrySet = ((Map<String, String>) defaultHeaders).entrySet();
			for (Entry<String, String> entry : entrySet) {
				request.header(entry.getKey(), entry.getValue());
			}
		}
		
		HttpRequestBase reqObj = null;

		switch (request.getHttpMethod()) {
		case GET:
			reqObj = new HttpGet(request.getUrl());
			break;
		case POST:
			reqObj = new HttpPost(request.getUrl());
			break;
		case PUT:
			reqObj = new HttpPut(request.getUrl());
			break;
		case DELETE:
			reqObj = new HttpDeleteWithBody(request.getUrl());
			break;
		case PATCH:
			reqObj = new HttpPatchWithBody(request.getUrl());
			break;
		}

		for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
			reqObj.addHeader(entry.getKey(), entry.getValue());
		}

		// Set body
		if (request.getHttpMethod() != HttpMethod.GET) {
			if (request.getBody() != null) {
				((HttpEntityEnclosingRequestBase) reqObj).setEntity(request.getBody().getEntity());
			}
		}

		return reqObj;
	}

}
