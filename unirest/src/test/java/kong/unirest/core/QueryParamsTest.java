package kong.unirest.core;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;
import static kong.unirest.core.QueryParams.fromURI;
import static kong.unirest.core.QueryParamsTest.NameValuePairsAssert.assertThatPairs;

class QueryParamsTest {

    @Test
    void simpleParsing() {
        var q = fromURI("http://lh?a=1&a=2&b=3")
                .getQueryParams();

        assertThatPairs(q)
                .hasSize(3)
                .containsPair("a", "1")
                .containsPair("a", "2")
                .containsPair("b", "3");
    }

    @Test
    void paramKeysAndValuesAreDecoded(){
        var q = fromURI(String.format("http://1h?%s=%s", encode("a t"), encode("%")))
                .getQueryParams();

        assertThatPairs(q)
                .hasSize(1)
                .containsPair("a t","%");
    }

    @Test @Disabled
    void equalsInValue(){
        var q = fromURI("http://lh?a=1=h")
                .getQueryParams();

        assertThatPairs(q)
                .hasSize(1)
                .containsPair("a","1=h");
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
            if(actual.size() != i){
                failWithMessage(
                        "Expected size of %s but was %s",
                        i, actual.size()
                );
            }
            return this;
        }
    }
}