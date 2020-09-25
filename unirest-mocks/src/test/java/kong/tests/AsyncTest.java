package kong.tests;

import kong.unirest.HttpMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsyncTest extends Base {
    @Test
    void canExpectAsync() throws Exception {
        client.expect(HttpMethod.GET, path).thenReturn("Hey Ma");

        String body = uni.get(path).asStringAsync().get().getBody();

        assertEquals("Hey Ma", body);

        client.verifyAll();
    }
}
