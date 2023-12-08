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

package kong.tests;

import kong.unirest.core.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static kong.unirest.core.HttpMethod.GET;
import static kong.unirest.core.HttpMethod.POST;
import static org.junit.jupiter.api.Assertions.*;

class AssertTest extends Base {

    @Test
    void canAssertANumberOfTimes() {
        Unirest.get(path).asEmpty();
        Unirest.get(path).asEmpty();

        Assert exp = client.assertThat(HttpMethod.GET, path);
        exp.wasInvokedTimes(2);

        assertException(() -> exp.wasInvokedTimes(1),
                "Incorrect number of invocations. Expected 1 got 2\n" +
                        "GET http://basic");

        assertException(() -> exp.wasInvokedTimes(3),
                "Incorrect number of invocations. Expected 3 got 2\n" +
                        "GET http://basic");
    }

    @Test
    void noExpectationsAtAll() {
        Unirest.get(path).asEmpty();
        client.verifyAll();

    }

    @Test
    void noExpectation() {
        client.expect(GET, otherPath);
        assertException(() -> client.verifyAll(),
                "Expected at least 1 invocations but got 0\n" +
                        "GET http://other\n");
    }

    @Test
    void noInvocationHappened() {
        assertException(() -> client.assertThat(GET, path),
                "No Matching Invocation:: GET http://basic");
    }

    @Test
    void assertHeader() {
        Unirest.get(path).header("monster", "grover").asEmpty();

        Assert expect = client.assertThat(GET, path);
        expect.hadHeader("monster", "grover");

        assertException(() -> expect.hadHeader("monster", "oscar"),
                "No invocation found with header [monster: oscar]\nFound:\nmonster: grover");
    }

    @Test
    void canSetHeaderExpectationOnExpects() {
        client.expect(GET, path).header("monster", "grover");

        assertException(() -> client.verifyAll(),
                "Expected at least 1 invocations but got 0\n" +
                        "GET http://basic\n" +
                        "Headers:\n" +
                        "monster: grover");

        Unirest.get(path).header("monster", "grover").asEmpty();

        client.verifyAll();
    }

    @Test
    public void expectAnyPath(){
        client.expect(HttpMethod.GET)
                .thenReturn("woh");

        Unirest.get(path).asEmpty();

        client.verifyAll();
    }

    @Test
    void canExpectQueryParams() {
        client.expect(GET, path).queryString("monster", "grover");

        Unirest.get(path).asEmpty();

        assertException(() -> client.verifyAll());

        Unirest.get(path).queryString("monster", "grover").asEmpty();

        client.verifyAll();
    }

    @Test
    void expectBody() {
        client.expect(POST, path)
                .body("foo")
                .thenReturn("bar");

        assertNull(Unirest.post(path).asString().getBody());
        assertEquals("bar", Unirest.post(path).body("foo").asString().getBody());
    }

    @Test
    void expectBodyFailure() {
        client.expect(POST, path)
                .body("foo")
                .thenReturn("bar");

        Unirest.post(path).body("baz").asString();

        assertException(() -> client.verifyAll(),
                "Expected at least 1 invocations but got 0\n" +
                        "POST http://basic\n" +
                        "Body:\n" +
                        "\tfoo");
    }

    @Test
    void assertBody() {
        client.expect(POST, path);

        Unirest.post(path).body("hey buddy").asString();

        client.assertThat(POST, path).hadBody("hey buddy");
    }

    @Test
    void assertBody_fail() {
        client.expect(POST, path);

        Unirest.post(path).body("hey buddy").asString();

        assertThrows(UnirestAssertion.class,
                () -> client.assertThat(POST, path).hadBody("I'm a big ol beat"));
    }

    @Test
    void assertBody_multipart() {
        client.expect(POST, path);

        Unirest.post(path).field("hey", "buddy").asString();

        client.assertThat(POST, path).hadField("hey", "buddy");
    }

    @Test
    void assertBody_multipart_fails() {
        client.expect(POST, path);

        Unirest.post(path).field("hey", "buddy").asString();
        assertThrows(UnirestAssertion.class, () -> client.assertThat(POST, path).hadField("nope", "buddy"));
        assertThrows(UnirestAssertion.class, () -> client.assertThat(POST, path).hadField("hey", "nope"));
    }

    @Test
    void canResetExpects() {
        client.expect(GET).thenReturn("HI");
        client.reset();
        assertEquals(null, Unirest.get(path).asString().getBody());
    }

    @Test
    void canBeStrictAndForbidAnythingWithoutAMatch() {
        client.defaultResponse().withStatus(400, "wtf")
                .thenReturn("boo");

        client.expect(GET, otherPath).thenReturn("Hi");

        HttpResponse<String> res = Unirest.get(path).asString();
        assertEquals(400, res.getStatus());
        assertEquals("wtf", res.getStatusText());
        assertEquals("boo", res.getBody());

        assertEquals("Hi", Unirest.get(otherPath).asString().getBody());
    }

    @Test
    void canSetASupplierForTheReponseBody() {
        BodyBuddy supplier = new BodyBuddy();
        client.expect(GET).thenReturn(supplier);
        supplier.body = "Hey Buddy";
        assertEquals("Hey Buddy", Unirest.get(path).asString().getBody());
        supplier.body = "Yeaaaah Buddy";
        assertEquals("Yeaaaah Buddy", Unirest.get(path).asString().getBody());
    }

    @Test
    void assertPostWithNoBody() {
        client.expect(HttpMethod.POST, "myurl").body("test").thenReturn().withStatus(200);
        UnirestAssertion ex = assertThrows(UnirestAssertion.class, () -> client.verifyAll());
        assertEquals("Expected at least 1 invocations but got 0\n" +
                "POST myurl\n" +
                "Body:\n" +
                "\t null", ex.getMessage());
    }

    @Test
    void returnAMockResponseObject() {
        client.expect(HttpMethod.POST, path)
                .thenReturn(MockResponse.of(500, "a 500 brah", "error")
                        .withHeader("cool", "beans"));

        HttpResponse<String> response = Unirest.post(path).asString();

        assertEquals(500, response.getStatus());
        assertEquals("a 500 brah", response.getStatusText());
        assertEquals("error", response.getBody());
        assertEquals(List.of("beans"), response.getHeaders().get("cool"));
    }

    @Test
    void returnAMockResponseObjectWithoutStuff() {
        client.expect(HttpMethod.POST, path)
                .thenReturn(MockResponse.of(500, null));

        HttpResponse<String> response = Unirest.post(path).asString();

        assertEquals(500, response.getStatus());
        assertNull(response.getBody());
        assertEquals(List.of(), response.getHeaders().get("cool"));
    }

    @Test
    void verbsAreImportant() {
        client.expect(GET, path).thenReturn("hi");
        assertNotEquals("hi", Unirest.post(path).asString().getBody());
    }

    private static class BodyBuddy implements Supplier<String>{
        String body;
        @Override
        public String get() {
            return body;
        }
    }
}
