package BehaviorTests;

import io.github.openunirest.http.HttpResponse;
import io.github.openunirest.http.JsonNode;
import io.github.openunirest.http.Unirest;
import org.junit.Test;

import java.util.Arrays;

public class QueryStringTest extends BddTest {
    @Test
    public void testGetQueryStrings() {
       Unirest.get(MockServer.GET)
                .queryString("name", "mark")
                .queryString("nick", "thefosk")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "mark")
                .assertParam("nick", "thefosk");
    }

    @Test
    public void canPassQueryParamsDirectlyOnUriOrWithMethod() {
        Unirest.get(MockServer.GET + "?name=mark")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "mark");

        Unirest.get(MockServer.GET)
                .queryString("name", "mark2")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "mark2");
    }

    @Test
    public void multipleParams() {
        Unirest.get(MockServer.GET + "?name=ringo")
                .queryString("name", "paul")
                .queryString("name", "john")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "ringo")
                .assertParam("name", "paul")
                .assertParam("name", "john");
    }

    @Test
    public void testGetUTF8() {
        Unirest.get(MockServer.GET)
                .queryString("param3", "こんにちは")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("param3", "こんにちは");
    }

    @Test
    public void testGetMultiple() {
        for (int i = 1; i <= 20; i++) {
            HttpResponse<JsonNode> response = Unirest.get(MockServer.GET + "?try=" + i).asJson();
            parse(response).assertParam("try", String.valueOf(i));
        }
    }

    @Test
    public void testQueryStringEncoding() {
        String testKey = "email2=someKey&email";
        String testValue = "hello@hello.com";

        Unirest.get(MockServer.GET)
                .queryString(testKey, testValue)
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam(testKey, testValue);
    }

    @Test
    public void testGetQuerystringArray() {
        Unirest.get(MockServer.GET)
                .queryString("name", "Mark")
                .queryString("name", "Tom")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testGetArray() {
        Unirest.get(MockServer.GET)
                .queryString("name", Arrays.asList("Mark", "Tom"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }
}
