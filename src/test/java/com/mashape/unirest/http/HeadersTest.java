package com.mashape.unirest.http;

import org.junit.Before;
import org.junit.Test;

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
