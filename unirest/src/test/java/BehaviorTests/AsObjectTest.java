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

import com.google.gson.Gson;
import kong.unirest.*;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class AsObjectTest extends BddTest {

    @Test
    public void whenNoBodyIsReturned() {
        HttpResponse<RequestCapture> i = Unirest.get(MockServer.NOBODY).asObject(RequestCapture.class);

        Assert.assertEquals(200, i.getStatus());
        Assert.assertEquals(null, i.getBody());
    }

    @Test
    public void canGetObjectResponse() {
         Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    public void canGetObjectResponseAsync() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    public void canGetObjectResponseAsyncWithCallback() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObjectAsync(RequestCapture.class, r -> {
                    RequestCapture cap = r.getBody();
                    cap.assertParam("foo", "bar");
                    asyncSuccess();
                });

        assertAsync();
    }

    @Test
    public void canPassAnObjectMapperAsPartOfARequest(){
        TestingMapper mapper = new TestingMapper();
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .withObjectMapper(mapper)
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar");

        assertTrue(mapper.wasCalled);
    }

    @Test
    public void ifTheObjectMapperFailsReturnEmptyAndAddToParsingError() {
        HttpResponse<RequestCapture> request = Unirest.get(MockServer.INVALID_REQUEST)
                .asObject(RequestCapture.class);

        assertNull(request.getBody());
        assertTrue(request.getParsingError().isPresent());
        assertEquals("kong.unirest.UnirestException: com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'You': was expecting ('true', 'false' or 'null')\n" +
                " at [Source: (String)\"You did something bad\"; line: 1, column: 4]", request.getParsingError().get().getMessage());
        assertEquals("You did something bad", request.getParsingError().get().getOriginalBody());

    }

    @Test
    public void ifTheObjectMapperFailsReturnEmptyAndAddToParsingErrorObGenericTypes() {
        HttpResponse<RequestCapture> request = Unirest.get(MockServer.INVALID_REQUEST)
                .asObject(new GenericType<RequestCapture>() {});

        assertNull(request.getBody());
        assertTrue(request.getParsingError().isPresent());
        assertEquals("kong.unirest.UnirestException: com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'You': was expecting ('true', 'false' or 'null')\n" +
                " at [Source: (String)\"You did something bad\"; line: 1, column: 4]", request.getParsingError().get().getMessage());
        assertEquals("You did something bad", request.getParsingError().get().getOriginalBody());
    }

    @Test
    public void unirestExceptionsAreAlsoParseExceptions() {
        HttpResponse<RequestCapture> request = Unirest.get(MockServer.INVALID_REQUEST)
                .asObject(new GenericType<RequestCapture>() {});

        assertNull(request.getBody());
        assertTrue(request.getParsingError().isPresent());
        assertEquals("kong.unirest.UnirestException: com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'You': was expecting ('true', 'false' or 'null')\n" +
                " at [Source: (String)\"You did something bad\"; line: 1, column: 4]", request.getParsingError().get().getMessage());
        assertEquals("You did something bad", request.getParsingError().get().getOriginalBody());
    }

    public static class TestingMapper implements ObjectMapper {
        public boolean wasCalled;

        @Override
        public <T> T readValue(String value, Class<T> valueType) {
            this.wasCalled = true;
            return new JacksonObjectMapper().readValue(value, valueType);
        }

        @Override
        public String writeValue(Object value) {
            return new Gson().toJson(value);
        }
    }
}
