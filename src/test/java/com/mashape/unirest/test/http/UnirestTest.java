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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.options.Options;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;

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
		assertEquals(200, jsonResponse.getStatus());
		
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
		
		response = Unirest.get("http://httpbin.org/get").queryString("name", "mark2").asJson();
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("name"), "mark2");
	}
	
	@Test
	public void testGetUTF8() throws UnirestException {
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get")
		.queryString("param3", "こんにちは").asJson();
		
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("param3"), "こんにちは");
	}
	
	@Test
	public void testPostUTF8() throws UnirestException {
		HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post")
		.field("param3", "こんにちは").asJson();
		
		assertEquals(response.getBody().getObject().getJSONObject("form").getString("param3"), "こんにちは");
	}
	
	@Test
	public void testPostBinaryUTF8() throws UnirestException, URISyntaxException {
		HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post")
		.field("param3", "こんにちは")
		.field("file", new File(getClass().getResource("/test").toURI())).asJson();
		
		assertEquals("This is a test file", response.getBody().getObject().getJSONObject("files").getString("file"));
		assertEquals("こんにちは", response.getBody().getObject().getJSONObject("form").getString("param3"));
	}

    @Test
    public void testPostRawBody() throws UnirestException, URISyntaxException, IOException {
        String sourceString = "'\"@こんにちは-test-123-" + Math.random();
        byte[] sentBytes = sourceString.getBytes();

        HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post")
                .body(sentBytes)
                .asJson();

        assertEquals(sourceString, response.getBody().getObject().getString("data"));
    }

	@Test
	public void testCustomUserAgent() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get?name=mark").header("user-agent", "hello-world").asJson();
		assertEquals("hello-world", response.getBody().getObject().getJSONObject("headers").getString("User-Agent"));
		
		
		GetRequest getRequest = Unirest.get("http");
		for(Object current : Arrays.asList(0, 1, 2)) {
			getRequest.queryString("name", current);
		}
		
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
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get").queryString("name", "mark").queryString("nick", "thefosk").asJson();
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("name"), "mark");
		assertEquals(response.getBody().getObject().getJSONObject("args").getString("nick"), "thefosk");
	}
	
	@Test
	public void testGetFields2() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get").queryString("email", "hello@hello.com").asJson();
		assertEquals("hello@hello.com", response.getBody().getObject().getJSONObject("args").getString("email"));
	}
	
	@Test
	public void testDelete() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.delete("http://httpbin.org/delete").asJson();
		assertEquals(200, response.getStatus());
	
		response = Unirest.delete("http://httpbin.org/delete").field("name", "mark").asJson();
		assertEquals("mark", response.getBody().getObject().getJSONObject("form").getString("name"));
	}
	
	@Test
	public void testDeleteBody() throws JSONException, UnirestException {
		String body = "{\"jsonString\":{\"members\":\"members1\"}}";
		HttpResponse<JsonNode> response = Unirest.delete("http://httpbin.org/delete").body(body).asJson();
		assertEquals(200, response.getStatus());
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
		assertEquals(200, jsonResponse.getStatus());
		
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
				 assertEquals(200, jsonResponse.getStatus());

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
		assertEquals(200, jsonResponse.getStatus());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
		assertNotNull(json.getObject().getJSONObject("files"));
		
		assertEquals("This is a test file", json.getObject().getJSONObject("files").getString("file"));
		assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
	}
	
	@Test
	public void testMultipartContentType() throws JSONException, InterruptedException, ExecutionException,
		URISyntaxException, UnirestException {
		HttpResponse<JsonNode> jsonResponse =
			Unirest.post("http://httpbin.org/post")
				.field("name", "Mark")
				.field("file", new File(getClass().getResource("/image.jpg").toURI()), "image/jpeg").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
		assertNotNull(json.getObject().getJSONObject("files"));

		assertTrue(json.getObject().getJSONObject("files").getString("file").contains("data:image/jpeg"));
		assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
	}
	
	@Test
	public void testMultipartInputStreamContentType() throws JSONException, InterruptedException, ExecutionException,
		URISyntaxException, UnirestException, FileNotFoundException {
		HttpResponse<JsonNode> jsonResponse =
			Unirest.post("http://httpbin.org/post")
				.field("name", "Mark")
				.field("file", new FileInputStream(new File(getClass().getResource("/image.jpg").toURI())), ContentType.APPLICATION_OCTET_STREAM, "image.jpg").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
		assertNotNull(json.getObject().getJSONObject("files"));

		assertTrue(json.getObject().getJSONObject("files").getString("file").contains("data:application/octet-stream"));
		assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
	}
	
	@Test
	public void testMultipartInputStreamContentTypeAsync() throws JSONException, InterruptedException, ExecutionException, URISyntaxException, UnirestException, FileNotFoundException {
		Unirest.post("http://httpbin.org/post")
		.field("name", "Mark")
		.field("file", new FileInputStream(new File(getClass().getResource("/test").toURI())), ContentType.APPLICATION_OCTET_STREAM, "test").asJsonAsync(new Callback<JsonNode>() {
			
			public void failed(UnirestException e) {
				fail();
			}
			
			public void completed(HttpResponse<JsonNode> response) {
				assertTrue(response.getHeaders().size() > 0);
				assertTrue(response.getBody().toString().length() > 0);
				assertFalse(response.getRawBody() == null);
				assertEquals(200, response.getStatus());
				
				JsonNode json = response.getBody();
				assertFalse(json.isArray());
				assertNotNull(json.getObject());
				assertNotNull(json.getArray());
				assertEquals(1, json.getArray().length());
				assertNotNull(json.getArray().get(0));
				
				assertEquals("This is a test file", json.getObject().getJSONObject("files").getString("file"));
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
	public void testMultipartByteContentType() throws JSONException, InterruptedException, ExecutionException,
		URISyntaxException, UnirestException, IOException {
		final InputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));
		final byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		HttpResponse<JsonNode> jsonResponse =
			Unirest.post("http://httpbin.org/post")
				.field("name", "Mark")
				.field("file", bytes, "image.jpg").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
		assertNotNull(json.getObject().getJSONObject("files"));

		assertTrue(json.getObject().getJSONObject("files").getString("file").contains("data:application/octet-stream"));
		assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
	}
	
	@Test
	public void testMultipartByteContentTypeAsync() throws JSONException, InterruptedException, ExecutionException, URISyntaxException, UnirestException, IOException {
		final InputStream stream = new FileInputStream(new File(getClass().getResource("/test").toURI()));
		final byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		Unirest.post("http://httpbin.org/post")
		.field("name", "Mark")
		.field("file", bytes, "test").asJsonAsync(new Callback<JsonNode>() {
			
			public void failed(UnirestException e) {
				fail();
			}
			
			public void completed(HttpResponse<JsonNode> response) {
				assertTrue(response.getHeaders().size() > 0);
				assertTrue(response.getBody().toString().length() > 0);
				assertFalse(response.getRawBody() == null);
				assertEquals(200, response.getStatus());
				
				JsonNode json = response.getBody();
				assertFalse(json.isArray());
				assertNotNull(json.getObject());
				assertNotNull(json.getArray());
				assertEquals(1, json.getArray().length());
				assertNotNull(json.getArray().get(0));
				
				assertEquals("This is a test file", json.getObject().getJSONObject("files").getString("file"));
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
				assertEquals(200, response.getStatus());
				
				JsonNode json = response.getBody();
				assertFalse(json.isArray());
				assertNotNull(json.getObject());
				assertNotNull(json.getArray());
				assertEquals(1, json.getArray().length());
				assertNotNull(json.getArray().get(0));
				
				assertEquals("This is a test file", json.getObject().getJSONObject("files").getString("file"));
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
		assertEquals(200, jsonResponse.getStatus());
		
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
		assertEquals(200, jsonResponse.getStatus());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertTrue(json.getObject().getBoolean("gzipped"));
	}
	
	@Test
	public void testDefaultHeaders() throws UnirestException, JSONException {
		Unirest.setDefaultHeader("X-Custom-Header", "hello");
		Unirest.setDefaultHeader("user-agent", "foobar");

		HttpResponse<JsonNode> jsonResponse =
				Unirest.get("http://httpbin.org/headers").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());
		
		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertTrue(jsonResponse.getBody().getObject().getJSONObject("headers").has("X-Custom-Header"));
		assertEquals("hello", json.getObject().getJSONObject("headers").getString("X-Custom-Header"));
		assertTrue(jsonResponse.getBody().getObject().getJSONObject("headers").has("User-Agent"));
		assertEquals("foobar", json.getObject().getJSONObject("headers").getString("User-Agent"));

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
		HttpResponse<JsonNode> jsonResponse = Unirest.get("http://httpbin.org/{method}").routeParam("method", "get").queryString("name", "Mark").asJson();
		
		assertEquals(200, jsonResponse.getStatus());
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("args").getString("name"), "Mark");
	}
	
	@Test
	public void testQueryAndBodyParameters() throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/{method}").routeParam("method", "post").queryString("name", "Mark").field("wot", "wat").asJson();
		
		assertEquals(200, jsonResponse.getStatus());
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("args").getString("name"), "Mark");
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("form").getString("wot"), "wat");
	}
	
	@Test
	public void testPathParameters2() throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.patch("http://httpbin.org/{method}").routeParam("method", "patch").field("name", "Mark").asJson();
		
		assertEquals(200, jsonResponse.getStatus());
		assertEquals("OK", jsonResponse.getStatusText());
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("form").getString("name"), "Mark");
	}
	
	@Test
	public void testMissingPathParameter() throws UnirestException {
		try {
			Unirest.get("http://httpbin.org/{method}").routeParam("method222", "get").queryString("name", "Mark").asJson();
			fail();
		} catch (RuntimeException e) {
			// OK
		}
	}
	
	@Test
	public void parallelTest() throws InterruptedException {
		Unirest.setConcurrency(10, 5);
		
		long start = System.currentTimeMillis();
		makeParallelRequests();
		long smallerConcurrencyTime = (System.currentTimeMillis() - start);
		
		Unirest.setConcurrency(200, 20);
		start = System.currentTimeMillis();
		makeParallelRequests();
		long higherConcurrencyTime = (System.currentTimeMillis() - start);
		
		assertTrue(higherConcurrencyTime < smallerConcurrencyTime);
	}

	private void makeParallelRequests() throws InterruptedException {
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);
		final AtomicInteger counter = new AtomicInteger(0);
		for(int i=0;i< 200;i++) {
			newFixedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						Unirest.get("http://httpbin.org/get").queryString("index", counter.incrementAndGet()).asJson();
					} catch (UnirestException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
		
		newFixedThreadPool.shutdown();
		newFixedThreadPool.awaitTermination(10, TimeUnit.MINUTES);
	}
	
	@Test
	public void testAsyncCustomContentType() throws InterruptedException {
		Unirest.post("http://httpbin.org/post")
		 .header("accept", "application/json")
		 .header("Content-Type", "application/json")
		 .body("{\"hello\":\"world\"}")
		 .asJsonAsync(new Callback<JsonNode>() {
			
			public void failed(UnirestException e) {
				fail();
			}
			
			public void completed(HttpResponse<JsonNode> jsonResponse) {
				JsonNode json = jsonResponse.getBody();
				assertEquals("{\"hello\":\"world\"}", json.getObject().getString("data"));
				assertEquals("application/json", json.getObject().getJSONObject("headers").getString("Content-Type"));
				
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
	public void testAsyncCustomContentTypeAndFormParams() throws InterruptedException {
		Unirest.post("http://httpbin.org/post")
		 .header("accept", "application/json")
		 .header("Content-Type", "application/x-www-form-urlencoded")
		 .field("name", "Mark")
		 .field("hello", "world")
		 .asJsonAsync(new Callback<JsonNode>() {
			
			public void failed(UnirestException e) {
				fail();
			}
			
			public void completed(HttpResponse<JsonNode> jsonResponse) {
				JsonNode json = jsonResponse.getBody();
				assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
				assertEquals("world", json.getObject().getJSONObject("form").getString("hello"));
				
				assertEquals("application/x-www-form-urlencoded", json.getObject().getJSONObject("headers").getString("Content-Type"));
				
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
	public void testGetQuerystringArray() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get").queryString("name","Mark").queryString("name", "Tom").asJson();
		
		JSONArray names = response.getBody().getObject().getJSONObject("args").getJSONArray("name");
		assertEquals(2, names.length());
		
		assertEquals("Mark", names.getString(0));
		assertEquals("Tom", names.getString(1));
	}
	
	@Test
	public void testPostMultipleFiles() throws JSONException, UnirestException, URISyntaxException { 
		HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post")
				.field("param3", "wot")
				.field("file1", new File(getClass().getResource("/test").toURI())).field("file2", new File(getClass().getResource("/test").toURI())).asJson();
		
		JSONObject names = response.getBody().getObject().getJSONObject("files");
		assertEquals(2, names.length());
		
		assertEquals("This is a test file", names.getString("file1"));
		assertEquals("This is a test file", names.getString("file2"));
		
		assertEquals("wot", response.getBody().getObject().getJSONObject("form").getString("param3"));
	}
	
	@Test
	public void testGetArray() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get").queryString("name", Arrays.asList("Mark", "Tom")).asJson();
		
		JSONArray names = response.getBody().getObject().getJSONObject("args").getJSONArray("name");
		assertEquals(2, names.length());
		
		assertEquals("Mark", names.getString(0));
		assertEquals("Tom", names.getString(1));
	}
	
	@Test
	public void testPostArray() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post").field("name", "Mark").field("name", "Tom").asJson();
		
		JSONArray names = response.getBody().getObject().getJSONObject("form").getJSONArray("name");
		assertEquals(2, names.length());
		
		assertEquals("Mark", names.getString(0));
		assertEquals("Tom", names.getString(1));
	}
	
	@Test
	public void testPostCollection() throws JSONException, UnirestException { 
		HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post").field("name", Arrays.asList("Mark", "Tom")).asJson();
		
		JSONArray names = response.getBody().getObject().getJSONObject("form").getJSONArray("name");
		assertEquals(2, names.length());
		
		assertEquals("Mark", names.getString(0));
		assertEquals("Tom", names.getString(1));
	}
	
	@Test
	public void testCaseInsensitiveHeaders() throws UnirestException {
		GetRequest request = Unirest.get("http://httpbin.org/headers").header("Name", "Marco");
		assertEquals(1, request.getHeaders().size());
		assertEquals("Marco", request.getHeaders().get("name").get(0));
		assertEquals("Marco", request.getHeaders().get("NAme").get(0));
		assertEquals("Marco", request.getHeaders().get("Name").get(0));
		JSONObject headers = request.asJson().getBody().getObject().getJSONObject("headers");
		assertEquals("Marco", headers.getString("Name"));
		
		request = Unirest.get("http://httpbin.org/headers").header("Name", "Marco").header("Name", "John");
		assertEquals(1, request.getHeaders().size());
		assertEquals("Marco", request.getHeaders().get("name").get(0));
		assertEquals("John", request.getHeaders().get("name").get(1));
		assertEquals("Marco", request.getHeaders().get("NAme").get(0));
		assertEquals("John", request.getHeaders().get("NAme").get(1));
		assertEquals("Marco", request.getHeaders().get("Name").get(0));
		assertEquals("John", request.getHeaders().get("Name").get(1));
		
		headers = request.asJson().getBody().getObject().getJSONObject("headers");
		assertEquals("Marco,John", headers.get("Name"));
	}
	
	@Test
	public void setTimeoutsAndCustomClient() {
		try {
			Unirest.setTimeouts(1000, 2000);
		} catch(Exception e) {
			fail();
		}
		
		try {
			Unirest.setAsyncHttpClient(HttpAsyncClientBuilder.create().build());
		} catch(Exception e) {
			fail();
		}
		
		try {
			Unirest.setAsyncHttpClient(HttpAsyncClientBuilder.create().build());
			Unirest.setTimeouts(1000, 2000);
			fail();
		} catch(Exception e) {
			// Ok
		}
		
		try {
			Unirest.setHttpClient(HttpClientBuilder.create().build());
			Unirest.setTimeouts(1000, 2000);
			fail();
		} catch(Exception e) {
			// Ok 
		}
	}

	@Test
	public void testObjectMapper() throws UnirestException, IOException {
		final String responseJson = "{\"locale\": \"english\"}";

		Unirest.setObjectMapper(new ObjectMapper() {
			public Object readValue(String ignored) {
				return Locale.ENGLISH;
			}

			public String writeValue(Object ignored) {
				return responseJson;
			}
		});

		HttpResponse<Locale> getResponse = Unirest.get("http://httpbin.org/get").asObject(Locale.class);
		assertEquals(200, getResponse.getStatus());
		assertEquals(getResponse.getBody(), Locale.ENGLISH);

		HttpResponse<JsonNode> postResponse = Unirest.post("http://httpbin.org/post")
				.header("accept", "application/json")
				.header("Content-Type", "application/json")
				.body(Locale.ENGLISH)
				.asJson();

		assertEquals(200, postResponse.getStatus());
		assertEquals(postResponse.getBody().getObject().getString("data"), responseJson);
	}
	
	@Test
	public void testPostProvidesSortedParams() throws IOException {
		// Verify that fields are encoded into the body in sorted order.
		HttpRequest httpRequest = Unirest.post("test")
			.field("z", "Z")
			.field("y", "Y")
			.field("x", "X")
			.getHttpRequest();

		InputStream content = httpRequest.getBody().getEntity().getContent();
		String body = IOUtils.toString(content, "UTF-8");
		assertEquals("x=X&y=Y&z=Z", body);
	}
}
