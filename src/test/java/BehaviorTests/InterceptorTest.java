package BehaviorTests;

import io.github.openunirest.http.Unirest;
import io.github.openunirest.http.options.Options;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class InterceptorTest extends BddTest {
    @Test
    public void canAddInterceptor() {
        Options.addInterceptor(new TestInterceptor());

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("x-custom", "foo");
    }

    @Test
    public void canAddInterceptorToAsync() throws ExecutionException, InterruptedException {
        Options.addInterceptor(new TestInterceptor());

        Unirest.get(MockServer.GET)
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertHeader("x-custom", "foo");
    }

    private class TestInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
            httpRequest.addHeader("x-custom", "foo");
        }
    }
}
