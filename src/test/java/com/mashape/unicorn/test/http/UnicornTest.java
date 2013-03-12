package com.mashape.unicorn.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import com.mashape.unicorn.http.HttpResponse;
import com.mashape.unicorn.http.Unicorn;

public class UnicornTest {

	@Test
	public void testRequests() throws Exception {
		HttpResponse<JsonNode> jsonResponse = Unicorn.post("http://httpbin.org/post")
													 .header("accept", "application/json")
													 .field("param1", "value1")
													 .field("param2", new File("/tmp/file"))
													 .asJson();
		
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getCode());
	}
	
}
