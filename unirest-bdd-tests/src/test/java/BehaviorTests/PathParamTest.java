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

import com.google.common.collect.ImmutableMap;
import kong.unirest.HttpMethod;
import kong.unirest.HttpRequestSummary;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathParamTest extends BddTest {

    @Test
    void canAddRouteParamsAsMap() {
        String param = "Hamberders";

        Unirest.get(MockServer.PASSED_PATH_PARAM_MULTI)
                .routeParam(ImmutableMap.of("params", param, "another", 42))
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/Hamberders/passed/42")
                .assertPathParam("params", param)
                .assertPathParam("another", "42");
    }

    @Test
    void specialCharactersInTheDirectoryPath() {
        Unirest.get(MockServer.GET + "/" + URLEncoder.encode("[brackets]") + "/passed")
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/%5Bbrackets%5D/passed");

        Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", "[brackets]")
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/%5Bbrackets%5D/passed");
    }

    @Test
    void properlyDealsWithPlusInPAth() {
        String param = "jack+4@email.com";

        Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", param)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/jack%2B4%40email.com/passed")
                .assertPathParam("params", param);
    }

    @Test
    void testPathParameters() {
        Unirest.get(MockServer.HOST + "/{method}")
                .routeParam("method", "get")
                .queryString("name", "Mark")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark");
    }

    @Test
    void testQueryAndBodyParameters() {
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
    void testPathParameters2() {
        Unirest.patch(MockServer.HOST + "/{method}")
                .routeParam("method", "patch")
                .field("name", "Mark")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark");
    }

    @Test
    void testMissingPathParameter() {
        UnirestException ex = assertThrows(UnirestException.class, () ->
                        Unirest.get(MockServer.HOST + "/{method}")
                        .routeParam("method222", "get")
                        .queryString("name", "Mark")
                        .asEmpty());
        Assertions.assertEquals("Can't find route parameter name \"method222\"", ex.getMessage());
    }

    @Test
    void testMissingPathParameterValue() {
        UnirestException ex = assertThrows(UnirestException.class, () ->
                        Unirest.get(MockServer.HOST + "/{method}")
                                .queryString("name", "Mark")
                                .asEmpty());
        Assertions.assertEquals("java.lang.IllegalArgumentException: Illegal character in path at index 22: http://localhost:4567/{method}?name=Mark", ex.getMessage());
    }

    @Test
    void illigalPathParams() {
        String value = "/?ЊЯЯ";

        Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", value)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/%2F%3F%D0%8A%D0%AF%D0%AF/passed")
                .assertPathParam("params", value);
    }

    @Test
    void spacesAndPluses() {
        String value = "Hunky Dory+Cheese Wiz";

        Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", value)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/Hunky%20Dory%2BCheese%20Wiz/passed")
                .assertPathParam("params", value);
    }

    @Test
    void nulls() {
        Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", null)
                .asObject(RequestCapture.class)
                .getBody()
                .assertStatus(404)
                .assertUrl("http://localhost:4567/get//passed");
    }

    @Test
    void getRequestSummaryOnResponse() {
        HttpRequestSummary sum = Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", "cheese")
                .queryString("fruit", "apples")
                .asEmpty()
                .getRequestSummary();

        assertEquals("http://localhost:4567/get/cheese/passed?fruit=apples", sum.getUrl());
        assertEquals("http://localhost:4567/get/{params}/passed", sum.getRawPath());
        assertEquals(HttpMethod.GET, sum.getHttpMethod());
    }

    @Test
    void getRequestSummaryOnResponse_async() throws Exception {
        HttpRequestSummary sum = Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", "cheese")
                .queryString("fruit", "apples")
                .asEmptyAsync()
                .get()
                .getRequestSummary();

        assertEquals("http://localhost:4567/get/cheese/passed?fruit=apples", sum.getUrl());
        assertEquals("http://localhost:4567/get/{params}/passed", sum.getRawPath());
        assertEquals(HttpMethod.GET, sum.getHttpMethod());
    }
}
