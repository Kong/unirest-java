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
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class BddTest {
    private static JacksonObjectMapper objectMapper = new JacksonObjectMapper();
    private CountDownLatch lock;
    private boolean status;
    private String fail;

    @Before
    public void setUp() {
        //TestUtil.debugApache();
        MockServer.reset();
        Unirest.config().setObjectMapper(objectMapper);
        lock = new CountDownLatch(1);
        status = false;
    }

    @After
    public void tearDown() {
        Unirest.shutDown(true);
    }

    public void assertAsync()  {
        try {
            lock.await(5, TimeUnit.SECONDS);
            assertTrue("Expected a async call but it never responded", status);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void assertFailed(String message) throws InterruptedException {
        lock.await(5, TimeUnit.SECONDS);
        assertFalse("Should have failed", status);
        assertEquals(message, fail);
    }

    public void asyncSuccess() {
        status = true;
        lock.countDown();
    }

    public void asyncFail(String message) {
        status = false;
        lock.countDown();
        fail = message;
    }

    public static RequestCapture parse(HttpResponse<JsonNode> response) {
        assertEquals(200, response.getStatus());
        return objectMapper.readValue(response.getBody().toString(), RequestCapture.class);
    }
}
