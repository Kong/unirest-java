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
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.options.Options;

public class UnirestTest {

	private CountDownLatch lock;
	private boolean status;
	
	@Before
	public void setUp() {
		lock = new CountDownLatch(1);
		status = false;
	}
	
	private String findAvailableIpAddress() throws UnknownHostException, IOException {
		for(int i = 100;i<=255;i++) {
			String ip = "192.168.1." + i;
			if (!InetAddress.getByName(ip).isReachable(1000)) {
				return ip;
			}
		}
		
		throw new RuntimeException("Couldn't find an available IP address in the range of 192.168.0.100-255");
	}
	
	@Test
	public void testRequests() throws JSONException, UnirestException {
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
	public void testGet() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get?name=mark").asJson();
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("name"), "mark");
		
		response = Unirest.get("http://httpbin.org/get").field("name", "mark2").asJson();
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("name"), "mark2");
	}
	
	@Test
	public void testGetMultiple() throws JSONException, UnirestException { 
		for(int i=1;i<=20;i++) {
			HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get?try=" + i).asJson();
			assertEquals(response.getBody().getObject().getJSONObject("args").getString("try"), ((Integer)i).toString());
		}
	}
	
	@Test
	public void testGetFields() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get").field("name", "mark").field("nick", "thefosk").asJson();
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("name"), "mark");
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("nick"), "thefosk");
	}
	
	@Test
	public void testDelete() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.delete("http://httpbin.org/delete").asJson();
		assertEquals(200, response.getCode());
	
		//TODO: Uncomment when https://github.com/Mashape/unirest-java/issues/36 has been fixed
//		response = Unirest.delete("http://httpbin.org/delete").field("name", "mark").asJson();
//		assertEquals("name=mark", response.getBody().getObject().getString("data"));
	}
	
	@Test
	public void testDeleteBody() throws JSONException, UnirestException {
		String body = "{\"jsonString\":{\"members\":\"members1\"}}";
		HttpResponse<JsonNode> response = Unirest.delete("http://httpbin.org/delete").body(body).asJson();
		assertEquals(200, response.getCode());
		assertEquals(body, response.getBody().getObject().getString("data"));
	}
	
	@Test
	public void testBasicAuth() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/headers").basicAuth("user", "test").asJson();
		assertEquals( "Basic dXNlcjp0ZXN0", response.getBody().getObject().getJSONObject("headers").getString("Authorization"));
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
	public void testAsyncCallback() throws JSONException, InterruptedException, ExecutionException {
		Unirest.post("http://httpbin.org/post")
		 .header("accept", "application/json")
		 .field("param1", "value1")
		 .field("param2","bye")
		 .asJsonAsync(new Callback<JsonNode>() {
			
			public void failed(UnirestException e) {
				fail();
			}
			
			public void completed(HttpResponse<JsonNode> jsonResponse) {
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
				
				assertEquals("value1", json.getObject().getJSONObject("form").getString("param1"));
				assertEquals("bye", json.getObject().getJSONObject("form").getString("param2"));
				
				status = true;
				lock.countDown();
			}
			
			public void cancelled() {
				fail();
			}
		});
		
		lock.await(10, TimeUnit.SECONDS);
		assertTrue(status);
	}
	
	@Test
	public void testMultipart() throws JSONException, InterruptedException, ExecutionException, URISyntaxException, UnirestException {
		HttpResponse<JsonNode> jsonResponse =
				Unirest.post("http://httpbin.org/post")
				.field("name", "Mark")
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
		assertNotNull(json.getObject().getJSONObject("files"));
		
		assertEquals("This \nis \na \ntest \nfile", json.getObject().getJSONObject("files").getString("file"));
		assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
	}
	
	@Test
	public void testMultipartAsync() throws JSONException, InterruptedException, ExecutionException, URISyntaxException, UnirestException {
		Unirest.post("http://httpbin.org/post")
		.field("name", "Mark")
		.field("file", new File(getClass().getResource("/test").toURI())).asJsonAsync(new Callback<JsonNode>() {
			
			public void failed(UnirestException e) {
				fail();
			}
			
			public void completed(HttpResponse<JsonNode> response) {
				assertTrue(response.getHeaders().size() > 0);
				assertTrue(response.getBody().toString().length() > 0);
				assertFalse(response.getRawBody() == null);
				assertEquals(200, response.getCode());
				
				JsonNode json = response.getBody();
				assertFalse(json.isArray());
				assertNotNull(json.getObject());
				assertNotNull(json.getArray());
				assertEquals(1, json.getArray().length());
				assertNotNull(json.getArray().get(0));
				
				assertEquals("This \nis \na \ntest \nfile", json.getObject().getJSONObject("files").getString("file"));
				assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
				
				status = true;
				lock.countDown();
			}
			
			public void cancelled() {
				fail();
			}
			
		});
		
		lock.await(10, TimeUnit.SECONDS);
		assertTrue(status);
	}
	
	@Test
	public void testGzip() throws UnirestException, JSONException {
		HttpResponse<JsonNode> jsonResponse =
				Unirest.get("http://httpbin.org/gzip").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getCode());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertTrue(json.getObject().getBoolean("gzipped"));
	}
	
	@Test
	public void testGzipAsync() throws UnirestException, JSONException, InterruptedException, ExecutionException {
		HttpResponse<JsonNode> jsonResponse =
				Unirest.get("http://httpbin.org/gzip").asJsonAsync().get();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getCode());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertTrue(json.getObject().getBoolean("gzipped"));
	}
	
	@Test
	public void testDefaultHeaders() throws UnirestException, JSONException {
		Unirest.setDefaultHeader("X-Custom-Header", "hello");
		
		HttpResponse<JsonNode> jsonResponse =
				Unirest.get("http://httpbin.org/headers").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getCode());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertTrue(jsonResponse.getBody().getObject().getJSONObject("headers").has("X-Custom-Header"));
		assertEquals("hello", json.getObject().getJSONObject("headers").getString("X-Custom-Header"));
		
		jsonResponse = Unirest.get("http://httpbin.org/headers").asJson();
		assertTrue(jsonResponse.getBody().getObject().getJSONObject("headers").has("X-Custom-Header"));
		assertEquals("hello", jsonResponse.getBody().getObject().getJSONObject("headers").getString("X-Custom-Header"));
		
		Unirest.clearDefaultHeaders();
		
		jsonResponse = Unirest.get("http://httpbin.org/headers").asJson();
		assertFalse(jsonResponse.getBody().getObject().getJSONObject("headers").has("X-Custom-Header"));
	}
	
	@Test
	public void testSetTimeouts() throws UnknownHostException, IOException {
		String address = "http://" + findAvailableIpAddress() + "/";
		long start = System.currentTimeMillis();
		try {
			Unirest.get("http://" + address + "/").asString();
		} catch (Exception e) {
			if (System.currentTimeMillis() - start > Options.CONNECTION_TIMEOUT + 100) { // Add 100ms for code execution
				fail();
			}
		}
		Unirest.setTimeouts(2000, 10000);
		start = System.currentTimeMillis();
		try {
			Unirest.get("http://" + address + "/").asString();
		} catch (Exception e) {
			if (System.currentTimeMillis() - start > 2100) { // Add 100ms for code execution
				fail();
			}
		}
	}
	
	@Test
	public void testPathParameters() throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get("http://httpbin.org/{method}").routeParam("method", "get").field("name", "Mark").asJson();
		
		assertEquals(200, jsonResponse.getCode());
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("args").getString("name"), "Mark");
	}
	
	@Test
	public void testPathParameters2() throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.patch("http://httpbin.org/{method}").routeParam("method", "patch").field("name", "Mark").asJson();
		
		assertEquals(200, jsonResponse.getCode());
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("form").getString("name"), "Mark");
	}
	
	@Test
	public void testMissingPathParameter() throws UnirestException {
		try {
			Unirest.get("http://httpbin.org/{method}").routeParam("method222", "get").field("name", "Mark").asJson();
			fail();
		} catch (RuntimeException e) {
			// OK
		}
	}
}
