package BehaviorTests;

import io.github.openunirest.http.Unirest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DefectTest extends BddTest {

    @Test
    public void hashOnLinksDoNotMessUpUri() {
        Unirest.get(MockServer.GET + "?a=1&b=2#some_location")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("a", "1")
                .assertParam("b", "2");
    }

    @Test
    public void nullAndObjectValuesInMap() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("foo", null);
        queryParams.put("baz", "qux");

        Unirest.get(MockServer.GET)
                .queryString(queryParams)
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "")
                .assertParam("baz", "qux")
                .assertQueryString("foo&baz=qux");
    }
}
