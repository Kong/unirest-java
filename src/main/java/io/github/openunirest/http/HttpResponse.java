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

package io.github.openunirest.http;

import org.apache.http.StatusLine;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

public class HttpResponse<T> {

	private final Headers headers;
	private final int statusCode;
	private final String statusText;

	private InputStream rawBody;
	private Optional<RuntimeException> parsingError = Optional.empty();
	private T body;

	private HttpResponse(org.apache.http.HttpResponse response){
		headers = new Headers(response.getAllHeaders());
		StatusLine statusLine = response.getStatusLine();
		this.statusCode = statusLine.getStatusCode();
		this.statusText = statusLine.getReasonPhrase();
	}

	public HttpResponse(org.apache.http.HttpResponse response, BodyData<T> data){
		this(response);
		this.rawBody = data.getRawInput();
		this.body = data.getTransFormedBody();
		this.parsingError = Optional.ofNullable(data.getParseEx());
	}

    public HttpResponse(org.apache.http.HttpResponse response, T body, InputStream is){
        this(response);
        this.body = body;
        this.rawBody = is;
    }

	public int getStatus() {
		return statusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	/**
	 * @return Response Headers (map) with <b>same case</b> as server response.
	 * For instance use <code>getHeaders().getFirst("Location")</code> and not <code>getHeaders().getFirst("location")</code> to get first header "Location"
	 */
	public Headers getHeaders() {
		return headers;
	}

	public InputStream getRawBody() {
		return rawBody;
	}

	public T getBody() {
		return body;
	}

	public Optional<RuntimeException> getParsingError() {
		return parsingError;
	}

	public <V> V mapBody(Function<T,V> func){
	    return func.apply(body);
    }

    public <V> V mapRawBody(Function<InputStream, V> func) {
	    return func.apply(rawBody);
    }
}
