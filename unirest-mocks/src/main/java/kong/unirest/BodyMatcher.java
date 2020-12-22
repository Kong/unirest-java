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

import java.util.List;

/**
 * Body Matchers can be used to evaluate a entity body.
 * This is represented by one or more (in the case of multi-part) sections
 * these sections are represented as strings in the same way as toString represents the part
 * For a single entity like a JSON payload this is simply going to be the JSON String
 * For field params it will be a key value pair like 'fruit=orange`
 * For binary types it will be some kind of indication like a file path.
 */
public interface BodyMatcher {
    /**
     * indicates if the Matcher succeeded in matching the body
     * @param body the list of body parts
     * @return MatchStatus indicating if the Matcher succeeded in matching the body
     */
    MatchStatus matches(List<String> body);

}
