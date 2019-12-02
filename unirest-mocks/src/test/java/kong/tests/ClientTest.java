package kong.tests;

import kong.unirest.Assert;
import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONObject;
import org.junit.Test;

import static kong.unirest.HttpMethod.GET;
import static org.junit.Assert.assertEquals;

public class ClientTest extends Base {

    @Test
    public void simpleGetString() {
        client.expect(HttpMethod.GET, path)
                .thenReturn("Hello World");

        assertEquals("Hello World", uni.get(path).asString().getBody());
    }

    @Test
    public void simpleGetBytes() {
        client.expect(HttpMethod.GET, path)
                .thenReturn("Hello World");

        byte[] body = uni.get(path).asBytes().getBody();
        assertEquals("Hello World", new String(body));
    }

    @Test
    public void simpleJson() {
        client.expect(HttpMethod.GET, path)
                .thenReturn("{\"fruit\": \"apple\"}");

        assertEquals("apple",
                uni.get(path).asJson().getBody().getObject().getString("fruit"));
    }

    @Test
    public void setReturnAsJson() {
        client.expect(HttpMethod.GET, path)
                .thenReturn(new JSONObject("{\"fruit\": \"apple\"}"));

        assertEquals("apple",
                uni.get(path).asJson().getBody().getObject().getString("fruit"));
    }

    @Test
    public void canPassInAndReturnObjectsAsJson() {
        client.expect(HttpMethod.GET, path)
                .thenReturn(new Pojo("apple"));
    }

    @Test
    public void canAssertANumberOfTimes() {
        uni.get(path).asEmpty();
        uni.get(path).asEmpty();

        Assert exp = client.assertThat(HttpMethod.GET, path);
        exp.assertInvokedTimes(2);

        assertException(() -> exp.assertInvokedTimes(1),
                "Incorrect number of invocations. Expected 1 got 2\n" +
                        "GET http://basic");

        assertException(() -> exp.assertInvokedTimes(3),
                "Incorrect number of invocations. Expected 3 got 2\n" +
                        "GET http://basic");
    }

    @Test
    public void noExpectationsAtAll() {
        uni.get(path).asEmpty();
        client.verifyAll();

    }

    @Test
    public void noExpectation() {
        client.expect(GET, otherPath);
        assertException(() -> client.verifyAll(),
                "A expectation was never invoked! GET http://other\n" +
                        "Headers:\n");
    }

    @Test
    public void noInvocationHappened() {
        assertException(() -> client.assertThat(GET, path),
                "No Matching Invocation:: GET http://basic");
    }

    @Test
    public void assertHeader() {
        uni.get(path).header("monster", "grover").asEmpty();

        Assert expect = client.assertThat(GET, path);
        expect.assertHeader("monster", "grover");

        assertException(() -> expect.assertHeader("monster", "oscar"),
                "No invocation found with header [monster: oscar]\nFound:\nmonster: grover\n");
    }

    @Test
    public void canSetHeaderExpectationOnExpects() {
        client.expect(GET, path).andHeader("monster", "grover");

        assertException(() -> client.verifyAll(),
                "A expectation was never invoked! GET http://basic\n" +
                        "Headers:\n" +
                        "monster: grover\n");

        uni.get(path).header("monster", "grover").asEmpty();

        client.verifyAll();
    }

    @Test
    public void canSetResponseHeaders() {
        client.expect(GET, path)
                .thenReturn("foo")
                .withHeader("monster", "grover");

        HttpResponse<String> rez = uni.get(path).asString();
        assertEquals("foo", rez.getBody());
        assertEquals("grover", rez.getHeaders().getFirst("monster"));
    }

    @Test
    public void canReturnEmptyWithHeaders() {
        client.expect(GET, path)
                .thenReturn()
                .withHeader("monster", "grover");

        HttpResponse<String> rez = uni.get(path).asString();
        assertEquals(null, rez.getBody());
        assertEquals("grover", rez.getHeaders().getFirst("monster"));
    }

    @Test
    public void canDifferenciateBetweenExpectations() {
        client.expect(GET, path).andHeader("monster", "oscar");
        client.expect(GET, path).andHeader("monster", "grover");

        uni.get(path).header("monster", "oscar").asEmpty();

        assertException(() -> client.verifyAll(),
                "A expectation was never invoked! GET http://basic\n" +
                        "Headers:\n" +
                        "monster: grover\n");
    }

}
