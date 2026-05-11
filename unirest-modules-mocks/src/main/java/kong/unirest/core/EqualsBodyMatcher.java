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

import java.util.List;
import java.util.Objects;

/**
 * A {@link BodyMatcher} implementation that performs exact string equality matching.
 * <p>
 * This matcher compares the actual request body against an expected string value using
 * {@link Objects#equals(Object, Object)}. It requires exactly one body element and
 * performs a strict character-by-character comparison.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * mockClient.expect(HttpMethod.POST, "/api/users")
 *     .body(new EqualsBodyMatcher("{\"name\":\"John\"}"))
 *     .thenReturn()
 *     .withStatus(201);
 * }</pre>
 *
 * @see BodyMatcher
 * @see MatchStatus
 */
public class EqualsBodyMatcher implements BodyMatcher {
    private final String expected;

    /**
     * Creates a new EqualsBodyMatcher with the expected body content.
     *
     * @param body the expected body string to match against
     */
    public EqualsBodyMatcher(String body) {
        this.expected = body;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Matches if the body list contains exactly one element that equals the expected string.
     * Uses {@link Objects#equals(Object, Object)} for null-safe comparison.
     * </p>
     *
     * @param body the list of body content strings from the actual request
     * @return a {@link MatchStatus} indicating whether the body matched,
     *         with the expected value for error reporting
     * @throws AssertionError if matching fails unexpectedly
     */
    @Override
    public MatchStatus matches(List<String> body) throws AssertionError {
        return new MatchStatus(body.size() == 1 && Objects.equals(expected, body.get(0)), expected);
    }

}
