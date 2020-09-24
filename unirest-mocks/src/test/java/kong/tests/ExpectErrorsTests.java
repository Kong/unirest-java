package kong.tests;

import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import org.junit.jupiter.api.Test;

import static kong.unirest.HttpStatus.BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpectErrorsTests extends Base {
    @Test
    void canExpectErrors() {
        client.expect(HttpMethod.GET, path).thenReturn().withStatus(BAD_REQUEST, "oh noes");

        HttpResponse<String> response = uni.get(path).asString();

        assertEquals(BAD_REQUEST, response.getStatus());
        assertEquals("oh noes", response.getStatusText());
    }
}
