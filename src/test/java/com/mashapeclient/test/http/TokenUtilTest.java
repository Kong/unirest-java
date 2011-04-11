package com.mashapeclient.test.http;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mashapeclient.exceptions.MashapeApiKeyException;
import com.mashapeclient.exceptions.MashapeEmptyResponseException;
import com.mashapeclient.exceptions.MashapeInvalidResponseException;
import com.mashapeclient.http.TokenUtil;

public class TokenUtilTest {

	@Test
	public void testGetToken() {
		try {
			TokenUtil.getToken("");
			fail();
		} catch (MashapeApiKeyException e) {
			// Ok
		} catch (MashapeEmptyResponseException e) {
			fail();
		} catch (MashapeInvalidResponseException e) {
			fail();
		}
	}
	
}
