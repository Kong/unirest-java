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

import com.mashape.unirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;
import com.mashape.unirest.http.utils.SyncIdleConnectionMonitorThread;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Unirest {

	/**
	 * Set the HttpClient implementation to use for every synchronous request
	 */
	public static void setHttpClient(HttpClient httpClient) {
		Options.setOption(Option.HTTPCLIENT, httpClient);
		Options.customClientSet();
	}

	/**
	 * Set the asynchronous AbstractHttpAsyncClient implementation to use for every asynchronous request
	 */
	public static void setAsyncHttpClient(CloseableHttpAsyncClient asyncHttpClient) {
		Options.setOption(Option.ASYNCHTTPCLIENT, asyncHttpClient);
		Options.customClientSet();
	}

	/**
	 * Set a proxy
	 */
	public static void setProxy(HttpHost proxy) {
		Options.setOption(Option.PROXY, proxy);

		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Set the ObjectMapper implementation to use for Response to Object binding
	 * 
	 * @param objectMapper Custom implementation of ObjectMapper interface
	 */
	public static void setObjectMapper(ObjectMapper objectMapper) {
		Options.setOption(Option.OBJECT_MAPPER, objectMapper);

		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Set the connection timeout and socket timeout
	 * 
	 * @param connectionTimeout The timeout until a connection with the server is established (in milliseconds). Default is 10000. Set to zero to disable the timeout.
	 * @param socketTimeout The timeout to receive data (in milliseconds). Default is 60000. Set to zero to disable the timeout.
	 */
	public static void setTimeouts(long connectionTimeout, long socketTimeout) {
		Options.setOption(Option.CONNECTION_TIMEOUT, connectionTimeout);
		Options.setOption(Option.SOCKET_TIMEOUT, socketTimeout);

		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Set the concurrency levels
	 * 
	 * @param maxTotal Defines the overall connection limit for a connection pool. Default is 200.
	 * @param maxPerRoute Defines a connection limit per one HTTP route (this can be considered a per target host limit). Default is 20.
	 */
	public static void setConcurrency(int maxTotal, int maxPerRoute) {
		Options.setOption(Option.MAX_TOTAL, maxTotal);
		Options.setOption(Option.MAX_PER_ROUTE, maxPerRoute);

		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Clear default headers
	 */
	public static void clearDefaultHeaders() {
		Options.setOption(Option.DEFAULT_HEADERS, null);
	}

	/**
	 * Set default header
	 */
	@SuppressWarnings("unchecked")
	public static void setDefaultHeader(String name, String value) {
		Object headers = Options.getOption(Option.DEFAULT_HEADERS);
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		((Map<String, String>) headers).put(name, value);
		Options.setOption(Option.DEFAULT_HEADERS, headers);
	}

	/**
	 * Close the asynchronous client and its event loop. Use this method to close all the threads and allow an application to exit.
	 */
	public static void shutdown() throws IOException {
		// Closing the Sync HTTP client
		CloseableHttpClient syncClient = (CloseableHttpClient) Options.getOption(Option.HTTPCLIENT);
		if (syncClient != null) {
			syncClient.close();
		}

		SyncIdleConnectionMonitorThread syncIdleConnectionMonitorThread = (SyncIdleConnectionMonitorThread) Options.getOption(Option.SYNC_MONITOR);
		if (syncIdleConnectionMonitorThread != null) {
			syncIdleConnectionMonitorThread.interrupt();
		}

		// Closing the Async HTTP client (if running)
		CloseableHttpAsyncClient asyncClient = (CloseableHttpAsyncClient) Options.getOption(Option.ASYNCHTTPCLIENT);
		if (asyncClient != null && asyncClient.isRunning()) {
			asyncClient.close();
		}

		AsyncIdleConnectionMonitorThread asyncMonitorThread = (AsyncIdleConnectionMonitorThread) Options.getOption(Option.ASYNC_MONITOR);
		if (asyncMonitorThread != null) {
			asyncMonitorThread.interrupt();
		}
	}

	public static GetRequest get(String url) {
		return new GetRequest(HttpMethod.GET, url);
	}

	public static GetRequest head(String url) {
		return new GetRequest(HttpMethod.HEAD, url);
	}

	public static HttpRequestWithBody options(String url) {
		return new HttpRequestWithBody(HttpMethod.OPTIONS, url);
	}

	public static HttpRequestWithBody post(String url) {
		return new HttpRequestWithBody(HttpMethod.POST, url);
	}

	public static HttpRequestWithBody delete(String url) {
		return new HttpRequestWithBody(HttpMethod.DELETE, url);
	}

	public static HttpRequestWithBody patch(String url) {
		return new HttpRequestWithBody(HttpMethod.PATCH, url);
	}

	public static HttpRequestWithBody put(String url) {
		return new HttpRequestWithBody(HttpMethod.PUT, url);
	}

}
