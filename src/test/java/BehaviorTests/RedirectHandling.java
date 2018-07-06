package BehaviorTests;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.Unirest;
import io.github.openunirest.http.options.Options;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RedirectHandling extends BddTest {

    @Test
    public void willFollowRedirectsByDefault() {
        Unirest.get(MockServer.REDIRECT)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get");
    }

    @Test
    public void canDisableRedirects(){
        Options.followRedirects(false);
        HttpResponse response = Unirest.get(MockServer.REDIRECT).asBinary();

        assertEquals(301, response.getStatus());
    }
}
