package BehaviorTests;

import com.fasterxml.jackson.annotation.JsonProperty;
import kong.unirest.HttpEither;
import kong.unirest.Unirest;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class EitherTest extends BddTest {

    @Test
    public void parsingAnAlternativeErrorObject() {
        HttpEither<RequestCapture, ErrorThing> request = Unirest.get(MockServer.ERROR_RESPONSE)
                .asObject(RequestCapture.class, ErrorThing.class);

        assertEquals("boom!", request.getError().getMessage());
    }

    @Test
    public void failsIfErrorResponseCantBeMapped() {

        HttpEither<RequestCapture, NotTheError> request = Unirest.get(MockServer.ERROR_RESPONSE)
                .asObject(RequestCapture.class, NotTheError.class);

        assertEquals(null, request.getError());
        assertEquals(null, request.getBody());
        assertEquals("{\"message\":\"boom!\"}", request.getParsingError().get().getOriginalBody());
    }

    public static class NotTheError {
        @JsonProperty("merp")
        public String merp;
    }
}
