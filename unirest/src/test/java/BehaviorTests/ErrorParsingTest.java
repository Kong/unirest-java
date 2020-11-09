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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ErrorParsingTest extends BddTest {
    private boolean errorCalled;

    @Test
    void parsingAnAlternativeErrorObject() {
        MockServer.setJsonAsResponse(new ErrorThing("boom!"));

        ErrorThing e = Unirest.get(MockServer.ERROR_RESPONSE)
                .asObject(RequestCapture.class)
                .mapError(ErrorThing.class);

        assertErrorThing(e);
    }

    @Test
    void mapTheErrorToAString() {
        MockServer.setJsonAsResponse(new ErrorThing("boom!"));

        String e = Unirest.get(MockServer.ERROR_RESPONSE)
                .asObject(RequestCapture.class)
                .mapError(String.class);

        assertEquals("{\"message\":\"boom!\"}", e);
    }

    @Test
    void parsingAnAlternativeErrorObject_StringBody() {
        MockServer.setJsonAsResponse(new ErrorThing("boom!"));

        ErrorThing e = Unirest.get(MockServer.ERROR_RESPONSE)
                .asString()
                .mapError(ErrorThing.class);

        assertErrorThing(e);
    }

    @Test
    void parsingAnAlternativeErrorObject_JsonBody() {
        MockServer.setJsonAsResponse(new ErrorThing("boom!"));

        ErrorThing e = Unirest.get(MockServer.ERROR_RESPONSE)
                .asJson()
                .mapError(ErrorThing.class);

        assertErrorThing(e);
    }

    @Test
    void ifNoErrorThenGetTheRegularBody() {
        ErrorThing error = Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .mapError(ErrorThing.class);

        assertNull(error);
    }

    @Test
    void failsIfErrorResponseCantBeMapped() {
        JacksonObjectMapper om = new JacksonObjectMapper();
        om.om.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, false);
        om.om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        MockServer.setJsonAsResponse(new ErrorThing("boom!"));

        HttpResponse<RequestCapture> request = Unirest.get(MockServer.ERROR_RESPONSE)
                .withObjectMapper(om)
                .asObject(RequestCapture.class);

        NotTheError error = request.mapError(NotTheError.class);

        assertNull(error.merp);
        assertEquals("{\"message\":\"boom!\"}", request.getParsingError().get().getOriginalBody());
    }

    @Test
    void mapTheErrorWithAFunction() {
        MockServer.setJsonAsResponse(new ErrorThing("boom!"));

        errorCalled = false;
        Unirest.get(MockServer.ERROR_RESPONSE)
                .asObject(RequestCapture.class)
                .ifFailure(ErrorThing.class, e -> {
                    assertEquals(400, e.getStatus());
                    assertErrorThing(e.getBody());
                    errorCalled = true;
                }).ifSuccess(e -> {throw new AssertionError("No");});

        assertTrue(errorCalled);
    }

    @Test
    void willKeepBodyAroundOnFailures_WhenJacksonIsPassive() {
        MockServer.setJsonAsResponse(new ErrorThing("boom!"));

        Unirest.get(MockServer.ERROR_RESPONSE)
                .asObject(RequestCapture.class)
                .mapError(ErrorThing.class)
                .assertError("boom!");

    }

    private void assertErrorThing(ErrorThing e) {
        assertEquals("boom!", e.getMessage());
    }

    public static class NotTheError {
        @JsonProperty("merp")
        public String merp;
    }
}
