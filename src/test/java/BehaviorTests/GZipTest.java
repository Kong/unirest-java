package BehaviorTests;

import io.github.openunirest.http.Unirest;
import org.json.JSONException;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class GZipTest extends BddTest {
    @Test
    public void testGzip() throws JSONException {
        Unirest.get(MockServer.GZIP)
                .queryString("zipme", "up")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("zipme", "up");
    }

    @Test
    public void testGzipAsync() throws JSONException, InterruptedException, ExecutionException {
        Unirest.get(MockServer.GZIP)
                .queryString("zipme", "up")
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertParam("zipme", "up");
    }
}
