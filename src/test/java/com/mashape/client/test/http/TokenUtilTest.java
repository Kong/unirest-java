package com.mashape.client.test.http;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.mashape.client.exceptions.MashapeClientException;
import com.mashape.client.http.TokenUtil;

public class TokenUtilTest {

	@Test
	public void testRequestToken() {
		try {
			TokenUtil.requestToken(null);
			fail();
		} catch (MashapeClientException e) {
			// Ok
		}
		
		try {
			TokenUtil.requestToken("");
			fail();
		} catch (MashapeClientException e) {
			// Ok
		}
		
		try {
			TokenUtil.requestToken("bla");
			fail();
		} catch (MashapeClientException e) {
			// Ok
		}
	}
}
