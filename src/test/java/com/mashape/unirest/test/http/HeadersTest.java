package com.mashape.unirest.test.http;

import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.Headers;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class HeadersTest {
	private Headers headers;

	@Before
	public void setUp() {
		headers = new Headers();
	}

	@Test
	public void headerNamesCaseInsensitive() {
		headers.put("content-type", Arrays.asList("application/json"));

		String value = headers.getFirst("cOnTeNt-TyPe");
		assertEquals("", "application/json", value);
	}
}
