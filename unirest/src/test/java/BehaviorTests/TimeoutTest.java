/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package BehaviorTests;

import kong.unirest.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@Ignore
public class TimeoutTest extends BddTest {

    @Test
    public void timeoutCatching(){
        TestErrorHandler handler = new TestErrorHandler();
        Unirest.config().errorHandler(handler);
        assertNull(Unirest.get("http://localhost:0").asString());
        assertEquals("Connect to localhost:80 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused (Connection refused)", handler.caught.getMessage());
    }

    @Test
    public void testSetTimeouts() {
        String address = "http://localhost:0";
        long start = System.currentTimeMillis();
        try {
            Unirest.get(address).asString();
            fail("should have thrown");
        } catch (Exception e) {
            if (System.currentTimeMillis() - start > Config.DEFAULT_CONNECTION_TIMEOUT + 100) { // Add 100ms for code execution
                fail();
            }
        }
        Unirest.config().reset();
        Unirest.config().connectTimeout(2000).socketTimeout(10000);

        start = System.currentTimeMillis();
        try {
            Unirest.get(address).asString();
            fail("should have thrown");
        } catch (Exception e) {
            if (System.currentTimeMillis() - start > 2100) { // Add 100ms for code execution
                fail();
            }
        }
    }

    @Test
    @Ignore // this is flakey
    public void parallelTest() throws InterruptedException {
        Unirest.config().connectTimeout(10).socketTimeout(5);

        long start = System.currentTimeMillis();
        makeParallelRequests();
        long smallerConcurrencyTime = (System.currentTimeMillis() - start);

        Unirest.config().connectTimeout(200).socketTimeout(20);
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
            Unirest.config().connectTimeout(1000).socketTimeout(2000);
        } catch (Exception e) {
            fail();
        }

        try {
            Unirest.config().asyncClient(HttpAsyncClientBuilder.create().build());
        } catch (Exception e) {
            fail();
        }

        try {
            Unirest.config().asyncClient(HttpAsyncClientBuilder.create().build());
            Unirest.config().connectTimeout(1000).socketTimeout(2000);
            fail();
        } catch (Exception e) {
            // Ok
        }

        try {
            Unirest.config().httpClient(HttpClientBuilder.create().build());
            Unirest.config().connectTimeout(1000).socketTimeout(2000);
            fail();
        } catch (Exception e) {
            // Ok
        }
    }

}
