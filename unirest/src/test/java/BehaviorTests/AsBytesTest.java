package BehaviorTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.junit.Test;

import java.io.IOException;

public class AsBytesTest extends BddTest {
    JacksonObjectMapper om = new JacksonObjectMapper();

    @Test
    public void getGetResultAsBytes() {
        byte[] content = Unirest.get(MockServer.GET)
                                .asBytes()
                                .getBody();

        RequestCapture cap = om.readValue(content, RequestCapture.class);

        cap.assertStatus(200);
    }
}
