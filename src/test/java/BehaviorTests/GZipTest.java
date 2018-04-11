package BehaviorTests;

import io.github.openunirest.http.Unirest;
import org.junit.Test;

public class GZipTest extends BddTest {
    @Test
    public void testGzip() {
        Unirest.get(MockServer.GZIP)
                .queryString("zipme", "up")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("zipme", "up");
    }

    @Test
    public void testGzipAsync() throws Exception {
        Unirest.get(MockServer.GZIP)
                .queryString("zipme", "up")
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertParam("zipme", "up");
    }
}
