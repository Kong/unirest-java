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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentationExamplesTest {
    @AfterEach
    public void tearDown(){
        MockClient.clear();
    }

    @Test
    void mockStatic(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("You can do anything!");

        assertEquals(
                "You can do anything!",
                Unirest.get("http://zombo.com").asString().getBody()
        );

        //Optional: Verify all expectations were fulfilled
        mock.verifyAll();
    }

    @Test
    void mockInstant(){
        UnirestInstance unirest = Unirest.spawnInstance();
        MockClient mock = MockClient.register(unirest);

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("You can do anything!");

        assertEquals(
                "You can do anything!",
                unirest.get("http://zombo.com").asString().getBody()
        );

        //Optional: Verify all expectations were fulfilled
        mock.verifyAll();
    }

    @Test
    void multipleExpects(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "https://somewhere.bad")
                .thenReturn("I'm Bad");

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("You can do anything!");

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .header("foo", "bar")
                .thenReturn("You can do anything with headers!");

        assertEquals(
                "You can do anything with headers!",
                Unirest.get("http://zombo.com")
                        .header("foo", "bar")
                        .asString().getBody()
        );

        assertEquals(
                "You can do anything!",
                Unirest.get("http://zombo.com")
                        .asString().getBody()
        );
    }

    @Test
    void validate(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "http://zombo.com")
                .thenReturn().withStatus(200);

        Unirest.post("http://zombo.com").asString().getBody();

        mock.verifyAll();
    }

    @Test
    void multipleValidates(){
        MockClient mock = MockClient.register();

        var zombo =    mock.expect(HttpMethod.POST, "http://zombo.com").thenReturn();
        var homestar = mock.expect(HttpMethod.DELETE, "http://homestarrunner.com").thenReturn();

        Unirest.post("http://zombo.com").asString().getBody();

        zombo.verify();
        homestar.verify(Times.never());
    }

    @Test
    void simpleBody() {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "http://zombo.com")
                .body("I can do anything? Anything at all?")
                .thenReturn()
                .withStatus(201);

        assertEquals(201,
                Unirest.post("http://zombo.com").body("I can do anything? Anything at all?").asEmpty().getStatus()
        );
    }

    @Test
    void formParams() {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "http://zombo.com")
                .body(FieldMatcher.of("foo", "bar",
                                      "baz", "qux"))
                .thenReturn()
                .withStatus(201);

        assertEquals(201,
                Unirest.post("http://zombo.com")
                        .field("foo", "bar")
                        .field("baz", "qux")
                        .asEmpty().getStatus()
        );
    }

    @Test
    void response() {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("Care for some tea mum?")
                .withHeader("x-zombo-brewing", "active")
                .withStatus(418, "I am a teapot");

        var response = Unirest.get("http://zombo.com").asString();

        assertEquals(418, response.getStatus());
        assertEquals("I am a teapot", response.getStatusText());
        assertEquals("Care for some tea mum?", response.getBody());
        assertEquals("active", response.getHeaders().getFirst("x-zombo-brewing"));
    }

    static class Teapot { public String brewstatus = "on"; }
    @Test
    void pojos() {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn(new Teapot());

        var response = Unirest.get("http://zombo.com").asString();

        assertEquals("{\"brewstatus\":\"on\"}", response.getBody());
    }
}
