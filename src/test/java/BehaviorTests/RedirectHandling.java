package BehaviorTests;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.Unirest;
import io.github.openunirest.http.options.Options;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

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

    @Test
    public void willFollowRedirectsByDefaultAsync() throws ExecutionException, InterruptedException {
        Unirest.get(MockServer.REDIRECT)
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertUrl("http://localhost:4567/get");
    }

    @Test
    public void canDisableRedirectsAsync() throws ExecutionException, InterruptedException {
        Options.followRedirects(false);
        HttpResponse response = Unirest.get(MockServer.REDIRECT).asBinaryAsync().get();

        assertEquals(301, response.getStatus());
    }
}
