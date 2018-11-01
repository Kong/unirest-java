package BehaviorTests;

import unirest.HttpMethod;
import unirest.HttpResponse;
import unirest.Unirest;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class VerbTest extends BddTest {
    @Test
    public void get() {
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.GET);
    }

    @Test
    public void post() {
        Unirest.post(MockServer.POST)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.POST);
    }

    @Test
    public void put() {
        Unirest.put(MockServer.POST)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.PUT);
    }

    @Test
    public void patch() {
        Unirest.patch(MockServer.PATCH)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.PATCH);
    }

    @Test
    public void head() {
        HttpResponse<InputStream> response = Unirest.head(MockServer.GET).asBinary();

        assertEquals(200, response.getStatus());
        assertEquals("text/html;charset=utf-8", response.getHeaders().getFirst("Content-Type"));
    }

    @Test
    public void option() {
        Unirest.options(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.OPTIONS);
    }
}
