package BehaviorTests;

import io.github.openunirest.http.Unirest;
import org.junit.Test;

public class BasicAuthTest extends BddTest {
    @Test
    public void canSendDifferentBasicAuths() {
        Unirest.get(MockServer.GET)
                .basicAuth("george","guitar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("george","guitar");

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoHeader("Authorization");

        Unirest.get(MockServer.GET)
                .basicAuth("ringo","drums")
                .asObject(RequestCapture.class)
                .getBody()
                .assertBasicAuth("ringo","drums");
    }
}
