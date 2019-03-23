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

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static kong.unirest.TestUtil.assertBasicAuth;
import static kong.unirest.TestUtil.mapOf;

public class HeaderTest extends BddTest {
    private String value = "one";

    @Test
    public void contentLengthIsSetWithBodies() {
        Unirest.post(MockServer.POST)
                .body("do do do do")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Content-Length", "11");
    }

    @Test
    public void testHeadersOnGetRequests() {
        Unirest.get(MockServer.GET)
                .header("user-agent", "hello-world")
                .accept("application/cheese-wiz")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("User-Agent", "hello-world")
                .assertHeader("Accept","application/cheese-wiz");
    }

    @Test
    public void testBasicAuth() {
        Unirest.get(MockServer.GET)
                .basicAuth("user", "test")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Authorization", "Basic dXNlcjp0ZXN0")
                .assertBasicAuth("user", "test");
    }

    @Test
    public void unicodeBasicAuth() {
        Unirest.get(MockServer.GET)
                .basicAuth("こんにちは", "こんにちは")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Authorization", "Basic 44GT44KT44Gr44Gh44GvOuOBk+OCk+OBq+OBoeOBrw==")
                .assertBasicAuth("こんにちは", "こんにちは");
    }

    @Test
    public void testDefaultHeaders() {
        Unirest.config().setDefaultHeader("X-Custom-Header", "hello");
        Unirest.config().setDefaultHeader("user-agent", "foobar");

        HttpResponse<JsonNode> jsonResponse = Unirest.get(MockServer.GET).asJson();

        parse(jsonResponse)
                .assertHeader("X-Custom-Header", "hello")
                .assertHeader("User-Agent", "foobar");

        jsonResponse = Unirest.get(MockServer.GET).asJson();
        parse(jsonResponse)
                .assertHeader("X-Custom-Header", "hello")
                .assertHeader("User-Agent", "foobar");

        Unirest.config().clearDefaultHeaders();

        jsonResponse = Unirest.get(MockServer.GET).asJson();
        parse(jsonResponse)
                .assertNoHeader("X-Custom-Header");
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
    public void headersOnMultipart() {
        Unirest.post(MockServer.POST)
                .field("one","a")
                .accept("application/json")
                .basicAuth("tony","tuna")
                .header("cheese", "cheddar")
                .contentType("application/xml")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Accept", "application/json")
                .assertBasicAuth("tony","tuna")
                .assertHeader("cheese", "cheddar")
                .assertHeader("Content-Type", "application/xml");

    }

    @Test
    public void canPassHeadersAsMap() {
        Unirest.post(MockServer.POST)
                .headers(mapOf("one", "foo", "two", "bar", "three", null))
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("one", "foo")
                .assertHeader("two", "bar")
                .assertHeader("three", "");
    }

    @Test
    public void basicAuthOnPosts() {
        Unirest.post(MockServer.POST)
                .basicAuth("user", "test")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Authorization", "Basic dXNlcjp0ZXN0")
                .assertBasicAuth("user", "test");
    }

    @Test
    public void canSetDefaultBasicAuth() {
        Unirest.config().setDefaultBasicAuth("bob", "pass");
        Unirest.config().setDefaultBasicAuth("user", "test");

        Unirest.post(MockServer.POST)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Authorization", "Basic dXNlcjp0ZXN0")
                .assertBasicAuth("user", "test");
    }

    @Test
    public void canOverrideDefaultBasicAuth() {
        Unirest.config().setDefaultBasicAuth("bob", "pass");

        Unirest.post(MockServer.POST)
                .basicAuth("user", "test")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Authorization", "Basic dXNlcjp0ZXN0")
                .assertBasicAuth("user", "test");

        Unirest.post(MockServer.POST)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Authorization", "Basic Ym9iOnBhc3M=")
                .assertBasicAuth("bob", "pass");
    }

    @Test
    public void willNotCacheBasicAuth() {
        Unirest.get(MockServer.GET)
                .basicAuth("george","guitar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("george","guitar");

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoHeader("Authorization");

        Unirest.get(MockServer.GET)
                .basicAuth("ringo","drums")
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("ringo","drums");
    }

    @Test
    public void willNotCacheHeadersAccrossRequests() {
        Unirest.get(MockServer.GET)
                .header("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("foo", "bar");

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoHeader("foo");

        Unirest.get(MockServer.GET)
                .header("baz", "qux")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("baz", "qux");
    }

    @Test
    public void doesNotCacheAcrossTypes(){
        Unirest.get(MockServer.GET)
                .basicAuth("user1","pass1")
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("user1","pass1");

        Unirest.post(MockServer.POST)
                .basicAuth("user2", "pass2")
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("user2", "pass2");
    }

    @Test @Ignore
    public void doesNotCacheAuthAcrossDomains(){
        Unirest.get(MockServer.GET)
                .basicAuth("user1","pass1")
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("user1","pass1");

        JsonNode bin = Unirest.post("http://httpbin.org/post")
                .basicAuth("user2", "pass2")
                .asJson()
                .getBody();

        String header = bin.getObject().getJSONObject("headers").getString("Authorization");
        assertBasicAuth(header, "user2", "pass2");
    }

    @Test //https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
    public void canHaveTheSameHeaderAddedTwice() {
        Unirest.config().setDefaultHeader("foo", "bar");

        Unirest.get(MockServer.GET)
                .header("foo", "qux")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeaderSize("foo", 2)
                .assertHeader("foo", "bar")
                .assertHeader("foo", "qux");
    }

    @Test
    public void canReplaceAHeader() {
        Unirest.config().setDefaultHeader("foo", "bar");
        Unirest.get(MockServer.GET)
                .headerReplace("foo", "qux")
                .headerReplace("fruit", "mango")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeaderSize("foo", 1)
                .assertHeaderSize("fruit", 1)
                .assertHeader("foo", "qux")
                .assertHeader("fruit", "mango");
    }

    @Test
    public void setVsAddDefaultHeaders() {
        Unirest.config().setDefaultHeader("foo", "bar")
                        .setDefaultHeader("foo", "qux")
                        .addDefaultHeader("fruit", "mango")
                        .addDefaultHeader("fruit", "orange")
                        .addDefaultHeader("colour","red")
                        .setDefaultHeader("colour","blue")
                        .addDefaultHeader("colour", "yellow");

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeaderSize("foo", 1)
                .assertHeader("foo", "qux")
                .assertHeaderSize("fruit", 2)
                .assertHeader("fruit", "mango")
                .assertHeader("fruit", "orange")
                .assertHeaderSize("colour", 2)
                .assertHeader("colour","blue")
                .assertHeader("colour", "yellow");
    }

    @Test
    public void canSetAHeaderAsASupplier() {
        Unirest.config().setDefaultHeader("trace", () -> value);

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("trace", "one");

        value = "two";

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("trace", "two");
    }

    @Test
    public void nullTests() {
        Unirest.get(MockServer.GET)
                .header("foo","bar")
                .headers(null)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("foo","bar");
    }
}
