package io.github.openunirest.http;

import BehaviorTests.RequestCapture;
import io.github.openunirest.request.JsonPatch;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class JacksonObjectMapperTest {

    private JacksonObjectMapper om = new JacksonObjectMapper();

    @Test
    public void jsonPatch() {
        JsonPatch patch = new JsonPatch();
        patch.add("/foo", "bar");
        patch.add("/baz", "qux");

        String expectStr = patch.toString();
        String actualStr = om.writeValue(patch);

        JSONAssert.assertEquals(expectStr,
                actualStr,
                true);
    }

    @Test
    public void jsonPatchInRequestCapture() {
        JsonPatch patch = new JsonPatch();
        patch.add("/foo", "bar");
        patch.add("/baz", "qux");

        RequestCapture rc = new RequestCapture();
        rc.setPatch(patch);

        String actualStr = om.writeValue(rc);
        System.out.println("actualStr = " + actualStr);
        JSONAssert.assertEquals("{}",
                actualStr,
                false);
    }
}