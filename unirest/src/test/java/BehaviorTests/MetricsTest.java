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
import org.junit.Test;

import static BehaviorTests.MockServer.*;
import static org.junit.Assert.assertEquals;

public class MetricsTest extends BddTest {
    @Test
    public void canSetAMetricObjectToInstramentUnirest() {
        MyMetric metric = new MyMetric(HttpRequestSummary::getUrl);
        Unirest.config().instramentWith(metric);

        Unirest.get(GET).asEmpty();
        Unirest.get(GET).asEmpty();
        Unirest.get(PASSED_PATH_PARAM)
                .routeParam("params","foo")
                .queryString("fruit","orange")
                .asEmpty();

        assertEquals(2, metric.routes.get(GET).size());
        assertEquals(1, metric.routes.get("http://localhost:4567/get/foo/passed?fruit=orange").size());
    }

    @Test
    public void canGetParamedUrlInSummary() {
        MyMetric metric = new MyMetric(HttpRequestSummary::getRawPath);
        Unirest.config().instramentWith(metric);

        Unirest.get(PASSED_PATH_PARAM)
                .routeParam("params","foo")
                .queryString("fruit","orange")
                .asEmpty();

        assertEquals(1, metric.routes.get("http://localhost:4567/get/{params}/passed").size());
    }

    @Test
    public void canGetToMethod() {
        MyMetric metric = new MyMetric(s -> s.getHttpMethod().name());
        Unirest.config().instramentWith(metric);

        Unirest.get(GET).asEmpty();
        Unirest.get(GET).asEmpty();
        Unirest.post(POST).asEmpty();
        Unirest.delete(DELETE).asEmpty();

        assertEquals(2, metric.routes.get("GET").size());
        assertEquals(1, metric.routes.get("DELETE").size());
    }

    @Test
    public void amountOfTimeBetweenRequestAndResponseDoesNotIncludeDeserialization() {
        MyMetric metric = new MyMetric(HttpRequestSummary::getUrl);
        Unirest.config().instramentWith(metric);

        Unirest.get(GET).asObject(r -> {
            assertEquals(1, metric.routes.get(GET).size());
            return null;
        });
    }

    @Test
    public void canGetInformationOnStatus() {
        MyMetric metric = new MyMetric(HttpRequestSummary::getUrl);
        Unirest.config().followRedirects(false).instramentWith(metric);

        Unirest.get(GET).asEmpty();
        Unirest.get(REDIRECT).asEmpty();
        Unirest.get(REDIRECT).asEmpty();
        Unirest.get(ERROR_RESPONSE).asEmpty();

        assertEquals(1L, metric.countResponses(200));
        assertEquals(2L, metric.countResponses(301));
        assertEquals(1L, metric.countResponses(400));
    }
}
