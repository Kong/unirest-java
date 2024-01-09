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

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

public class Http2Test extends BddTest  {

    @Test @Disabled
    void canMakeHttp2Requests() {
        Unirest.config().version(HttpClient.Version.HTTP_2);

        HttpResponse<JsonNode> httpResponse = Unirest.get("https://nghttp2.org/httpbin/get")
                .accept("application/json")
                //.header("wu","tang")
                .asJson();

        System.out.println("httpResponse = " + httpResponse);
    }

    @Test
    void sendsHeadersForHttp2() {
        Unirest.config().version(HttpClient.Version.HTTP_2);

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Connection", "Upgrade, HTTP2-Settings")
                .assertHeader("Upgrade", "h2c")
                .assertHeader("HTTP2-Settings", "AAEAAEAAAAIAAAABAAMAAABkAAQBAAAAAAUAAEAA");
    }

    @Test
    void dontSendHttp2HeadersForHttp1() {
        Unirest.config().version(HttpClient.Version.HTTP_1_1);

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoHeader("Connection")
                .assertNoHeader("Upgrade")
                .assertNoHeader("HTTP2-Settings");
    }

    @Test
    void overrideConfigPerRequest() {
        Unirest.config().version(HttpClient.Version.HTTP_1_1);

        Unirest.get(MockServer.GET)
                .version(HttpClient.Version.HTTP_2)
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Connection", "Upgrade, HTTP2-Settings")
                .assertHeader("Upgrade", "h2c")
                .assertHeader("HTTP2-Settings", "AAEAAEAAAAIAAAABAAMAAABkAAQBAAAAAAUAAEAA");
    }

    @Test
    void overrideConfigPerRequest2() {
        Unirest.config().version(HttpClient.Version.HTTP_2);

        Unirest.get(MockServer.GET)
                .version(HttpClient.Version.HTTP_1_1)
                .asObject(RequestCapture.class)
                .getBody()
                .assertNoHeader("Connection")
                .assertNoHeader("Upgrade")
                .assertNoHeader("HTTP2-Settings");
    }
}
