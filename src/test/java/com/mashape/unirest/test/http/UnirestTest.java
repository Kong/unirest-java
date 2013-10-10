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

package com.mashape.unirest.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.options.Options;

public class UnirestTest {

	private static final String UNEXISTING_IP = "http://192.168.1.100/";

	@Test
	public void testRequests() throws JSONException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
													 .header("accept", "application/json")
													 .field("param1", "value1")
													 .field("param2","bye")
													 .asJson();
		
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getCode());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
	}
	
	@Test
	public void testGet() throws JSONException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get?name=mark").asJson();
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("name"), "mark");
		
		response = Unirest.get("http://httpbin.org/get").field("name", "mark2").asJson();
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("name"), "mark2");
	}
	
	@Test
	public void testBasicAuth() throws JSONException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/headers").basicAuth("user", "test").asJson();
		assertEquals(response.getBody().getObject().getJSONObject("headers").getString("Authorization"), "Basic dXNlcjp0ZXN0");
	}
	
	@Test
	public void testAsync() throws JSONException, InterruptedException, ExecutionException {
		Future<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post")
				 .header("accept", "application/json")
				 .field("param1", "value1")
				 .field("param2","bye")
				 .asJsonAsync();
		
		assertNotNull(future);
		HttpResponse<JsonNode> jsonResponse = future.get();
		
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getCode());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
	}
	
	@Test
	public void testMultipart() throws JSONException, InterruptedException, ExecutionException, URISyntaxException {
		HttpResponse<JsonNode> jsonResponse =
				Unirest.post("http://httpbin.org/post")
				.field("file", new File(getClass().getResource("/test").toURI())).asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getCode());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
	}
	
	@Test
	public void testSetTimeouts() {
		long start = System.currentTimeMillis();
		try {
			Unirest.get(UNEXISTING_IP).asString();
		} catch (Exception e) {
			if (System.currentTimeMillis() - start > Options.CONNECTION_TIMEOUT + 100) { // Add 100ms for code execution
				fail();
			}
		}
		Unirest.setTimeouts(2000, 10000);
		start = System.currentTimeMillis();
		try {
			Unirest.get(UNEXISTING_IP).asString();
		} catch (Exception e) {
			if (System.currentTimeMillis() - start > 2100) { // Add 100ms for code execution
				fail();
			}
		}
	}
}
