/*
The MIT License

Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.

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

package io.github.openunirest.request;

import io.github.openunirest.http.async.Callback;
import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.JsonNode;
import io.github.openunirest.http.exceptions.UnirestException;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public abstract class BaseRequest {

	protected static final Charset UTF_8 = StandardCharsets.UTF_8;
	private final ResponseBuilder builder = new ResponseBuilder();

	protected HttpRequest httpRequest;

	protected BaseRequest(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public HttpRequest getHttpRequest() {
		return this.httpRequest;
	}

	protected BaseRequest() {
		super();
	}

	public HttpResponse<String> asString() throws UnirestException {
		return HttpClientHelper.request(httpRequest, builder::asString);
	}

	public CompletableFuture<HttpResponse<String>> asStringAsync() {
		return HttpClientHelper.requestAsync(httpRequest, builder::asString);
	}

	public CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback) {
		return HttpClientHelper.requestAsync(httpRequest, builder::asString, callback);
	}

	public HttpResponse<JsonNode> asJson() throws UnirestException {
		return HttpClientHelper.request(httpRequest, builder::asJson);
	}

	public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync() {
		return HttpClientHelper.requestAsync(httpRequest, builder::asJson);
	}

	public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback) {
		return HttpClientHelper.requestAsync(httpRequest, builder::asJson, callback);
	}

	public <T> HttpResponse<T> asObject(Class<? extends T> responseClass) throws UnirestException {
		return HttpClientHelper.request(httpRequest, r -> builder.asObject(r, responseClass));
	}

	public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass) {
		return HttpClientHelper.requestAsync(httpRequest, r -> builder.asObject(r, responseClass));
	}

	public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass, Callback<T> callback) {
		return HttpClientHelper.requestAsync(httpRequest, r -> builder.asObject(r, responseClass), callback);
	}

	public HttpResponse<InputStream> asBinary() throws UnirestException {
		return HttpClientHelper.request(httpRequest, builder::asBinary);
	}

	public CompletableFuture<HttpResponse<InputStream>> asBinaryAsync() {
		return HttpClientHelper.requestAsync(httpRequest, builder::asBinary);
	}

	public CompletableFuture<HttpResponse<InputStream>> asBinaryAsync(Callback<InputStream> callback) {
		return HttpClientHelper.requestAsync(httpRequest, builder::asBinary, callback);
	}
}
