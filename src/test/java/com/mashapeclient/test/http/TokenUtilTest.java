package com.mashapeclient.test.http;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.mashapeclient.exceptions.MashapeDeveloperKeyException;
import com.mashapeclient.exceptions.MashapeEmptyResponseException;
import com.mashapeclient.exceptions.MashapeInvalidResponseException;
import com.mashapeclient.http.TokenUtil;

public class TokenUtilTest {

	@Test
	public void testGetToken() {
		try {
			TokenUtil.getToken("");
			fail();
		} catch (MashapeDeveloperKeyException e) {
			// Ok
		} catch (MashapeEmptyResponseException e) {
			fail();
		} catch (MashapeInvalidResponseException e) {
			fail();
		}
	}
	
}
