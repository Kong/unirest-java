package com.mashape.client.test.http;

import org.junit.Test;

import com.mashape.client.http.HttpClient;

public class HttpClientTest {

	@Test
	public void t() throws Exception {
		
		System.out.println(HttpClient.get("http://components.mashape.com/sub/test/api.php?_method=getHello&name=Marco")
				.asString().getBody());
		
		System.out.println(HttpClient.post("http://components.mashape.com/sub/test/api.php")
				.field("_method", "postHello")
				.field("name", "Marco")
				.asString().getBody());
	}

}
