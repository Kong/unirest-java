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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.gson.Gson;
import kong.unirest.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AsObjectTest extends BddTest {

    @Test
    void basicJsonObjectMapperIsTheDefault() {
        Unirest.config().shutDown(true);
        assertThat(Unirest.config().getObjectMapper(), instanceOf(JsonObjectMapper.class));
    }

    @Test
    void basicJsonObjectMapperIsGoodEnough() {
        Unirest.config().shutDown(true);
        MockServer.setJsonAsResponse(new Foo("bar"));

        Foo f = Unirest.get(MockServer.GET)
                .asObject(Foo.class)
                .getBody();

        assertEquals("bar", f.bar);
    }

    @Test
    void whenNoBodyIsReturned() {
        HttpResponse<RequestCapture> i = Unirest.get(MockServer.NOBODY).asObject(RequestCapture.class);

        assertEquals(200, i.getStatus());
        assertEquals(null, i.getBody());
    }

    @Test
    void canGetObjectResponse() {
         Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    void canGetObjectResponseAsync() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    void canGetObjectResponseAsyncWithCallback() {
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
    void canPassAnObjectMapperAsPartOfARequest(){
        Unirest.config().setObjectMapper(null);

        TestingMapper mapper = new TestingMapper();
        Unirest.post(MockServer.POST)
                .queryString("foo", "bar")
                .withObjectMapper(mapper)
                .body(new Foo("Hello World"))
                .asObject(RequestCapture.class)
                .ifFailure(e -> {
                    UnirestParsingException ex = e.getParsingError().get();
                    ex.printStackTrace();
                    throw new RuntimeException(ex.getMessage());
                })
                .getBody()
                .assertParam("foo", "bar")
                .assertBody("{\"bar\":\"Hello World\"}");

        assertTrue(mapper.readWasCalled);
        assertTrue(mapper.writeWasCalled);
    }

    @Test
    void canOverrideBody() {
        Unirest.post(MockServer.POST)
                .body(new Foo("Apple"))
                .body(new Foo("Orange"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertBody("{\"bar\":\"Orange\"}");
    }

    @Test
    void setCharSetAfterBody() {
        Unirest.post(MockServer.POST)
                .body(new Foo("Orange"))
                .charset(StandardCharsets.US_ASCII)
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("text/plain; charset=US-ASCII");
    }

    @Test
    void setNoCharSetAfterBody() {
        Unirest.post(MockServer.POST)
                .body(new Foo("Orange"))
                .noCharset()
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("text/plain");
    }

    @Test
    void ifTheObjectMapperFailsReturnEmptyAndAddToParsingError() {
        HttpResponse<RequestCapture> request = Unirest.get(MockServer.INVALID_REQUEST)
                .asObject(RequestCapture.class);

        assertNull(request.getBody());
        assertTrue(request.getParsingError().isPresent());
        assertEquals("kong.unirest.UnirestException: com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'You': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n" +
                " at [Source: (String)\"You did something bad\"; line: 1, column: 4]", request.getParsingError().get().getMessage());
        assertEquals("You did something bad", request.getParsingError().get().getOriginalBody());

    }

    @Test
    void ifTheObjectMapperFailsReturnEmptyAndAddToParsingErrorObGenericTypes() {
        HttpResponse<RequestCapture> request = Unirest.get(MockServer.INVALID_REQUEST)
                .asObject(new GenericType<RequestCapture>() {});

        assertNull(request.getBody());
        assertTrue(request.getParsingError().isPresent());
        assertEquals("kong.unirest.UnirestException: com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'You': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n" +
                " at [Source: (String)\"You did something bad\"; line: 1, column: 4]", request.getParsingError().get().getMessage());
        assertEquals("You did something bad", request.getParsingError().get().getOriginalBody());
    }

    @Test
    void unirestExceptionsAreAlsoParseExceptions() {
        HttpResponse<RequestCapture> request = Unirest.get(MockServer.INVALID_REQUEST)
                .asObject(new GenericType<RequestCapture>() {});

        assertNull(request.getBody());
        assertTrue(request.getParsingError().isPresent());
        assertEquals("kong.unirest.UnirestException: com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'You': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n" +
                " at [Source: (String)\"You did something bad\"; line: 1, column: 4]", request.getParsingError().get().getMessage());
        assertEquals("You did something bad", request.getParsingError().get().getOriginalBody());
    }

    @Test
    void canSetObjectMapperToFailOnUnknown() {
        com.fasterxml.jackson.databind.ObjectMapper jack = new com.fasterxml.jackson.databind.ObjectMapper();
        jack.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        Unirest.config().setObjectMapper(new JacksonObjectMapper(jack));

        MockServer.setStringResponse("{\"foo\": [1,2,3] }");

        Error error = Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .mapError(Error.class);

        assertEquals(Arrays.asList(1,2,3), error.foo);
    }

    public static class Error {
        public List<Integer> foo;
    }

    public static class TestingMapper implements ObjectMapper {
        public boolean readWasCalled;
        public boolean writeWasCalled;

        @Override
        public <T> T readValue(String value, Class<T> valueType) {
            this.readWasCalled = true;
            return new JacksonObjectMapper().readValue(value, valueType);
        }

        @Override
        public String writeValue(Object value) {
            writeWasCalled = true;
            return new Gson().toJson(value);
        }
    }
}
