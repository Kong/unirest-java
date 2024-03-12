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

import static kong.unirest.core.HttpMethod.GET;
import static kong.unirest.core.HttpMethod.POST;
import static org.junit.jupiter.api.Assertions.*;

public class VerifyTimesTest extends Base {
    @Test
    void assertJustOne() {
        var response = client.expect(HttpMethod.POST, path)
                .thenReturn(MockResponse.of(500, null));

        assertThrows(UnirestAssertion.class, response::verify);

        Unirest.post(path).asEmpty();

        assertDoesNotThrow(() -> response.verify());
    }

    @Test
    void assertFromTheExpect() {
        var expect = client.expect(POST, path);
        expect.thenReturn(MockResponse.of(500, null));

        assertThrows(UnirestAssertion.class, expect::verify);

        Unirest.post(path).asEmpty();

        assertDoesNotThrow(() -> expect.verify());
    }

    @Test
    void assertJustOneWithFunctionalInterface() {
        var response = ExpectedResponse.of(200)
                .thenReturn("Hello Jane")
                .withHeader("foo", "bar");

        client.expect(HttpMethod.POST, path)
                .thenReturn(r -> response);

        assertThrows(UnirestAssertion.class, response::verify);

        Unirest.post(path).asEmpty();

        assertDoesNotThrow(() -> response.verify());
    }

    @Test
    void verifyExactlyOnce() {
        var expect = client.expect(GET, path).thenReturn("hi");

        Unirest.get(path).asEmpty();

        assertDoesNotThrow(() -> expect.verify(Times.exactlyOnce()));

        Unirest.get(path).asEmpty();

        var ex = assertThrows(UnirestAssertion.class, () ->  expect.verify(Times.exactly(1)));
        assertEquals("Expected exactly 1 invocations but got 2\n" +
                "GET http://basic\n", ex.getMessage());
    }

    @Test
    void verifyAtLeast() {
        var expect = client.expect(GET, path).thenReturn("hi");
        Unirest.get(path).asEmpty();

        var ex = assertThrows(UnirestAssertion.class, () ->  expect.verify(Times.atLeast(2)));
        assertEquals("Expected at least 2 invocations but got 1\n" +
                "GET http://basic\n", ex.getMessage());

        Unirest.get(path).asEmpty();

        assertDoesNotThrow(() -> expect.verify(Times.atLeast(2)));
    }

    @Test
    void verifyNever() {
        var expect = client.expect(GET, path).thenReturn("hi");

        assertDoesNotThrow(() -> expect.verify(Times.never()));

        Unirest.get(path).asEmpty();

        var ex = assertThrows(UnirestAssertion.class, () ->  expect.verify(Times.never()));
        assertEquals("Expected exactly 0 invocations but got 1\n" +
                "GET http://basic\n", ex.getMessage());

        Unirest.get(path).asEmpty();
    }

    @Test
    void verifyAtMost() {
        var expect = client.expect(GET, path).thenReturn("hi");

        assertDoesNotThrow(() -> expect.verify(Times.atMost(1)));

        Unirest.get(path).asEmpty();
        assertDoesNotThrow(() -> expect.verify(Times.atMost(1)));

        Unirest.get(path).asEmpty();

        var ex = assertThrows(UnirestAssertion.class, () ->  expect.verify(Times.atMost(1)));
        assertEquals("Expected no more than 1 invocations but got 2\n" +
                "GET http://basic\n", ex.getMessage());

        Unirest.get(path).asEmpty();
    }

    @Test
    void buildExpectationTimesIntoChain() {
        var expect = client.expect(GET, path).times(Times.never()).thenReturn("hi");

        assertDoesNotThrow(() -> expect.verify());

        Unirest.get(path).asEmpty();

        var ex = assertThrows(UnirestAssertion.class, () -> expect.verify());
        assertEquals("Expected exactly 0 invocations but got 1\n" +
                "GET http://basic\n", ex.getMessage());
    }
}
