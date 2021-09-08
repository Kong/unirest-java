/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package BehaviorTests;

import kong.unirest.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RetryTest extends BddTest {



    @Test
    void whenSettingIsOff() {
        MockServer.retryTimes(100, 429, 1d);
        assertEquals(429, Unirest.get(MockServer.GET).asEmpty().getStatus());
        assertEquals(429, Unirest.get(MockServer.GET).asString().getStatus());
        assertEquals(429, Unirest.get(MockServer.GET).asBytes().getStatus());
        assertEquals(429, Unirest.get(MockServer.GET).asObject(RequestCapture.class).getStatus());
    }

    @Test
    void willRetryAfterSeconds_AsObject() {
        this.<RequestCapture>doWithRetry(r -> r.asObject(RequestCapture.class))
                .assertMethod(HttpMethod.GET);
    }

    @Test
    void willRetryAfterSeconds_AsObjectGenerics() {
        List<Foo> o = Arrays.asList(
                new Foo("foo"),
                new Foo("bar"),
                new Foo("baz")
        );
        MockServer.setJsonAsResponse(o);
        List<Foo> cap = (List<Foo>)this.<RequestCapture>doWithRetry(r -> r.asObject(new GenericType<List<Foo>>(){}));
        assertEquals(o, cap);
    }

    @Test
    void willRetryAfterSeconds_AsString() {
        MockServer.setStringResponse("Hi Mom");
        String cap = this.<String>doWithRetry(r -> r.asString());
        assertEquals("Hi Mom", cap);
    }

    @Test
    void willRetryAfterSeconds_AsJson() {
        MockServer.setStringResponse("{\"message\": \"Hi Mom\"}");
        JsonNode cap = this.<JsonNode>doWithRetry(r -> r.asJson());
        assertEquals("Hi Mom", cap.getObject().getString("message"));
    }

    @Test
    void willRetryAfterSeconds_AsBytes() {
        MockServer.setStringResponse("Hi Mom");
        byte[] cap = this.<byte[]>doWithRetry(r -> r.asBytes());
        assertEquals("Hi Mom", new String(cap));
    }

    @Test
    void willRetryAfterSeconds_Empty() {
        doWithRetry(r -> r.asEmpty());
    }

    @Test
    void willRetryAfterSeconds_AsFile() {
        Path path = Paths.get("results.json");
        clearFile(path);

        MockServer.setStringResponse("Hi Mom");
        File cap = this.<File>doWithRetry(r -> r.asFile(path.toString(), StandardCopyOption.REPLACE_EXISTING));
        assertTrue(cap.exists());

        clearFile(path);
    }

    private void clearFile(Path path) {
        try {
            Files.delete(path);
        } catch (Exception ignored) { }
    }

    private <R> R doWithRetry(Function<HttpRequest, HttpResponse<R>> bodyExtractor) {
        MockServer.retryTimes(1, 429, 0.01);

        Unirest.config().automaticRetryAfter(true);

        HttpRequest request = Unirest.get(MockServer.GET);

        HttpResponse<R> response = bodyExtractor.apply(request);
        assertEquals(200, response.getStatus());
        MockServer.assertRequestCount(2);

        return response.getBody();
    }
}
