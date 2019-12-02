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

/**
 * a set of assertable things about the requests that the mock client handled.
 * Each instance of an Assert represents one or more calls to a specific method/path
 */
public interface Assert {
    /**
     * Assert that any request to this method/path contained this header
     * @param key the expected header key
     * @param value the expected header value
     * @throws UnirestAssertion
     */
    void assertHeader(String key, String value);

    /**
     * assert that this instance of method/path was invoked x times
     * @param i the number of times invoked.
     * @throws UnirestAssertion
     */
    void assertInvokedTimes(int i);

    /**
     * verify that all Expectations were fulfilled at least once.
     * @throws UnirestAssertion
     */
    void verifyAll();
}
