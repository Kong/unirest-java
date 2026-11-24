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
package kong.unirest.core;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseEncodingCopyTest {

    @Test
    void responseEncodingSurvivesAddingABody() {
        assertEncoding(r -> r.responseEncoding("windows-1250").body("hello"));
    }

    @Test
    void responseEncodingSurvivesAddingAField() {
        assertEncoding(r -> r.responseEncoding("windows-1250").field("a", "b"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void assertEncoding(Function<HttpRequestWithBody, HttpRequest> build) {
        var seen = new AtomicReference<String>();
        var raw = (RawResponse) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{RawResponse.class}, (p, m, a) -> {
                    if (m.getName().equals("getContentAsString") && a != null && a.length == 1) {
                        seen.set((String) a[0]);
                        return "";
                    }
                    if (m.getName().equals("getHeaders")) {
                        return new Headers();
                    }
                    if (m.getReturnType() == int.class) {
                        return 200;
                    }
                    if (m.getReturnType() == boolean.class) {
                        return false;
                    }
                    return null;
                });
        var client = (Client) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{Client.class}, (p, m, a) -> {
                    if (m.getName().equals("request")) {
                        return ((Function) a[1]).apply(raw);
                    }
                    return null;
                });
        var config = new Config();
        config.httpClient(client);

        build.apply(new HttpRequestBody(config, HttpMethod.POST, "http://localhost/")).asString();

        assertEquals("windows-1250", seen.get());
    }
}
