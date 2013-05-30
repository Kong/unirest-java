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

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;

public class UnirestTest {

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
	public void testAsync() throws JSONException, InterruptedException, ExecutionException {
//		Future<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post")
//				 .header("accept", "application/json")
//				 .field("param1", "value1")
//				 .field("param2","bye")
//				 .asJsonAsync();
//		
//		assertNotNull(future);
//		HttpResponse<JsonNode> jsonResponse = future.get();
//		
//		assertTrue(jsonResponse.getHeaders().size() > 0);
//		assertTrue(jsonResponse.getBody().toString().length() > 0);
//		assertFalse(jsonResponse.getRawBody() == null);
//		assertEquals(200, jsonResponse.getCode());
//		
//		JsonNode json = jsonResponse.getBody();
//		assertFalse(json.isArray());
//		assertNotNull(json.getObject());
//		assertNotNull(json.getArray());
//		assertEquals(1, json.getArray().length());
//		assertNotNull(json.getArray().get(0));
		
		
		Future<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post")
		  .header("accept", "application/json")
		  .field("param1", "value1")
		  .field("param2", "value2")
		  .asJsonAsync(new Callback<JsonNode>() {
			  
			public void failed(Exception e) {
				System.out.println("The request has failed");
			}
			
			public void completed(HttpResponse<JsonNode> response) {
				 int code = response.getCode();
			     Map<String, String> headers = response.getHeaders();
			     JsonNode body = response.getBody();
			     InputStream rawBody = response.getRawBody();
			}
			
			public void cancelled() {
				System.out.println("The request has been cancelled");
			}
			
		});
	}
}
