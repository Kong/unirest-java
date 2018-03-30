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
    public void failureToReturnValidJsonWillResultInAnEmptyNode() {
        HttpResponse<JsonNode> response = Unirest.get(MockServer.INVALID_REQUEST).asJson();

        assertEquals(400, response.getStatus());
        assertNull(response.getBody());
        assertEquals("You did something bad", TestUtil.toString(response.getRawBody()));
        assertEquals("org.json.JSONException: A JSONArray text must start with '[' at 1 [character 2 line 1]",
                response.getParsingError().get().getMessage());
    }

    @Test
    public void failureToReturnValidJsonWillResultInAnEmptyNodeAsync() {
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
