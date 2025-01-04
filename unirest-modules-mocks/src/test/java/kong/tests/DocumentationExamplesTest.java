package kong.tests;

import kong.unirest.core.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentationExamplesTest {
    @AfterEach
    public void tearDown(){
        MockClient.clear();
    }

    @Test
    void mockStatic(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("You can do anything!");

        assertEquals(
                "You can do anything!",
                Unirest.get("http://zombo.com").asString().getBody()
        );

        //Optional: Verify all expectations were fulfilled
        mock.verifyAll();
    }

    @Test
    void mockInstant(){
        UnirestInstance unirest = Unirest.spawnInstance();
        MockClient mock = MockClient.register(unirest);

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("You can do anything!");

        assertEquals(
                "You can do anything!",
                unirest.get("http://zombo.com").asString().getBody()
        );

        //Optional: Verify all expectations were fulfilled
        mock.verifyAll();
    }

    @Test
    void multipleExpects(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "https://somewhere.bad")
                .thenReturn("I'm Bad");

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .thenReturn("You can do anything!");

        mock.expect(HttpMethod.GET, "http://zombo.com")
                .header("foo", "bar")
                .thenReturn("You can do anything with headers!");

        assertEquals(
                "You can do anything with headers!",
                Unirest.get("http://zombo.com")
                        .header("foo", "bar")
                        .asString().getBody()
        );

        assertEquals(
                "You can do anything!",
                Unirest.get("http://zombo.com")
                        .asString().getBody()
        );
    }

    @Test
    void validate(){
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.POST, "http://zombo.com")
                .thenReturn().withStatus(200);

        Unirest.post("http://zombo.com").asString().getBody();

        mock.verifyAll();
    }

    @Test
    void multipleValidates(){
        MockClient mock = MockClient.register();

        var zombo =    mock.expect(HttpMethod.POST, "http://zombo.com").thenReturn();
        var homestar = mock.expect(HttpMethod.DELETE, "http://homestarrunner.com").thenReturn();

        Unirest.post("http://zombo.com").asString().getBody();

        zombo.verify();
        homestar.verify(Times.never());
    }
}
