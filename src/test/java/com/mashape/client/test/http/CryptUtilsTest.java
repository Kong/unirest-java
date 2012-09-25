package com.mashape.client.test.http;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mashape.client.http.utils.CryptUtils;

public class CryptUtilsTest {

	@Test
	public void testGetHMAC_SHA1() {
		assertEquals("a0929c354acce0fc18b8a55450743524d143c560", CryptUtils.getHMAC_SHA1("9bagkvdvvgwj5oaqs1s8jtyxvrtuic", "o793cid79hgwnuabtb9msory63vh7a"));
	}
	
}
