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

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HostsHeaderTest extends BddTest {

    /**
     * #
     * # Allow restricted HTTP request headers
     * #
     * # By default, the following request headers are not allowed to be set by user code
     * # in HttpRequests: "connection", "content-length", "expect", "host" and "upgrade".
     * # The 'jdk.httpclient.allowRestrictedHeaders' property allows one or more of these
     * # headers to be specified as a comma separated list to override the default restriction.
     * # The names are case-insensitive and white-space is ignored (removed before processing
     * # the list). Note, this capability is mostly intended for testing and isn't expected
     * # to be used in real deployments. Protocol errors or other undefined behavior is likely
     * # to occur when using them. The property is not set by default.
     * # Note also, that there may be other headers that are restricted from being set
     * # depending on the context. This includes the "Authorization" header when the
     * # relevant HttpClient has an authenticator set. These restrictions cannot be
     * # overridden by this property.
     * #
     * # jdk.httpclient.allowRestrictedHeaders=host
     * #
     */
    @Test @Disabled
    void willHonorHostsHeaders() {
        Unirest.get(MockServer.ALTGET)
                .header("Host", "localhost")
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost/get");
    }

    @Test
    void cannotNormallyOverrideHost() {
        Exception ex = assertThrows(UnirestException.class,
                () -> Unirest.get(MockServer.ALTGET)
                        .header("Host", "localhost").asEmpty());
        assertEquals("restricted header name: \"Host\"", ex.getCause().getMessage());
    }

    @Test @Disabled
    void canBuildCustomHost() {
        HttpResponse<String> s = Unirest.get("https://104.154.89.105/")
                .header("Host", "sha512.badssl.com")
                .asString();

        assertEquals(200, s.getStatus());
    }
}
