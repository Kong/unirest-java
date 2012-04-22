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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.junit.Test;

import com.mashape.client.http.utils.RequestPrepareResult;
import com.mashape.client.http.utils.UrlUtils;


public class UrlUtilsTest {

	@Test
	public void testPrepareRequest() throws UnsupportedEncodingException {
		RequestPrepareResult prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com", null, false);
		assertEquals("http://www.ciao.com", prepareRequest.getUrl());
		assertEquals(new HashMap<String, String>(), prepareRequest.getParameters());
		
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com", new HashMap<String, String>(), false);
		assertEquals("http://www.ciao.com", prepareRequest.getUrl());
		assertEquals(new HashMap<String, String>(), prepareRequest.getParameters());
		
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}", null, false);
		assertEquals("http://www.ciao.com/", prepareRequest.getUrl());
		assertEquals(new HashMap<String, String>(), prepareRequest.getParameters());
		
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}", null, false);
		assertEquals("http://www.ciao.com/", prepareRequest.getUrl());
		assertEquals(new HashMap<String, String>(), prepareRequest.getParameters());
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}", parameters, false);
		assertEquals("http://www.ciao.com/12", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("name", "tom");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}", parameters, false);
		assertEquals("http://www.ciao.com/12?name=tom", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("name", "tom");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt=1", parameters, false);
		assertEquals("http://www.ciao.com/12?name=tom&opt=1", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("name", "tom jerry");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}", parameters, false);
		assertEquals("http://www.ciao.com/12?name=tom+jerry", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("name", "tom jerry");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt=1&nick={nick}", parameters, false);
		assertEquals("http://www.ciao.com/12?name=tom+jerry&opt=1", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("name", "tom jerry");
		parameters.put("nick", "sinz");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt={opt}&nick={nick}", parameters, false);
		assertEquals("http://www.ciao.com/12?name=tom+jerry&nick=sinz", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("name", "tom jerry");
		parameters.put("opt", "yes");
		parameters.put("nick", "sinz");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt={opt}&nick={nick}", parameters, false);
		assertEquals("http://www.ciao.com/12?name=tom+jerry&opt=yes&nick=sinz", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("opt", "yes");
		parameters.put("nick", "sinz");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt={opt}&nick={nick}", parameters, false);
		assertEquals("http://www.ciao.com/12?opt=yes&nick=sinz", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("opt", "yes");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt={opt}&nick={nick}", parameters, false);
		assertEquals("http://www.ciao.com/12?opt=yes", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());

		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt={opt}&nick={nick}", parameters, false);
		assertEquals("http://www.ciao.com/12", prepareRequest.getUrl());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "12");
		parameters.put("pippo", null);
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt={opt}&nick={nick}", parameters, false);
		assertEquals("http://www.ciao.com/12", prepareRequest.getUrl());
		assertEquals(1, parameters.size());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "ciao marco");
		parameters.put("name", "ciao pippo");
		parameters.put("opt", "2");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt={opt}&nick=some+nick", parameters, false);
		assertEquals("http://www.ciao.com/ciao%20marco?name=ciao+pippo&opt=2&nick=some+nick", prepareRequest.getUrl());
		assertEquals(3, parameters.size());
		assertEquals(parameters, prepareRequest.getParameters());
		
		parameters = new HashMap<String, String>();
		parameters.put("id", "ciao marco");
		parameters.put("name", "ciao pippo");
		parameters.put("opt", "{this is opt}");
		prepareRequest = UrlUtils.prepareRequest("http://www.ciao.com/{id}?name={name}&opt={opt}&nick=some+nick", parameters, false);
		assertEquals("http://www.ciao.com/ciao%20marco?name=ciao+pippo&opt=%7Bthis+is+opt%7D&nick=some+nick", prepareRequest.getUrl());
		assertEquals(3, parameters.size());
		assertEquals(parameters, prepareRequest.getParameters());
	}	
	
	@Test
	public void testAddClientParameters() throws UnsupportedEncodingException {
		List<Header> headers = UrlUtils.generateClientHeaders();
		assertEquals(1, headers.size());
		
		assertEquals("mashape-java/1.0", headers.get(0).getValue());
	}
}
