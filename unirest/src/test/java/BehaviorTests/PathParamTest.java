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
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.junit.Test;
import kong.unirest.TestUtil;

public class PathParamTest extends BddTest {

    @Test
    public void canAddRouteParamsAsMap() {
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
    public void properlyDealsWithPlusInPAth() {
        String param = "jack+4@email.com";

        Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", param)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/jack%2B4%40email.com/passed")
                .assertPathParam("params", param);
    }

    @Test
    public void testPathParameters() {
        Unirest.get(MockServer.HOST + "/{method}")
                .routeParam("method", "get")
                .queryString("name", "Mark")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark");
    }

    @Test
    public void testQueryAndBodyParameters() {
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
    public void testPathParameters2() {
        Unirest.patch(MockServer.HOST + "/{method}")
                .routeParam("method", "patch")
                .field("name", "Mark")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark");
    }

    @Test
    public void testMissingPathParameter() {
        TestUtil.assertException(() ->
                        Unirest.get(MockServer.HOST + "/{method}")
                        .routeParam("method222", "get")
                        .queryString("name", "Mark")
                        .asEmpty(),
                UnirestException.class,
                "Can't find route parameter name \"method222\"");
    }

    @Test
    public void testMissingPathParameterValue() {
        TestUtil.assertException(() ->
                        Unirest.get(MockServer.HOST + "/{method}")
                                .queryString("name", "Mark")
                                .asEmpty(),
                UnirestException.class,
                "java.lang.IllegalArgumentException: Illegal character in path at index 22: http://localhost:4567/{method}?name=Mark");
    }

    @Test
    public void illigalPathParams() {
        String value = "/?ЊЯЯ";

        Unirest.get(MockServer.PASSED_PATH_PARAM)
                .routeParam("params", value)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get/%2F%3F%D0%8A%D0%AF%D0%AF/passed")
                .assertPathParam("params", value);
    }
}
