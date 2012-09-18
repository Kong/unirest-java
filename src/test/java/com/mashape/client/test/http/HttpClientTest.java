/*
 *
 * Mashape Java Client library.
 * Copyright (C) 2011 Mashape, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * The author of this software is Mashape, Inc.
 * For any question or feedback please contact us at: support@mashape.com
 *
 */

package com.mashape.client.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.mashape.client.authentication.Authentication;
import com.mashape.client.authentication.MashapeAuthentication;
import com.mashape.client.http.ContentType;
import com.mashape.client.http.HttpClient;
import com.mashape.client.http.HttpMethod;
import com.mashape.client.http.MashapeResponse;
import com.mashape.client.http.ResponseType;

public class HttpClientTest {

	@Test
	public void testDoRequest() throws IOException {
		MashapeResponse<String> doRequest = HttpClient.doRequest(String.class,
				HttpMethod.GET, "http://www.google.com", null,
				ContentType.BINARY, ResponseType.STRING, null);
		assertTrue(doRequest.getBody() != null
				&& doRequest.getBody().trim() != "");
		assertEquals(200, doRequest.getCode());
		assertFalse(doRequest.getHeaders().isEmpty());
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("_method", "getHello");
		parameters.put("name", "Marco");
		MashapeResponse<JSONObject> res = HttpClient.doRequest(JSONObject.class, HttpMethod.GET,
				"https://mashaper-test.p.mashape.com/api.php", parameters, ContentType.FORM,
				ResponseType.JSON, null);
		assertEquals(403, res.getCode());
		
		
		
		List<Authentication> authenticationHandlers = new ArrayList<Authentication>();
		authenticationHandlers.add(new MashapeAuthentication("PUBVV-sMYgCVmTBI31iwEw9Qz8_XvyQk", "PRIpOYUeeFF3sV%KGsSSxCINDLBpmXoH"));
		
		parameters = new HashMap<String, Object>();
		parameters.put("echo", "this is echo");
		parameters.put("file", new File("/Users/marco/rsa.rb"));
		
		res = HttpClient.doRequest(JSONObject.class, HttpMethod.POST,
				"https://mashaper-testbinarysupport.p.mashape.com/index.php", parameters, ContentType.BINARY,
				ResponseType.JSON, authenticationHandlers);
		
		System.out.println(res.getBody());
		
	}

}
