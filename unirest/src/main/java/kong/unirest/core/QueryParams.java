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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;

import static java.nio.charset.StandardCharsets.UTF_8;

public class QueryParams {
    private final Collection<NameValuePair> pairs;

    public static QueryParams fromURI(String uri) {
        return new QueryParams(uri);
    }

    public static QueryParams fromBody(String body) {
        var queryParams = new QueryParams();
        queryParams.parse(body);
        return queryParams;
    }

    private QueryParams() {
        pairs = new ArrayList<>();
    }

    private QueryParams(String url) {
        this();
        String[] urlParts = url.split("\\?", 2);
        if (urlParts.length > 1) {
            parse(urlParts[1]);
        }
    }

    private void parse(String q) {
        StringBuilder key = new StringBuilder();
        StringBuilder value = null;

        for (char c : q.toCharArray()) {
            if (value == null) {
                if (c == '=') {
                    value = new StringBuilder();
                } else if (c == '&') {
                    addPair(key, new StringBuilder());
                    key = new StringBuilder();
                }
                else {
                    key.append(c);
                }
            } else {
                if (c == '&') {
                    addPair(key, value);
                    key = new StringBuilder();
                    value = null;
                } else {
                    value.append(c);
                }
            }
        }
        if (key.length() > 0 && value != null) {
            addPair(key, value);
        }
    }

    private void addPair(StringBuilder key, StringBuilder value) {
        pairs.add(new NameValuePair(
                URLDecoder.decode(key.toString(), UTF_8),
                URLDecoder.decode(value.toString(), UTF_8))
        );
    }


    public Collection<NameValuePair> getQueryParams() {
        return new ArrayList<>(pairs);
    }

    public static class NameValuePair {
        private final String key;
        private final String value;

        public NameValuePair(String name, String value) {
            this.key = name;
            this.value = value;
        }

        public String getName() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "NameValuePair{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}