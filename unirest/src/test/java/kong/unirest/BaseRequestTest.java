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

package kong.unirest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static kong.unirest.HttpMethod.GET;
import static org.junit.jupiter.api.Assertions.*;

class BaseRequestTest {

    private Config testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new Config();
    }

    @AfterEach
    void tearDown() throws Exception {
        Util.resetClock();
    }

    @Test
    void connectTimeoutCanOverrrideConfig() {
        testConfig.connectTimeout(42);

        HttpRequest request = new TestRequest(testConfig);

        assertEquals(42, request.getConnectTimeout());
        request.connectTimeout(111);
        assertEquals(111, request.getConnectTimeout());
    }

    @Test
    void requestEquals_PathAndVerb() {
        assertEquals(
                new TestRequest(GET, "/path"),
                new TestRequest(GET, "/path")
        );
    }

    @Test
    void requestEquals_PathAndVerb_differentVerb() {
        assertNotEquals(
                new TestRequest(GET, "/path"),
                new TestRequest(HttpMethod.HEAD, "/path")
        );
    }

    @Test
    void requestEquals_PathAndVerb_differentPath() {
        assertNotEquals(
                new TestRequest(GET, "/path"),
                new TestRequest(GET, "/derp")
        );
    }

    @Test
    void reqeustEquals_Headers() {
        assertEquals(
                new TestRequest(of("Accept", "json")),
                new TestRequest(of("Accept", "json"))
        );
    }

    @Test
    void reqeustEquals_Headers_differentValues() {
        assertNotEquals(
                new TestRequest(of("Accept", "json")),
                new TestRequest(of("Accept", "xml"))
        );
    }

    @Test
    void reqeustEquals_Headers_additionalValues() {
        assertNotEquals(
                new TestRequest(of("Accept", "json")),
                new TestRequest(of("Accept", "json", "x-header", "cheese"))
        );
    }

    @Test
    void canGetTimeOfRequest() {
        TestRequest request = new TestRequest();

        assertTrue(ChronoUnit.MILLIS.between(request.getCreationTime(), Instant.now()) < 10);
    }

    @Test
    void canFreezeTimeForTests() {
        Instant i = Instant.now();
        Util.freezeClock(i);
        TestRequest r1 = new TestRequest();
        TestRequest r2 = new TestRequest();

        assertEquals(r1.getCreationTime(), r2.getCreationTime());

        Util.freezeClock(i.plus(50, ChronoUnit.MINUTES));

        TestRequest r3 = new TestRequest();

        assertEquals(50L, ChronoUnit.MINUTES.between(r1.getCreationTime(), r3.getCreationTime()));
    }

    private class TestRequest extends BaseRequest<TestRequest> {
        TestRequest(){
            super(testConfig, GET, "/");
        }

        TestRequest(BaseRequest httpRequest) {
            super(httpRequest);
        }

        TestRequest(Config config) {
            super(config, GET, "");
        }

        TestRequest(HttpMethod method, String url){
            super(testConfig, method, url);
        }

        TestRequest(Map<String, String> headers){
            super(testConfig, GET, "/");
            headers.forEach(this::header);
        }
    }
}