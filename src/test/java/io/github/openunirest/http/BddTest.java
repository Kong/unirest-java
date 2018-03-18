package io.github.openunirest.http;

import org.junit.Before;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.github.openunirest.http.TestUtils.read;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BddTest {
    private JacksonObjectMapper objectMapper = new JacksonObjectMapper();
    private CountDownLatch lock;
    private boolean status;
    private String fail;

    @Before
    public void setUp() {
        MockServer.reset();
        Unirest.setObjectMapper(objectMapper);
        lock = new CountDownLatch(1);
        status = false;
    }

    public void assertAsync() throws InterruptedException {
        lock.await(5, TimeUnit.SECONDS);
        assertTrue("Expected a async call but it never responded", status);
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
        return read(response, RequestCapture.class);
    }
}
