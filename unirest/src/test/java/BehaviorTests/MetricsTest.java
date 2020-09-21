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

import kong.unirest.HttpRequestSummary;
import kong.unirest.Unirest;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.function.Function;

import static BehaviorTests.MockServer.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetricsTest extends BddTest {
    @Test
    public void canSetAMetricObjectToInstramentUnirest() {
        MyMetric metric = configureMetric();

        Unirest.get(GET).asEmpty();
        Unirest.get(GET).asEmpty();
        Unirest.get(PASSED_PATH_PARAM)
                .routeParam("params", "foo")
                .queryString("fruit", "orange")
                .asEmpty();

        assertEquals(2, metric.routes.get(GET).size());
        assertEquals(1, metric.routes.get("http://localhost:4567/get/foo/passed?fruit=orange").size());
    }

    @Test
    public void canGetParamedUrlInSummary() {
        MyMetric metric = configureMetric(HttpRequestSummary::getRawPath);

        Unirest.get(PASSED_PATH_PARAM)
                .routeParam("params", "foo")
                .queryString("fruit", "orange")
                .asEmpty();

        assertEquals(1, metric.routes.get("http://localhost:4567/get/{params}/passed").size());
    }

    @Test
    public void canGetToMethod() {
        MyMetric metric = configureMetric(s -> s.getHttpMethod().name());

        Unirest.get(GET).asEmpty();
        Unirest.get(GET).asEmpty();
        Unirest.post(POST).asEmpty();
        Unirest.delete(DELETE).asEmpty();

        assertEquals(2, metric.routes.get("GET").size());
        assertEquals(1, metric.routes.get("DELETE").size());
    }

    @Test
    public void amountOfTimeBetweenRequestAndResponseDoesNotIncludeDeserialization() {
        MyMetric metric = configureMetric();

        Unirest.get(GET).asObject(r -> {
            assertEquals(1, metric.routes.get(GET).size());
            return null;
        });
    }

    @Test
    public void canGetInformationOnStatus() {
        MyMetric metric = configureMetric(HttpRequestSummary::getUrl);

        Unirest.get(GET).asEmpty();
        Unirest.get(REDIRECT).asEmpty();
        Unirest.get(REDIRECT).asEmpty();
        Unirest.get(ERROR_RESPONSE).asEmpty();

        assertEquals(1L, metric.countResponses(200));
        assertEquals(2L, metric.countResponses(301));
        assertEquals(1L, metric.countResponses(400));
    }

    @Test
    public void metricsOnAsyncRequests() throws Exception {
        MyMetric metric = configureMetric();

        Unirest.get(GET).asEmptyAsync().get();

        assertEquals(1L, metric.countResponses(200));
    }

    @Test
    public void metricsOnAsyncMethodsWithCallbacks() {
        MyMetric metric = configureMetric();

        Unirest.get(GET).asEmptyAsync(r -> asyncSuccess());

        assertAsync();
        assertEquals(1L, metric.countResponses(200));
    }

    @Test
    public void errorHandling() throws Exception {
        HttpClient mock = mock(HttpClient.class);
        when(mock.execute(any(HttpRequestBase.class))).thenThrow(new RuntimeException("boo"));
        when(mock.execute(any(HttpHost.class), any(HttpRequestBase.class))).thenThrow(new RuntimeException("boo"));
        MyMetric metric = new MyMetric(HttpRequestSummary::getUrl);
        Unirest.config().reset().httpClient(mock).instrumentWith(metric);

        try {
            Unirest.get(GET).asEmpty();
        }catch (Exception e){

        }

        assertEquals("boo", metric.routes.get(GET).get(0).e.getMessage());
    }

    @Test @Disabled
    public void errorHandling_async() throws Exception {
        MyMetric metric = configureMetric();

        try {
            Unirest.get("http://localhost:0000").asEmptyAsync().get();
        }catch (Exception e){

        }

        assertEquals("Connection refused", metric.routes.get("http://localhost:0000").get(0).e.getMessage());
        assertEquals(1, metric.routes.get("http://localhost:0000").size());
    }

    long exTime;

    @Test
    public void metricAsALambda() {
        exTime = 0;
        Unirest.config().instrumentWith((s) -> {
            long startNanos = System.nanoTime();
            return (r,e) -> exTime = System.nanoTime() - startNanos;
        });

        Unirest.get(GET).asEmpty();

        assertThat(exTime, greaterThan(0L));
    }

    @Test
    public void showWhatSparkDoes() {
        HashMap map = Unirest.get(SPARKLE)
                .routeParam("spark", "joy")
                .queryString("food", "hamberders")
                .queryString("colour", "red")
                .asObject(HashMap.class)
                .getBody();

        assertEquals("localhost:4567", map.get("host()"));
        assertEquals("/sparkle/joy/yippy", map.get("uri()")); // this is different from what the Spark doc says.
        assertEquals("http://localhost:4567/sparkle/joy/yippy", map.get("url()"));
        assertEquals(null, map.get("contextPath()"));
        assertEquals("/sparkle/joy/yippy", map.get("pathInfo()"));
        assertEquals("food=hamberders&colour=red", map.get("queryString()"));
    }

    private MyMetric configureMetric() {
        return configureMetric(HttpRequestSummary::getUrl);
    }

    private MyMetric configureMetric(Function<HttpRequestSummary, String> keyFunction) {
        MyMetric metric = new MyMetric(keyFunction);
        Unirest.config().followRedirects(false).instrumentWith(metric);
        return metric;
    }
}
