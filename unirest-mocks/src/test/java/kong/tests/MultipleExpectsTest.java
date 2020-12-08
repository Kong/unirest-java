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

import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static kong.unirest.HttpMethod.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipleExpectsTest extends Base {

    @Test
    void canDifferenciateBetweenExpectations() {
        client.expect(GET, path).header("monster", "oscar").header("fruit", "apple");
        client.expect(GET, path).header("monster", "oscar");

        Unirest.get(path).header("monster", "oscar").asEmpty();

        assertException(() -> client.verifyAll(),
                "A expectation was never invoked! GET http://basic\n" +
                        "Headers:\n" +
                        "monster: oscar\n" +
                        "fruit: apple");
    }

    @Test
    void willPickTheBestMatchingExpectation() {
        client.expect(GET, path)
                .header("monster", "grover")
                .header("fruit", "apples");

        client.expect(GET, path)
                .header("monster", "grover");

        Unirest.get(path)
                .header("monster", "grover")
                .header("fruit", "apples")
                .asEmpty();

        assertException(() -> client.verifyAll(),
                "A expectation was never invoked! GET http://basic\n" +
                        "Headers:\n" +
                        "monster: grover");
    }

    @Test
    void willPickTheBestMatchingQueryExpectation() {
        client.expect(GET, path)
                .queryString("monster", "grover");

        client.expect(GET, path)
                .queryString("monster", "grover")
                .queryString("fruit", "apples");

        client.expect(GET, path)
                .queryString("monster", "grover");

        Unirest.get(path)
                .queryString("monster", "grover")
                .queryString("fruit", "apples")
                .asEmpty();

        assertException(() -> client.verifyAll(),
                "A expectation was never invoked! GET http://basic\n" +
                        "Params:\n" +
                        "monster: grover");
    }

    @Test
    void whenDuplicateExpectsExistUseTheLastOne() {
        client.expect(GET, path)
                .queryString("monster", "grover")
                .thenReturn("one");

        client.expect(GET, path)
                .queryString("monster", "grover")
                .thenReturn("two");

        String result = Unirest.get(path)
                .queryString("monster", "grover")
                .asString()
                .getBody();

        assertEquals("two", result);

        assertException(() -> client.verifyAll(),
                "A expectation was never invoked! GET http://basic\n" +
                        "Params:\n" +
                        "monster: grover");
    }
}
