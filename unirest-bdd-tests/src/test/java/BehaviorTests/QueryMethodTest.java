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

import kong.unirest.core.Unirest;
import org.junit.jupiter.api.Test;

public class QueryMethodTest extends BddTest {
    @Test
    void canUseQueryVerbWithFormFields() {
        Unirest.query(MockServer.QUERY)
                .queryString("foo", "bar")
                .field("fruit", "apple")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar")
                .assertParam("fruit", "apple");
    }

    @Test
    void canUseQueryVerbWithUniBody() {
        Unirest.query(MockServer.QUERY)
                .queryString("foo", "bar")
                .body("this is a very complicated query")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar")
                .assertBody("this is a very complicated query");
    }
}
