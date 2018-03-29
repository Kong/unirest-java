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

import io.github.openunirest.http.async.MockCallback;
import io.github.openunirest.http.exceptions.UnirestException;
import io.github.openunirest.http.options.Options;
import io.github.openunirest.request.GetRequest;
import io.github.openunirest.request.HttpRequest;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class UnirestTest extends BddTest {

    @Test
    public void postFormReturnJson() throws JSONException, UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param1", "value1")
                .field("param2", "bye")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());

        parse(jsonResponse)
                .assertHeader("Accept", "application/json")
                .assertParam("param1", "value1")
                .assertParam("param2", "bye");
    }

    @Test
    public void canPassQueryParamsOnStringOrWithForm() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET + "?name=mark")
                .header("accept", "application/json")
                .asJson();

        parse(response)
                .assertParam("name", "mark");

        response = Unirest.get(MockServer.GET)
                .header("accept", "application/json")
                .queryString("name", "mark2")
                .asJson();

        parse(response)
                .assertParam("name", "mark2");
    }

    @Test
    public void multipleParams() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET + "?name=ringo")
                .header("accept", "application/json")
                .queryString("name", "paul")
                .queryString("name", "john")
                .asJson();

        parse(response)
                .assertParam("name", "ringo")
                .assertParam("name", "paul")
                .assertParam("name", "john");
    }

    @Test
    public void testGetUTF8() {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET)
                .header("accept", "application/json")
                .queryString("param3", "こんにちは")
                .asJson();

        parse(response)
                .assertParam("param3", "こんにちは");
    }

    @Test
    public void testPostUTF8() {
        HttpResponse response = Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param3", "こんにちは")
                .asJson();

        parse(response)
                .assertParam("param3", "こんにちは");
    }

    @Test
    public void testPostBinaryUTF8() throws URISyntaxException {
        HttpResponse<JsonNode> response = Unirest.post(MockServer.POST)
                .header("Accept", ContentType.MULTIPART_FORM_DATA.getMimeType())
                .field("param3", "こんにちは")
                .field("file", new File(getClass().getResource("/test").toURI()))
                .asJson();

        parse(response)
                .assertParam("param3", "こんにちは")
                .assertFileContent("file", "This is a test file");
    }

    @Test
    public void testCustomUserAgent() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET)
                .header("user-agent", "hello-world")
                .asJson();

        RequestCapture json = parse(response);
        json.assertHeader("User-Agent", "hello-world");
    }

    @Test
    public void testGetMultiple() throws JSONException, UnirestException {
        for (int i = 1; i <= 20; i++) {
            HttpResponse<JsonNode> response = Unirest.get(MockServer.GET + "?try=" + i).asJson();
            parse(response).assertParam("try", String.valueOf(i));
        }
    }

    @Test
    public void testGetFields() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET)
                .queryString("name", "mark")
                .queryString("nick", "thefosk")
                .asJson();

        RequestCapture parse = parse(response);
        parse.assertParam("name", "mark");
        parse.assertParam("nick", "thefosk");
    }

    @Test
    public void testGetFields2() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET)
                .queryString("email", "hello@hello.com")
                .asJson();

        parse(response).assertParam("email", "hello@hello.com");
    }

    @Test
    public void testQueryStringEncoding() throws JSONException, UnirestException {
        String testKey = "email2=someKey&email";
        String testValue = "hello@hello.com";

        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET)
                .queryString(testKey, testValue)
                .asJson();

        parse(response).assertParam(testKey, testValue);
    }

    @Test
    public void testDelete() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.delete(MockServer.DELETE).asJson();
        assertEquals(200, response.getStatus());

        response = Unirest.delete(MockServer.DELETE)
                .field("name", "mark")
                .field("foo", "bar")
                .asJson();

        RequestCapture parse = parse(response);
        parse.assertParam("name", "mark");
        parse.assertParam("foo", "bar");
    }

    @Test
    public void testBasicAuth() throws JSONException, UnirestException {
        Unirest.get(MockServer.GET)
                .basicAuth("user", "test")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Authorization", "Basic dXNlcjp0ZXN0");
    }

    @Test
    public void unicodeBasicAuth() throws JSONException, UnirestException {
        Unirest.get(MockServer.GET)
                .basicAuth("こんにちは", "こんにちは")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Authorization", "Basic 44GT44KT44Gr44Gh44GvOuOBk+OCk+OBq+OBoeOBrw==")
                .assertBasicAuth("こんにちは", "こんにちは");

    }

    @Test
    public void testAsync() throws JSONException, InterruptedException, ExecutionException {
        Future<HttpResponse<JsonNode>> future = Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param1", "value1")
                .field("param2", "bye")
                .asJsonAsync();

        assertNotNull(future);

        RequestCapture req = parse(future.get());
        req.assertParam("param1", "value1");
        req.assertParam("param2", "bye");
    }

    @Test
    public void testAsyncCallback() throws JSONException, InterruptedException {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param1", "value1")
                .field("param2", "bye")
                .asJsonAsync(new MockCallback<>(this, r -> {
                    RequestCapture req = parse(r);
                    req.assertParam("param1", "value1");
                    req.assertParam("param2", "bye");
                }));

        assertAsync();
    }

    @Test
    public void testMultipart() throws JSONException, URISyntaxException, UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new File(getClass().getResource("/test").toURI()))
                .asJson();

        RequestCapture res = parse(jsonResponse);
        res.getFile("test").assertBody("This is a test file");
        res.getFile("test").assertFileType("application/octet-stream");

        res.assertParam("name", "Mark");
    }

    @Test
    public void testMultipartContentType() throws JSONException, URISyntaxException, UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new File(getClass().getResource("/image.jpg").toURI()), "image/jpeg")
                .asJson();

        RequestCapture res = parse(jsonResponse);
        res.getFile("image.jpg").assertFileType("image/jpeg");
        res.assertParam("name", "Mark");
    }

    @Test
    public void testMultipartInputStreamContentType() throws JSONException, URISyntaxException, FileNotFoundException {
        FileInputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));

        HttpResponse<JsonNode> request = Unirest.post(MockServer.POST)
                .header("accept", ContentType.MULTIPART_FORM_DATA.toString())
                .field("name", "Mark")
                .field("file", stream, ContentType.APPLICATION_OCTET_STREAM, "image.jpg")
                .asJson();

        assertEquals(200, request.getStatus());

        parse(request)
                .assertHeader("Accept", ContentType.MULTIPART_FORM_DATA.toString())
                .assertParam("name", "Mark")
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartInputStreamContentTypeAsync() throws JSONException, InterruptedException, URISyntaxException, FileNotFoundException {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new FileInputStream(new File(getClass().getResource("/test").toURI())), ContentType.APPLICATION_OCTET_STREAM, "test")
                .asJsonAsync(new MockCallback<>(this, r -> parse(r)
                        .assertParam("name", "Mark")
                        .getFile("test")
                        .assertFileType("application/octet-stream"))
                );

        assertAsync();
    }

    @Test
    public void testMultipartByteContentType() throws JSONException, URISyntaxException, IOException {
        final InputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));
        final byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        stream.close();

        HttpResponse<JsonNode> jsonResponse = Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", bytes, "image.jpg")
                .asJson();

        RequestCapture parse = parse(jsonResponse);
        parse.getFile("image.jpg").assertFileType("application/octet-stream");
        parse.assertParam("name", "Mark");
    }

    @Test
    public void testMultipartByteContentTypeAsync() throws Exception {
        final InputStream stream = new FileInputStream(new File(getClass().getResource("/test").toURI()));
        final byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        stream.close();

        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", bytes, "test")
                .asJsonAsync(new MockCallback<>(this, r ->
                        parse(r)
                                .assertParam("name", "Mark")
                                .getFile("test")
                                .assertFileType("application/octet-stream"))
                );

        assertAsync();
    }

    @Test
    public void testMultipartAsync() throws Exception {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new File(getClass().getResource("/test").toURI()))
                .asJsonAsync(new MockCallback<>(this, r ->
                        parse(r)
                                .assertParam("name", "Mark")
                                .getFile("test")
                                .assertFileType("application/octet-stream")
                                .assertBody("This is a test file"))
                );

        assertAsync();
    }

    @Test
    public void testGzip() throws JSONException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(MockServer.GZIP)
                .queryString("zipme", "up")
                .asJson();

        parse(jsonResponse).assertParam("zipme", "up");
    }

    @Test
    public void testGzipAsync() throws JSONException, InterruptedException, ExecutionException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(MockServer.GZIP)
                .queryString("zipme", "up")
                .asJsonAsync()
                .get();

        parse(jsonResponse).assertParam("zipme", "up");
    }

    @Test
    public void testDefaultHeaders() throws JSONException {
        Unirest.setDefaultHeader("X-Custom-Header", "hello");
        Unirest.setDefaultHeader("user-agent", "foobar");

        HttpResponse<JsonNode> jsonResponse = Unirest.get(MockServer.GET).asJson();

        parse(jsonResponse)
                .assertHeader("X-Custom-Header", "hello")
                .assertHeader("User-Agent", "foobar");

        jsonResponse = Unirest.get(MockServer.GET).asJson();
        parse(jsonResponse)
                .assertHeader("X-Custom-Header", "hello")
                .assertHeader("User-Agent", "foobar");

        Unirest.clearDefaultHeaders();

        jsonResponse = Unirest.get(MockServer.GET).asJson();
        parse(jsonResponse)
                .assertNoHeader("X-Custom-Header");
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
        HttpResponse<JsonNode> jsonResponse = Unirest.get(MockServer.HOST + "/{method}")
                .routeParam("method", "get")
                .queryString("name", "Mark")
                .asJson();

        parse(jsonResponse)
                .assertParam("name", "Mark");
    }

    @Test
    public void testQueryAndBodyParameters() {
        HttpResponse<JsonNode> jsonResponse = Unirest.post(MockServer.HOST + "/{method}")
                .routeParam("method", "post")
                .queryString("name", "Mark")
                .field("wot", "wat")
                .asJson();

        parse(jsonResponse)
                .assertParam("name", "Mark")
                .assertParam("wot", "wat");
    }

    @Test
    public void testPathParameters2() {
        HttpResponse<JsonNode> jsonResponse = Unirest.patch(MockServer.HOST + "/{method}")
                .routeParam("method", "patch")
                .field("name", "Mark")
                .asJson();

        parse(jsonResponse)
                .assertParam("name", "Mark");
    }

    @Test
    public void testMissingPathParameter() {
        try {
            Unirest.get(MockServer.HOST + "/{method}")
                    .routeParam("method222", "get")
                    .queryString("name", "Mark")
                    .asJson();

            fail();
        } catch (RuntimeException e) {
            assertEquals("Can't find route parameter name \"method222\"", e.getMessage());
        }
    }

    @Test
    @Ignore // this is flakey
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
                    Unirest.get(MockServer.GET).queryString("index", counter.incrementAndGet()).asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        newFixedThreadPool.shutdown();
        newFixedThreadPool.awaitTermination(10, TimeUnit.MINUTES);
    }

    @Test
    public void testAsyncCustomContentTypeAndFormParams() throws InterruptedException {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("name", "Mark")
                .field("hello", "world")
                .asJsonAsync(new MockCallback<>(this, r -> parse(r)
                                .assertParam("name", "Mark")
                                .assertParam("hello", "world")
                                .assertHeader("Content-Type", "application/x-www-form-urlencoded")
                ));

        assertAsync();
    }


    @Test
    public void testGetQuerystringArray() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET)
                .queryString("name", "Mark")
                .queryString("name", "Tom")
                .asJson();

        parse(response)
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testPostMultipleFiles() throws JSONException, URISyntaxException {
        HttpResponse<JsonNode> response = Unirest.post(MockServer.POST)
                .field("param3", "wot")
                .field("file1", new File(getClass().getResource("/test").toURI()))
                .field("file2", new File(getClass().getResource("/test").toURI()))
                .asJson();

        parse(response)
                .assertParam("param3", "wot")
                .assertFileContent("file1", "This is a test file")
                .assertFileContent("file2", "This is a test file");
    }

    @Test
    public void testGetArray() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.GET)
                .queryString("name", Arrays.asList("Mark", "Tom"))
                .asJson();

        parse(response)
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testPostArray() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("name", "Tom")
                .asJson();

        parse(response)
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testPostCollection() throws JSONException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.post(MockServer.POST)
                .field("name", Arrays.asList("Mark", "Tom"))
                .asJson();

        parse(response)
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testCaseInsensitiveHeaders() {
        GetRequest request = Unirest.get(MockServer.GET)
                .header("Name", "Marco");

        assertEquals(1, request.getHeaders().size());
        assertEquals("Marco", request.getHeaders().get("name").get(0));
        assertEquals("Marco", request.getHeaders().get("NAme").get(0));
        assertEquals("Marco", request.getHeaders().get("Name").get(0));

        parse(request.asJson())
                .assertHeader("Name", "Marco");

        request = Unirest.get(MockServer.GET).header("Name", "Marco").header("Name", "John");
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
        getResponseMock.setUrl(MockServer.GET);

        HttpResponse<GetResponse> getResponse = Unirest.get(getResponseMock.getUrl())
                .asObject(GetResponse.class);

        assertEquals(200, getResponse.getStatus());
        assertEquals(getResponse.getBody().getUrl(), getResponseMock.getUrl());
    }

    @Test
    public void testPostProvidesSortedParams() throws IOException {
        // Verify that fields are encoded into the body in sorted order.
        HttpRequest httpRequest = Unirest.post("test")
                .field("z", "Z")
                .field("y", "Y")
                .field("x", "X")
                .getHttpRequest();

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

    @Test
    public void failureToReturnValidJsonWillResultInAnEmptyNode() {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.INVALID_REQUEST).asJson();

        assertEquals(400, response.getStatus());
        assertNull(response.getBody());
        assertEquals("You did something bad", TestUtil.toString(response.getRawBody()));
        assertEquals("org.json.JSONException: A JSONArray text must start with '[' at 1 [character 2 line 1]",
                response.getParsingError().get().getMessage());
    }

    @Test
    public void failureToReturnValidJsonWillResultInAnEmptyNodeAsync() throws InterruptedException {
        Unirest.get(MockServer.INVALID_REQUEST)
                .asJsonAsync(new MockCallback<>(this, response -> {
                        assertEquals(400, response.getStatus());
                        assertNull(response.getBody());
                        assertEquals("You did something bad", TestUtil.toString(response.getRawBody()));
                        assertEquals("org.json.JSONException: A JSONArray text must start with '[' at 1 [character 2 line 1]",
                                response.getParsingError().get().getMessage());

                    }));

        assertAsync();
    }

    @Test
    public void willNotCacheBasicAuth() {
        RequestCapture r1 = parse(Unirest.get(MockServer.GET).basicAuth("foo", "bar").asJson());
        RequestCapture r2 = parse(Unirest.get(MockServer.ALTGET).basicAuth("baz", "qux").asJson());

        r1.assertBasicAuth("foo", "bar");
        r2.assertBasicAuth("baz", "qux");
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
}
