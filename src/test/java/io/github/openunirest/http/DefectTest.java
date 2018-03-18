package io.github.openunirest.http;

import org.junit.Test;

public class DefectTest extends BddTest {

    @Test
    public void hashOnLinksDoNotMessUpUri() {
        Unirest.get(MockServer.GET + "?a=1&b=2#some_location")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("a","1")
                .assertParam("b", "2");
    }
}
