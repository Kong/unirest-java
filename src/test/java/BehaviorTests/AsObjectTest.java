package BehaviorTests;

import io.github.openunirest.http.Unirest;
import org.junit.Test;

public class AsObjectTest extends BddTest {
    @Test
    public void canGetObjectResponse() {
         Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    public void canGetObjectResponseAsync() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    public void canGetObjectResponseAsyncWithCallback() {
        Unirest.get(MockServer.GET)
                .queryString("foo", "bar")
                .asObjectAsync(RequestCapture.class, r -> {
                    RequestCapture cap = r.getBody();
                    cap.assertParam("foo", "bar");
                    asyncSuccess();
                });

        assertAsync();
    }
}
