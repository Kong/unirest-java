package BehaviorTests;

import io.github.openunirest.http.Unirest;
import io.github.openunirest.http.exceptions.UnirestException;
import org.junit.Test;
import util.TestUtil;

public class PathParamTest extends BddTest {

    @Test
    public void testPathParameters() {
        Unirest.get(MockServer.HOST + "/{method}")
                .routeParam("method", "get")
                .queryString("name", "Mark")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark");
    }

    @Test
    public void testQueryAndBodyParameters() {
        Unirest.post(MockServer.HOST + "/{method}")
                .routeParam("method", "post")
                .queryString("name", "Mark")
                .field("wot", "wat")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .assertParam("wot", "wat");
    }

    @Test
    public void testPathParameters2() {
        Unirest.patch(MockServer.HOST + "/{method}")
                .routeParam("method", "patch")
                .field("name", "Mark")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark");
    }

    @Test
    public void testMissingPathParameter() {
        TestUtil.assertException(() ->
                        Unirest.get(MockServer.HOST + "/{method}")
                        .routeParam("method222", "get")
                        .queryString("name", "Mark")
                        .asBinary(),
                RuntimeException.class,
                "Can't find route parameter name \"method222\"");
    }

    @Test
    public void testMissingPathParameterValue() {
        TestUtil.assertException(() ->
                        Unirest.get(MockServer.HOST + "/{method}")
                                .queryString("name", "Mark")
                                .asBinary(),
                UnirestException.class,
                "java.net.URISyntaxException: Illegal character in path at index 22: http://localhost:4567/{method}?name=Mark");
    }

    @Test
    public void illigalPathParams() {
        String value = "/?ЊЯЯ";

        Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("param", value)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/%2F%3F%D0%8A%D0%AF%D0%AF/passed")
                .assertPathParam(value);
    }
}
