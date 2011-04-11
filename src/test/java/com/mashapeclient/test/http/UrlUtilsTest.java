package com.mashapeclient.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import com.mashapeclient.http.UrlUtils;

public class UrlUtilsTest {

	@Test
	public void testIsPlaceholder() {
		assertFalse(UrlUtils.isPlaceholder(null));
		assertFalse(UrlUtils.isPlaceholder(""));
		assertFalse(UrlUtils.isPlaceholder("hello"));
		assertFalse(UrlUtils.isPlaceholder("{hello"));
		assertTrue(UrlUtils.isPlaceholder("{hello}"));
	}
	
	@Test
	public void testAddClientParameters() {
		assertEquals("http://www.ciao.com?_token={_token}&_language={_language}&_version={_version}", UrlUtils.addClientParameters("http://www.ciao.com"));
		assertEquals("http://www.ciao.com?param1=value1&_token={_token}&_language={_language}&_version={_version}", UrlUtils.addClientParameters("http://www.ciao.com?param1=value1"));
	}
	
	@Test
	public void testRemoveQueryString() {
		assertEquals("ciao", UrlUtils.removeQueryString("ciao"));
		assertEquals("ciao", UrlUtils.removeQueryString("ciao?marco"));
	}
	
	@Test
	public void testGetQueryStringParameters() {
		assertEquals(0, UrlUtils.getQueryStringParameters("http://www.ciao.com").size());
		assertEquals(0, UrlUtils.getQueryStringParameters("http://www.ciao.com/{name}").size());
		
		List<NameValuePair> parameters = UrlUtils.getQueryStringParameters("http://www.ciao.com?param1=value1");
		assertEquals(1, parameters.size());
		assertEquals("param1", parameters.get(0).getName());
		assertEquals("value1", parameters.get(0).getValue());
		
		assertEquals(1, UrlUtils.getQueryStringParameters("http://www.ciao.com?param1=value1&param2={param2}").size());
	}
	
	@Test
	public void testGetCleanUrl() {
		assertEquals("", UrlUtils.getCleanUrl("", null));
		assertEquals("http://www.ciao.com", UrlUtils.getCleanUrl("http://www.ciao.com", null));
		assertEquals("http://www.ciao.com/user//comments", UrlUtils.getCleanUrl("http://www.ciao.com/user/{id}/comments", null));
		assertEquals("http://www.ciao.com/user//comments?param1=value1", UrlUtils.getCleanUrl("http://www.ciao.com/user/{id}/comments?param1=value1&param2={value2}", null));
		assertEquals("http://www.ciao.com/user//comments?param1=value1&param4=value4", UrlUtils.getCleanUrl("http://www.ciao.com/user/{id}/comments?param1=value1&param2={param2}&param3={param3}&param4=value4", null));
		
		List<NameValuePair> parameters= new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("id", "2"));
		assertEquals("http://www.ciao.com/user/{id}/comments", UrlUtils.getCleanUrl("http://www.ciao.com/user/{id}/comments", parameters));
		
		parameters.add(new BasicNameValuePair("param2", "value2"));
		assertEquals("http://www.ciao.com/user/{id}/comments?param1=value1&param2={param2}&param4=value4", UrlUtils.getCleanUrl("http://www.ciao.com/user/{id}/comments?param1=value1&param2={param2}&param3={param3}&param4=value4", parameters));

		parameters= new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("param2", "value2"));
		assertEquals("http://www.ciao.com/?param2={param2}&param4=value4", UrlUtils.getCleanUrl("http://www.ciao.com/?param1={param1}&param2={param2}&param3={param3}&param4=value4", parameters));
	}
	
}
