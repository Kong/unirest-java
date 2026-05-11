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

/**
 * Thrown to indicate that a mock verification or assertion has failed.
 * <p>
 * This exception extends {@link AssertionError} and is used by the Unirest mock framework
 * to signal that an expected condition was not met. It is thrown by various assertion
 * and verification methods in the mocking API.
 * </p>
 *
 * <h2>Common scenarios where this is thrown:</h2>
 * <ul>
 *   <li>An expected request was never invoked</li>
 *   <li>A request was invoked a different number of times than expected</li>
 *   <li>An expected message was not received on a WebSocket</li>
 *   <li>A mock component was not properly initialized</li>
 * </ul>
 *
 * @see MockClient#assertThat(HttpMethod, String)
 * @see MockClient#verifyAll()
 * @see MockListener
 * @see Times
 */
public class UnirestAssertion extends AssertionError {

    /**
     * Constructs a new UnirestAssertion with a formatted message.
     * <p>
     * The message is formatted using {@link String#format(String, Object...)},
     * allowing for dynamic error messages with placeholders.
     * </p>
     *
     * @param base the format string for the error message
     * @param args the arguments to be substituted into the format string
     */
    public UnirestAssertion(String base, Object... args){
        super(String.format(base, args));
    }
}
