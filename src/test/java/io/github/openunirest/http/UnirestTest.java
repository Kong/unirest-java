/*
The MIT License

Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.openunirest.http;

import io.github.openunirest.http.async.Callback;
import io.github.openunirest.http.options.Options;
import io.github.openunirest.request.body.MultipartBody;
import io.github.openunirest.http.exceptions.UnirestException;
import io.github.openunirest.request.GetRequest;
import io.github.openunirest.request.HttpRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.openunirest.http.TestUtils.read;
import static org.junit.Assert.*;

public class UnirestTest {

	private CountDownLatch lock;
	private boolean status;

	@BeforeClass
	public static void suiteSetUp(){
		MockServer.start();
	}

	@AfterClass
	public static void suiteTearDown(){
		MockServer.shutdown();
	}

	@Before
	public void setUp() {
		MockServer.reset();
		lock = new CountDownLatch(1);
		status = false;
	}

	@Test
	public void postFormReturnJson() throws JSONException, UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(MockServer.POST)
				.header("accept", "application/json")
				.field("param1", "value1")
				.field("param2", "bye")
				.asJson();

		assertEquals(200, jsonResponse.getStatus());

		RequestCapture json = parse(jsonResponse);
		json.assertHeader("Accept", "application/json");
		json.assertQuery("param1", "value1");
		json.assertQuery("param2", "bye");
	}

	@Test
	public void canPassQueryParamsOnStringOrWithForm() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.get(MockServer.GETJSON + "?name=mark")
				.header("accept", "application/json")
				.asJson();

		RequestCapture json = parse(response);
		json.assertQuery("name", "mark");

		response = Unirest.get(MockServer.GETJSON)
				.header("accept", "application/json")
				.queryString("name", "mark2")
				.asJson();

		json = parse(response);
		json.assertQuery("name", "mark2");
	}

	@Test
	public void multipleParams() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.get(MockServer.GETJSON + "?name=ringo")
				.header("accept", "application/json")
				.queryString("name", "paul")
				.queryString("name", "john")
				.asJson();

		RequestCapture json = parse(response);
		json.assertQuery("name", "ringo");
		json.assertQuery("name", "paul");
		json.assertQuery("name", "john");
	}

	@Test
	public void testGetUTF8() {
		HttpResponse<JsonNode> response = Unirest.get(MockServer.GETJSON)
				.header("accept", "application/json")
				.queryString("param3", "こんにちは")
				.asJson();

		RequestCapture json = parse(response);
		json.assertQuery("param3", "こんにちは");
	}

	@Test
	public void testPostUTF8() {
		HttpResponse response = Unirest.post(MockServer.POST)
				.header("accept", "application/json")
				.field("param3", "こんにちは")
				.asJson();

		RequestCapture json = parse(response);
		json.assertQuery("param3", "こんにちは");
	}

	@Test
	public void testPostBinaryUTF8() throws URISyntaxException {
		HttpResponse<JsonNode> response = Unirest.post(MockServer.POST)
				.header("Accept", ContentType.MULTIPART_FORM_DATA.getMimeType())
				.field("param3", "こんにちは")
				.field("file", new File(getClass().getResource("/test").toURI()))
				.asJson();

		RequestCapture json = parse(response);
		json.assertQuery("param3", "こんにちは");
		json.getFile("test").assertBody("This is a test file");
	}

	@Test
	public void testPostRawBody() {
		String sourceString = "'\"@こんにちは-test-123-" + Math.random();
		byte[] sentBytes = sourceString.getBytes();

		HttpResponse<JsonNode> response = Unirest.post(MockServer.POST)
				.body(sentBytes)
				.asJson();

		RequestCapture json = parse(response);
		json.asserBody(sourceString);
	}

	@Test
	public void testCustomUserAgent() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.get(MockServer.GETJSON)
                .header("user-agent", "hello-world")
                .asJson();

        RequestCapture json = parse(response);
        json.assertHeader("User-Agent", "hello-world");
	}

    @Test
	public void testGetMultiple() throws JSONException, UnirestException {
		for (int i = 1; i <= 20; i++) {
			HttpResponse<JsonNode> response = Unirest.get(MockServer.GETJSON + "?try=" + i).asJson();
            parse(response).assertQuery("try", String.valueOf(i));
		}
	}

	@Test
	public void testGetFields() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.get(MockServer.GETJSON)
                .queryString("name", "mark")
                .queryString("nick", "thefosk")
                .asJson();

        RequestCapture parse = parse(response);
        parse.assertQuery("name", "mark");
		parse.assertQuery("nick", "thefosk");
	}

	@Test
	public void testGetFields2() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.get(MockServer.GETJSON)
                .queryString("email", "hello@hello.com")
                .asJson();

        parse(response).assertQuery("email", "hello@hello.com");
	}

	@Test
	public void testQueryStringEncoding() throws JSONException, UnirestException {
		String testKey = "email2=someKey&email";
		String testValue = "hello@hello.com";

		HttpResponse<JsonNode> response = Unirest.get(MockServer.GETJSON)
                .queryString(testKey, testValue)
                .asJson();

		parse(response).assertQuery(testKey, testValue);
	}

	@Test
	public void testDelete() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.delete("http://httpbin.org/delete").asJson();
		assertEquals(200, response.getStatus());

		response = Unirest.delete("http://httpbin.org/delete").field("name", "mark").asJson();
		assertEquals("mark", response.getBody().getObject().getJSONObject("form").getString("name"));
	}

	@Test
	public void testDeleteBody() throws JSONException, UnirestException {
		String body = "{\"jsonString\":{\"members\":\"members1\"}}";
		HttpResponse<JsonNode> response = Unirest.delete("http://httpbin.org/delete").body(body).asJson();
		assertEquals(200, response.getStatus());
		assertEquals(body, response.getBody().getObject().getString("data"));
	}

	@Test
	public void testBasicAuth() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/headers").basicAuth("user", "test").asJson();
		assertEquals("Basic dXNlcjp0ZXN0", response.getBody().getObject().getJSONObject("headers").getString("Authorization"));
	}

	@Test
	public void testAsync() throws JSONException, InterruptedException, ExecutionException {
		Future<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post").header("accept", "application/json").field("param1", "value1").field("param2", "bye").asJsonAsync();

		assertNotNull(future);
		HttpResponse<JsonNode> jsonResponse = future.get();

		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
	}

	@Test
	public void testAsyncCallback() throws JSONException, InterruptedException {
		Unirest.post("http://httpbin.org/post").header("accept", "application/json").field("param1", "value1").field("param2", "bye").asJsonAsync(new Callback<JsonNode>() {

			public void failed(UnirestException e) {
				fail();
			}

			public void completed(HttpResponse<JsonNode> jsonResponse) {
				assertTrue(jsonResponse.getHeaders().size() > 0);
				assertTrue(jsonResponse.getBody().toString().length() > 0);
				assertFalse(jsonResponse.getRawBody() == null);
				assertEquals(200, jsonResponse.getStatus());

				JsonNode json = jsonResponse.getBody();
				assertFalse(json.isArray());
				assertNotNull(json.getObject());
				assertNotNull(json.getArray());
				assertEquals(1, json.getArray().length());
				assertNotNull(json.getArray().get(0));

				assertEquals("value1", json.getObject().getJSONObject("form").getString("param1"));
				assertEquals("bye", json.getObject().getJSONObject("form").getString("param2"));

				status = true;
				lock.countDown();
			}

			public void cancelled() {
				fail();
			}
		});

		lock.await(10, TimeUnit.SECONDS);
		assertTrue(status);
	}

	@Test
	public void testMultipart() throws JSONException, URISyntaxException, UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post").field("name", "Mark").field("file", new File(getClass().getResource("/test").toURI())).asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
		assertNotNull(json.getObject().getJSONObject("files"));

		assertEquals("This is a test file", json.getObject().getJSONObject("files").getString("file"));
		assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
	}

	@Test
	public void testMultipartContentType() throws JSONException, URISyntaxException, UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post").field("name", "Mark").field("file", new File(getClass().getResource("/image.jpg").toURI()), "image/jpeg").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
		assertNotNull(json.getObject().getJSONObject("files"));

		assertTrue(json.getObject().getJSONObject("files").getString("file").contains("data:image/jpeg"));
		assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
	}

	@Test
	public void testMultipartInputStreamContentType() throws JSONException, URISyntaxException, FileNotFoundException {
		FileInputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));

		MultipartBody request = Unirest.post(MockServer.HOST + "/post")
            .header("accept", ContentType.MULTIPART_FORM_DATA.toString())
			.field("name", "Mark")
			.field("file", stream, ContentType.APPLICATION_OCTET_STREAM, "image.jpg");

		HttpResponse<JsonNode> jsonResponse = request
			.asJson();

		assertEquals(200, jsonResponse.getStatus());

		RequestCapture json = parse(jsonResponse);
		json.assertHeader("Accept", ContentType.MULTIPART_FORM_DATA.toString());
		json.assertQuery("name", "Mark");
		assertEquals("application/octet-stream", json.getFile("image.jpg").type);
	}

	@Test
	public void testMultipartInputStreamContentTypeAsync() throws JSONException, InterruptedException, URISyntaxException, FileNotFoundException {
		Unirest.post("http://httpbin.org/post").field("name", "Mark").field("file", new FileInputStream(new File(getClass().getResource("/test").toURI())), ContentType.APPLICATION_OCTET_STREAM, "test").asJsonAsync(new Callback<JsonNode>() {

			public void failed(UnirestException e) {
				fail();
			}

			public void completed(HttpResponse<JsonNode> response) {
				assertTrue(response.getHeaders().size() > 0);
				assertTrue(response.getBody().toString().length() > 0);
				assertFalse(response.getRawBody() == null);
				assertEquals(200, response.getStatus());

				JsonNode json = response.getBody();
				assertFalse(json.isArray());
				assertNotNull(json.getObject());
				assertNotNull(json.getArray());
				assertEquals(1, json.getArray().length());
				assertNotNull(json.getArray().get(0));

				assertEquals("This is a test file", json.getObject().getJSONObject("files").getString("file"));
				assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));

				status = true;
				lock.countDown();
			}

			public void cancelled() {
				fail();
			}

		});

		lock.await(10, TimeUnit.SECONDS);
		assertTrue(status);
	}

	@Test
	public void testMultipartByteContentType() throws JSONException, InterruptedException, ExecutionException, URISyntaxException, IOException {
		final InputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));
		final byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post").field("name", "Mark").field("file", bytes, "image.jpg").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertNotNull(json.getObject());
		assertNotNull(json.getArray());
		assertEquals(1, json.getArray().length());
		assertNotNull(json.getArray().get(0));
		assertNotNull(json.getObject().getJSONObject("files"));

		assertTrue(json.getObject().getJSONObject("files").getString("file").contains("data:application/octet-stream"));
		assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
	}

	@Test
	public void testMultipartByteContentTypeAsync() throws JSONException, InterruptedException, ExecutionException, URISyntaxException, IOException {
		final InputStream stream = new FileInputStream(new File(getClass().getResource("/test").toURI()));
		final byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		Unirest.post("http://httpbin.org/post").field("name", "Mark").field("file", bytes, "test").asJsonAsync(new Callback<JsonNode>() {

			public void failed(UnirestException e) {
				fail();
			}

			public void completed(HttpResponse<JsonNode> response) {
				assertTrue(response.getHeaders().size() > 0);
				assertTrue(response.getBody().toString().length() > 0);
				assertFalse(response.getRawBody() == null);
				assertEquals(200, response.getStatus());

				JsonNode json = response.getBody();
				assertFalse(json.isArray());
				assertNotNull(json.getObject());
				assertNotNull(json.getArray());
				assertEquals(1, json.getArray().length());
				assertNotNull(json.getArray().get(0));

				assertEquals("This is a test file", json.getObject().getJSONObject("files").getString("file"));
				assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));

				status = true;
				lock.countDown();
			}

			public void cancelled() {
				fail();
			}

		});

		lock.await(10, TimeUnit.SECONDS);
		assertTrue(status);
	}

	@Test
	public void testMultipartAsync() throws JSONException, InterruptedException, ExecutionException, URISyntaxException, UnirestException {
		Unirest.post("http://httpbin.org/post").field("name", "Mark").field("file", new File(getClass().getResource("/test").toURI())).asJsonAsync(new Callback<JsonNode>() {

			public void failed(UnirestException e) {
				fail();
			}

			public void completed(HttpResponse<JsonNode> response) {
				assertTrue(response.getHeaders().size() > 0);
				assertTrue(response.getBody().toString().length() > 0);
				assertFalse(response.getRawBody() == null);
				assertEquals(200, response.getStatus());

				JsonNode json = response.getBody();
				assertFalse(json.isArray());
				assertNotNull(json.getObject());
				assertNotNull(json.getArray());
				assertEquals(1, json.getArray().length());
				assertNotNull(json.getArray().get(0));

				assertEquals("This is a test file", json.getObject().getJSONObject("files").getString("file"));
				assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));

				status = true;
				lock.countDown();
			}

			public void cancelled() {
				fail();
			}

		});

		lock.await(10, TimeUnit.SECONDS);
		assertTrue(status);
	}

	@Test
	public void testGzip() throws JSONException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get("http://httpbin.org/gzip").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertTrue(json.getObject().getBoolean("gzipped"));
	}

	@Test
	public void testGzipAsync() throws JSONException, InterruptedException, ExecutionException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get("http://httpbin.org/gzip").asJsonAsync().get();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertTrue(json.getObject().getBoolean("gzipped"));
	}

	@Test
	public void testDefaultHeaders() throws JSONException {
		Unirest.setDefaultHeader("X-Custom-Header", "hello");
		Unirest.setDefaultHeader("user-agent", "foobar");

		HttpResponse<JsonNode> jsonResponse = Unirest.get("http://httpbin.org/headers").asJson();
		assertTrue(jsonResponse.getHeaders().size() > 0);
		assertTrue(jsonResponse.getBody().toString().length() > 0);
		assertFalse(jsonResponse.getRawBody() == null);
		assertEquals(200, jsonResponse.getStatus());

		JsonNode json = jsonResponse.getBody();
		assertFalse(json.isArray());
		assertTrue(jsonResponse.getBody().getObject().getJSONObject("headers").has("X-Custom-Header"));
		assertEquals("hello", json.getObject().getJSONObject("headers").getString("X-Custom-Header"));
		assertTrue(jsonResponse.getBody().getObject().getJSONObject("headers").has("User-Agent"));
		assertEquals("foobar", json.getObject().getJSONObject("headers").getString("User-Agent"));

		jsonResponse = Unirest.get("http://httpbin.org/headers").asJson();
		assertTrue(jsonResponse.getBody().getObject().getJSONObject("headers").has("X-Custom-Header"));
		assertEquals("hello", jsonResponse.getBody().getObject().getJSONObject("headers").getString("X-Custom-Header"));

		Unirest.clearDefaultHeaders();

		jsonResponse = Unirest.get("http://httpbin.org/headers").asJson();
		assertFalse(jsonResponse.getBody().getObject().getJSONObject("headers").has("X-Custom-Header"));
	}

	@Test
	public void testSetTimeouts() throws IOException {
		String address = "http://" + findAvailableIpAddress() + "/";
		long start = System.currentTimeMillis();
		try {
			Unirest.get("http://" + address + "/").asString();
		} catch (Exception e) {
			if (System.currentTimeMillis() - start > Options.CONNECTION_TIMEOUT + 100) { // Add 100ms for code execution
				fail();
			}
		}
		Unirest.setTimeouts(2000, 10000);
		start = System.currentTimeMillis();
		try {
			Unirest.get("http://" + address + "/").asString();
		} catch (Exception e) {
			if (System.currentTimeMillis() - start > 2100) { // Add 100ms for code execution
				fail();
			}
		}
	}

	@Test
	public void testPathParameters() {
		HttpResponse<JsonNode> jsonResponse = Unirest.get("http://httpbin.org/{method}").routeParam("method", "get").queryString("name", "Mark").asJson();

		assertEquals(200, jsonResponse.getStatus());
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("args").getString("name"), "Mark");
	}

	@Test
	public void testQueryAndBodyParameters() {
		HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/{method}").routeParam("method", "post").queryString("name", "Mark").field("wot", "wat").asJson();

		assertEquals(200, jsonResponse.getStatus());
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("args").getString("name"), "Mark");
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("form").getString("wot"), "wat");
	}

	@Test
	public void testPathParameters2() {
		HttpResponse<JsonNode> jsonResponse = Unirest.patch("http://httpbin.org/{method}").routeParam("method", "patch").field("name", "Mark").asJson();

		assertEquals(200, jsonResponse.getStatus());
		assertEquals("OK", jsonResponse.getStatusText());
		assertEquals(jsonResponse.getBody().getObject().getJSONObject("form").getString("name"), "Mark");
	}

	@Test
	public void testMissingPathParameter() {
		try {
			Unirest.get("http://httpbin.org/{method}").routeParam("method222", "get").queryString("name", "Mark").asJson();
			fail();
		} catch (RuntimeException e) {
			// OK
		}
	}

	@Test
	public void parallelTest() throws InterruptedException {
		Unirest.setConcurrency(10, 5);

		long start = System.currentTimeMillis();
		makeParallelRequests();
		long smallerConcurrencyTime = (System.currentTimeMillis() - start);

		Unirest.setConcurrency(200, 20);
		start = System.currentTimeMillis();
		makeParallelRequests();
		long higherConcurrencyTime = (System.currentTimeMillis() - start);

		assertTrue(higherConcurrencyTime < smallerConcurrencyTime);
	}

	private void makeParallelRequests() throws InterruptedException {
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);
		final AtomicInteger counter = new AtomicInteger(0);
		for (int i = 0; i < 200; i++) {
			newFixedThreadPool.execute(() -> {
                try {
                    Unirest.get(MockServer.HOST + "/get").queryString("index", counter.incrementAndGet()).asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
            });
		}

		newFixedThreadPool.shutdown();
		newFixedThreadPool.awaitTermination(10, TimeUnit.MINUTES);
	}

	@Test
	public void testAsyncCustomContentType() throws InterruptedException {
		Unirest.post("http://httpbin.org/post").header("accept", "application/json").header("Content-Type", "application/json").body("{\"hello\":\"world\"}").asJsonAsync(new Callback<JsonNode>() {

			public void failed(UnirestException e) {
				fail();
			}

			public void completed(HttpResponse<JsonNode> jsonResponse) {
				JsonNode json = jsonResponse.getBody();
				assertEquals("{\"hello\":\"world\"}", json.getObject().getString("data"));
				assertEquals("application/json", json.getObject().getJSONObject("headers").getString("Content-Type"));

				status = true;
				lock.countDown();
			}

			public void cancelled() {
				fail();
			}
		});

		lock.await(10, TimeUnit.SECONDS);
		assertTrue(status);
	}

	@Test
	public void testAsyncCustomContentTypeAndFormParams() throws InterruptedException {
		Unirest.post("http://httpbin.org/post").header("accept", "application/json").header("Content-Type", "application/x-www-form-urlencoded").field("name", "Mark").field("hello", "world").asJsonAsync(new Callback<JsonNode>() {

			public void failed(UnirestException e) {
				fail();
			}

			public void completed(HttpResponse<JsonNode> jsonResponse) {
				JsonNode json = jsonResponse.getBody();
				assertEquals("Mark", json.getObject().getJSONObject("form").getString("name"));
				assertEquals("world", json.getObject().getJSONObject("form").getString("hello"));

				assertEquals("application/x-www-form-urlencoded", json.getObject().getJSONObject("headers").getString("Content-Type"));

				status = true;
				lock.countDown();
			}

			public void cancelled() {
				fail();
			}
		});

		lock.await(10, TimeUnit.SECONDS);
		assertTrue(status);
	}

	@Test
	public void testGetQuerystringArray() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get").queryString("name", "Mark").queryString("name", "Tom").asJson();

		JSONArray names = response.getBody().getObject().getJSONObject("args").getJSONArray("name");
		assertEquals(2, names.length());

		assertEquals("Mark", names.getString(0));
		assertEquals("Tom", names.getString(1));
	}

	@Test
	public void testPostMultipleFiles() throws JSONException, URISyntaxException {
		HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post").field("param3", "wot").field("file1", new File(getClass().getResource("/test").toURI())).field("file2", new File(getClass().getResource("/test").toURI())).asJson();

		JSONObject names = response.getBody().getObject().getJSONObject("files");
		assertEquals(2, names.length());

		assertEquals("This is a test file", names.getString("file1"));
		assertEquals("This is a test file", names.getString("file2"));

		assertEquals("wot", response.getBody().getObject().getJSONObject("form").getString("param3"));
	}

	@Test
	public void testGetArray() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.get("http://httpbin.org/get").queryString("name", Arrays.asList("Mark", "Tom")).asJson();

		JSONArray names = response.getBody().getObject().getJSONObject("args").getJSONArray("name");
		assertEquals(2, names.length());

		assertEquals("Mark", names.getString(0));
		assertEquals("Tom", names.getString(1));
	}

	@Test
	public void testPostArray() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post").field("name", "Mark").field("name", "Tom").asJson();

		JSONArray names = response.getBody().getObject().getJSONObject("form").getJSONArray("name");
		assertEquals(2, names.length());

		assertEquals("Mark", names.getString(0));
		assertEquals("Tom", names.getString(1));
	}

	@Test
	public void testPostCollection() throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = Unirest.post("http://httpbin.org/post").field("name", Arrays.asList("Mark", "Tom")).asJson();

		JSONArray names = response.getBody().getObject().getJSONObject("form").getJSONArray("name");
		assertEquals(2, names.length());

		assertEquals("Mark", names.getString(0));
		assertEquals("Tom", names.getString(1));
	}

	@Test
	public void testCaseInsensitiveHeaders() {
		GetRequest request = Unirest.get("http://httpbin.org/headers").header("Name", "Marco");
		assertEquals(1, request.getHeaders().size());
		assertEquals("Marco", request.getHeaders().get("name").get(0));
		assertEquals("Marco", request.getHeaders().get("NAme").get(0));
		assertEquals("Marco", request.getHeaders().get("Name").get(0));
		JSONObject headers = request.asJson().getBody().getObject().getJSONObject("headers");
		assertEquals("Marco", headers.getString("Name"));

		request = Unirest.get("http://httpbin.org/headers").header("Name", "Marco").header("Name", "John");
		assertEquals(1, request.getHeaders().size());
		assertEquals("Marco", request.getHeaders().get("name").get(0));
		assertEquals("John", request.getHeaders().get("name").get(1));
		assertEquals("Marco", request.getHeaders().get("NAme").get(0));
		assertEquals("John", request.getHeaders().get("NAme").get(1));
		assertEquals("Marco", request.getHeaders().get("Name").get(0));
		assertEquals("John", request.getHeaders().get("Name").get(1));
	}

	@Test
	public void setTimeoutsAndCustomClient() {
		try {
			Unirest.setTimeouts(1000, 2000);
		} catch (Exception e) {
			fail();
		}

		try {
			Unirest.setAsyncHttpClient(HttpAsyncClientBuilder.create().build());
		} catch (Exception e) {
			fail();
		}

		try {
			Unirest.setAsyncHttpClient(HttpAsyncClientBuilder.create().build());
			Unirest.setTimeouts(1000, 2000);
			fail();
		} catch (Exception e) {
			// Ok
		}

		try {
			Unirest.setHttpClient(HttpClientBuilder.create().build());
			Unirest.setTimeouts(1000, 2000);
			fail();
		} catch (Exception e) {
			// Ok
		}
	}

	@Test
	public void testObjectMapperRead() {
		Unirest.setObjectMapper(new JacksonObjectMapper());

		GetResponse getResponseMock = new GetResponse();
		getResponseMock.setUrl("http://httpbin.org/get");

		HttpResponse<GetResponse> getResponse = Unirest.get(getResponseMock.getUrl()).asObject(GetResponse.class);

		assertEquals(200, getResponse.getStatus());
		assertEquals(getResponse.getBody().getUrl(), getResponseMock.getUrl());
	}

	@Test
	public void testObjectMapperWrite() {
		Unirest.setObjectMapper(new JacksonObjectMapper());

		GetResponse postResponseMock = new GetResponse();
		postResponseMock.setUrl("http://httpbin.org/post");

		HttpResponse<JsonNode> postResponse = Unirest.post(postResponseMock.getUrl()).header("accept", "application/json").header("Content-Type", "application/json").body(postResponseMock).asJson();

		assertEquals(200, postResponse.getStatus());
		assertEquals(postResponse.getBody().getObject().getString("data"), "{\"url\":\"http://httpbin.org/post\"}");
	}

	@Test
	public void testPostProvidesSortedParams() throws IOException {
		// Verify that fields are encoded into the body in sorted order.
		HttpRequest httpRequest = Unirest.post("test").field("z", "Z").field("y", "Y").field("x", "X").getHttpRequest();

		InputStream content = httpRequest.getBody().getEntity().getContent();
		String body = IOUtils.toString(content, "UTF-8");
		assertEquals("x=X&y=Y&z=Z", body);
	}

	@Test
	public void testHeaderNamesCaseSensitive() {
		// Verify that header names are the same as server (case sensitive)
		final Headers headers = new Headers();
		headers.put("Content-Type", Arrays.asList("application/json"));

		assertEquals("Only header \"Content-Type\" should exist", null, headers.getFirst("cOnTeNt-TyPe"));
		assertEquals("Only header \"Content-Type\" should exist", null, headers.getFirst("content-type"));
		assertEquals("Only header \"Content-Type\" should exist", "application/json", headers.getFirst("Content-Type"));
	}


	private String findAvailableIpAddress() throws IOException {
		for (int i = 100; i <= 255; i++) {
			String ip = "192.168.1." + i;
			if (!InetAddress.getByName(ip).isReachable(1000)) {
				return ip;
			}
		}

		throw new RuntimeException("Couldn't find an available IP address in the range of 192.168.0.100-255");
	}

    private RequestCapture parse(HttpResponse<JsonNode> response) {
        return read(response, RequestCapture.class);
    }
}
