/**
 * The MIT License
 * <p>
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package kong.unirest.core;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static kong.unirest.core.QueryParams.fromURI;
import static kong.unirest.core.QueryParamsTest.NameValuePairsAssert.assertThatPairs;

class QueryParamsTest {

    private static Collection<QueryParams.NameValuePair> parse(String query, Object... params) {
        return fromURI("http://l" + String.format(query, params))
                .getQueryParams();
    }

    @Test
    void justOne() {
        var q = parse("?a=1");

        assertThatPairs(q)
                .hasSize(1)
                .containsPair("a", "1");
    }

    @Test
    void simpleParsing() {
        var q = parse("?a=1&a=2&b=3");

        assertThatPairs(q)
                .hasSize(3)
                .containsPair("a", "1")
                .containsPair("a", "2")
                .containsPair("b", "3");
    }

    @Test
    void paramKeysAndValuesAreDecoded() {
        var q = parse("?%s=%s", encode("a t", UTF_8), encode("%", UTF_8));

        assertThatPairs(q)
                .hasSize(1)
                .containsPair("a t", "%");
    }

    @Test
    void equalsInValue() {
        var q = parse("?a=1=h");

        assertThatPairs(q)
                .hasSize(1)
                .containsPair("a", "1=h");
    }

    @Test
    void questionMarkInKeysAndValues() {
        var q = parse("?a?b=1?2");

        assertThatPairs(q)
                .hasSize(1)
                .containsPair("a?b", "1?2");
    }

    @Test
    void valuelessParams() {
        var q = parse("?flag&a=1");

        assertThatPairs(q)
                .hasSize(2)
                .containsPair("flag", "")
                .containsPair("a", "1");
    }


    public static class NameValuePairsAssert
            extends AbstractAssert<NameValuePairsAssert, Collection<QueryParams.NameValuePair>> {

        private NameValuePairsAssert(Collection<QueryParams.NameValuePair> actual) {
            super(actual, NameValuePairsAssert.class);
        }

        public static NameValuePairsAssert assertThatPairs(Collection<QueryParams.NameValuePair> actual) {
            return new NameValuePairsAssert(actual);
        }

        public NameValuePairsAssert containsPair(String name, String value) {
            boolean found = actual.stream()
                    .anyMatch(p -> name.equals(p.getName()) && value.equals(p.getValue()));
            if (!found) {
                failWithMessage("Expected pair <%s=%s> but actual pairs were: %s",
                        name, value,
                        actual.stream().map(p -> p.getName() + "=" + p.getValue()).collect(Collectors.toList()));
            }
            return this;
        }

        public NameValuePairsAssert hasSize(int i) {
            if (actual.size() != i) {
                failWithMessage(
                        "Expected size of %s but was %s",
                        i, actual.size()
                );
            }
            return this;
        }
    }
}