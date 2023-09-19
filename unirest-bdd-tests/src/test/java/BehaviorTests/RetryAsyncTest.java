package BehaviorTests;

import kong.unirest.core.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RetryAsyncTest extends BddTest {

    @Test
    void whenSettingIsOff() {
        MockServer.retryTimes(100, 429, 1d);
        assertEquals(429, Unirest.get(MockServer.GET).asEmpty().getStatus());
        assertEquals(429, Unirest.get(MockServer.GET).asString().getStatus());
        assertEquals(429, Unirest.get(MockServer.GET).asBytes().getStatus());
        assertEquals(429, Unirest.get(MockServer.GET).asObject(RequestCapture.class).getStatus());
    }

    @Test
    void dontRetry_whenHeaderIsMissing(){
        MockServer.retryTimes(100, 429, (String)null);
        assertDidNotRetry();
    }

    @Test
    void dontRetry_whenHeaderIsUnparseable(){
        MockServer.retryTimes(100, 429, "David S Pumpkins");
        assertDidNotRetry();
    }

    @Test
    void dontRetry_whenHeaderIsNegative(){
        MockServer.retryTimes(100, 429, -5.5);
        assertDidNotRetry();
    }

    @Test
    void dontRetry_whenHeaderIsEmpty(){
        MockServer.retryTimes(100, 429, "");
        assertDidNotRetry();
    }

    @Test
    void willRetryAfterSeconds_AsObject() {
        this.<RequestCapture>doWithRetry(r -> r.asObjectAsync(RequestCapture.class))
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
        List<Foo> cap = (List<Foo>)this.<RequestCapture>doWithRetry(r -> r.asObjectAsync(new GenericType<List<Foo>>(){}));
        assertEquals(o, cap);
    }

    @Test
    void willRetryAfterSeconds_AsString() {
        MockServer.setStringResponse("Hi Mom");
        String cap = this.<String>doWithRetry(r -> r.asStringAsync());
        assertEquals("Hi Mom", cap);
    }

    @Test
    void willRetryAfterSeconds_AsJson() {
        MockServer.setStringResponse("{\"message\": \"Hi Mom\"}");
        JsonNode cap = this.<JsonNode>doWithRetry(r -> r.asJsonAsync());
        assertEquals("Hi Mom", cap.getObject().getString("message"));
    }

    @Test
    void willRetryAfterSeconds_AsBytes() {
        MockServer.setStringResponse("Hi Mom");
        byte[] cap = this.<byte[]>doWithRetry(r -> r.asBytesAsync());
        assertEquals("Hi Mom", new String(cap));
    }

    @Test
    void willRetryAfterSeconds_Empty() {
        doWithRetry(r -> r.asEmptyAsync());
    }

    @Test
    void willRetryAfterSeconds_AsFile() {
        Path path = Paths.get("results.json");
        clearFile(path);

        MockServer.setStringResponse("Hi Mom");
        File cap = (File)this.doWithRetry(r -> r.asFileAsync(path.toString(), StandardCopyOption.REPLACE_EXISTING));
        assertTrue(cap.exists());

        clearFile(path);
    }

    @Test
    void willReturn429OnceItExceedsMaxAttempts() {
        MockServer.retryTimes(10, 429, .01);
        Unirest.config().retryAfter(true, 3);

        HttpResponse resp = Unirest.get(MockServer.GET).asEmpty();
        assertEquals(429, resp.getStatus());
        MockServer.assertRequestCount(3);
    }

    private void clearFile(Path path) {
        try {
            Files.delete(path);
        } catch (Exception ignored) { }
    }

    private void assertDidNotRetry() {
        Unirest.config().retryAfter(true);
        assertEquals(429, Unirest.get(MockServer.GET).asEmpty().getStatus());
        MockServer.assertRequestCount(1);
    }

    private <R> R doWithRetry(Function<HttpRequest, CompletableFuture<HttpResponse<R>>> bodyExtractor) {
       try {
           MockServer.retryTimes(1, 429, 0.01);

           Unirest.config().retryAfter(true);

           HttpRequest request = Unirest.get(MockServer.GET);

           HttpResponse<R> response = bodyExtractor.apply(request).get();
           assertEquals(200, response.getStatus());
           MockServer.assertRequestCount(2);

           return response.getBody();
       }catch (Exception e){
           throw new AssertionError(e);
       }
    }
}
