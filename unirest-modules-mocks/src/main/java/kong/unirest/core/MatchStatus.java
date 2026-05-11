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
 * Represents the result of a body matching operation.
 * <p>
 * This class encapsulates whether a {@link BodyMatcher} successfully matched the request body,
 * along with a description that can be used for error reporting when the match fails.
 * </p>
 * <p>
 * Typically returned by {@link BodyMatcher#matches(java.util.List)} to indicate
 * whether the actual request body matched the expected pattern.
 * </p>
 *
 * @see BodyMatcher
 * @see EqualsBodyMatcher
 */
public class MatchStatus {
    private final boolean isSuccess;
    private final String description;

    /**
     * Constructs a new MatchStatus with the specified success flag and description.
     *
     * @param isSuccess   {@code true} if the match was successful, {@code false} otherwise
     * @param description a description of what was expected; used for error messages when matching fails
     */
    public MatchStatus(boolean isSuccess, String description) {
        this.isSuccess = isSuccess;
        this.description = description;
    }

    /**
     * Returns whether the match was successful.
     *
     * @return {@code true} if the body matched the expected pattern, {@code false} otherwise
     */
    public boolean isSuccess(){
        return isSuccess;
    }

    /**
     * Returns the description associated with this match status.
     * <p>
     * This typically contains the expected value or pattern, which is useful
     * for generating meaningful error messages when the match fails.
     * </p>
     *
     * @return the description, or {@code null} if none was provided
     */
    public String getDescription() {
        return description;
    }
}
