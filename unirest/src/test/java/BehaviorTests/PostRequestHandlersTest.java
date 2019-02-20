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

import org.junit.After;
import org.junit.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import static org.junit.Assert.*;

public class PostRequestHandlersTest extends BddTest {

    private HttpResponse<RequestCapture> captured;

    @Override
    @After
    public void tearDown() {
        super.tearDown();
        captured = null;
    }

    @Test
    public void onSuccessDoSomething() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .ifSuccess(r -> captured = r)
                .ifFailure(r -> fail("should not have been called"));

        assertNotNull(captured);
        captured.getBody().assertParam("foo", "bar");
    }

    @Test
    public void onFailDoSomething() {
        Unirest.get(MockServer.INVALID_REQUEST)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .ifFailure(r -> captured = r)
                .ifSuccess(r -> fail("should not have been called"));

        assertNotNull(captured);
        assertEquals(400, captured.getStatus());
    }

    @Test
    public void itsAFailIfTheMapperFails() {
        MockServer.setStringResponse("not what you expect");

        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .ifFailure(r -> captured = r)
                .ifSuccess(r -> fail("should not have been called"));

        assertNotNull(captured);
        assertEquals(200, captured.getStatus());
        assertTrue(captured.getParsingError().isPresent());
        assertEquals("not what you expect", captured.getParsingError().get().getOriginalBody());
    }



    @Test
    public void onSuccessBeSuccessful() {
        HttpResponse<RequestCapture> response = Unirest.get(MockServer.GET)
            .queryString("foo", "bar")
            .asObject(RequestCapture.class);

        assertTrue(response.isSuccess());
    }

    @Test
    public void onFailBeUnsuccessful() {
        HttpResponse<RequestCapture> response = Unirest.get(MockServer.INVALID_REQUEST)
            .queryString("foo", "bar")
            .asObject(RequestCapture.class);

        assertFalse(response.isSuccess());
    }

    @Test
    public void beUnsuccessfulIfTheMapperFails() {
        MockServer.setStringResponse("not what you expect");

        HttpResponse<RequestCapture> response = Unirest.get(MockServer.GET)
            .queryString("foo", "bar")
            .asObject(RequestCapture.class);

        assertFalse(response.isSuccess());
    }
}
