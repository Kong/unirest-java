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

package kong.tests;

import kong.unirest.FieldMatcher;
import kong.unirest.HttpMethod;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static kong.unirest.FieldMatcher.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BodyMatchingTest extends Base {

    @Test
    void canTestForForms() {
        client.expect(HttpMethod.POST, path)
                .body(of("beetle", "ringo", "number", "42"))
                .thenReturn()
                .withStatus(200);

        Unirest.post(path)
                .field("beetle", "ringo")
                .field("number", "42")
                .asEmpty();

        client.verifyAll();
    }

    @Test
    void missingAField() {
        client.expect(HttpMethod.POST, path)
                .body(of("beetle", "ringo", "number", "42"))
                .thenReturn()
                .withStatus(200);

        Unirest.post(path)
                .field("beetle", "ringo")
                .asEmpty();

        assertException(() -> client.verifyAll(),
                "A expectation was never invoked! POST http://basic\n" +
                        "Body:\n" +
                        "\tnumber=42");
    }

    @Test
    void forFieldsShouldBeEncoded() {
        client.expect(HttpMethod.POST, path)
                .body(of("beetles", "ringo george",
                         "url", "http://somewhere"))
                .thenReturn("cool")
                .withStatus(200);

        String result = Unirest.post(path)
                .field("beetles", "ringo george")
                .field("url", "http://somewhere")
                .asString()
                .getBody();

        client.verifyAll();

        assertEquals("cool", result);
    }
}
