package io.github.openunirest.http;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.github.openunirest.http.TestUtils.read;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BddTest {

    private CountDownLatch lock;
    private boolean status;

    @BeforeClass
    public static void suiteSetUp() {
        MockServer.start();
    }

    @AfterClass
    public static void suiteTearDown() {
        MockServer.shutdown();
    }

    @Before
    public void setUp() {
        MockServer.reset();
        lock = new CountDownLatch(1);
        status = false;
    }

    protected void assertAsync() throws InterruptedException {
        lock.await(10, TimeUnit.SECONDS);
        assertTrue(status);
    }

    protected void startCountdown() {
        status = true;
        lock.countDown();
    }

    public static RequestCapture parse(HttpResponse<JsonNode> response) {
        assertEquals(200, response.getStatus());
        return read(response, RequestCapture.class);
    }
}
