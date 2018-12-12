package BehaviorTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.Test;
import unirest.JacksonObjectMapper;
import unirest.Unirest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AsFileTests extends BddTest {

    private Path test = Paths.get("results.json");
    private JacksonObjectMapper om = new JacksonObjectMapper();

    @Override
    public void tearDown() {
        try {
            Files.delete(test);
        } catch (IOException e) {

        }
    }

    @Test
    public void canSaveContentsIntoFile() {
        Unirest.get(MockServer.GET)
                .asFile(test.toString());

        om.readValue(test.toFile(), RequestCapture.class)
                .assertStatus(200);
    }
}
