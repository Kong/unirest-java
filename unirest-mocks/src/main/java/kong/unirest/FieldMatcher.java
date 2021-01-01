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

import java.net.URLEncoder;
import java.util.*;

/**
 * Matches simple form fields
 */
public class FieldMatcher implements BodyMatcher {
    private final Map<String, String> formParams;

    /**
     * Creates a FieldMatcher expecting a map of keys and values
     * use like: FieldMatcher.of("fruit", "orange", "quantity" "42")
     * @param keyValuePairs an array of key-value pairs to expect
     * @return a new FieldMatcher
     */
    public static FieldMatcher of(String... keyValuePairs) {
        return new FieldMatcher(mapOf(keyValuePairs));
    }

    /**
     * Creates a FieldMatcher expecting a map of keys and values
     * @param formParams the map of field params
     */
    public FieldMatcher(Map<String, String> formParams) {
        this.formParams = formParams;
    }

    @Override
    public MatchStatus matches(List<String> body) throws AssertionError {
        List<String> missing = new ArrayList<>();
        boolean pass = true;
        for (Map.Entry<String, String> r : formParams.entrySet()) {
            String expectedParam = r.getKey() + "=" + URLEncoder.encode(r.getValue());
            if (body.stream().noneMatch(p -> Objects.equals(expectedParam, p))) {
                missing.add(expectedParam);
                pass = false;
            }
        }
        return new MatchStatus(pass, description(pass, missing));
    }


    private String description(boolean pass, List<String> missing) {
        if(pass){
            return "";
        }
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        missing.forEach(m -> joiner.add(m));

        return joiner.toString();
    }

    private static <K, V> Map<K, V> mapOf(Object... keyValues) {
        Map<K, V> map = new HashMap<>();

        K key = null;
        for (int index = 0; index < keyValues.length; index++) {
            if (index % 2 == 0) {
                key = (K) keyValues[index];
            } else {
                map.put(key, (V) keyValues[index]);
            }
        }

        return map;
    }
}
