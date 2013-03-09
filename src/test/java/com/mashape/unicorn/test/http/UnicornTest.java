package com.mashape.unicorn.test.http;

import org.junit.Test;

import com.mashape.unicorn.http.Unicorn;

public class UnicornTest {

	@Test
	public void t() throws Exception {
		
		System.out.println(Unicorn.get("http://components.mashape.com/sub/test/api.php?_method=getHello&name=Marco")
				.asString().getBody());
		
		System.out.println(Unicorn.post("http://components.mashape.com/sub/test/api.php")
				.field("_method", "postHello")
				.field("name", "Marco")
				.asString().getBody());
	}

}
