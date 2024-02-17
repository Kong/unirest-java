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

package BehaviorTests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import io.javalin.http.Context;
import org.assertj.core.data.MapEntry;
import org.assertj.guava.api.MultimapAssert;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class HeaderAsserts {
    public ArrayListMultimap<String, HeaderValue> headers = ArrayListMultimap.create();

    public HeaderAsserts(){}

    public HeaderAsserts(Context req) {
        Collections.list(req.req().getHeaderNames())
                .forEach(name -> Collections.list(req.req().getHeaders(name))
                        .forEach(value -> headers.put(name, new HeaderValue(value))));
    }

    public HeaderAsserts(Map<String, String> values){
        values.forEach((k,v) -> headers.put(k, new HeaderValue(v)));
    }

    public void assertNoHeader(String s) {
        assertFalse(headers.containsKey(s), "Should Have No Header " + s);
    }

    public void assertHeader(String key, String... value) {
        assertMultiMap(headers).containsKeys(key);
        assertThat(headers.get(key))
                .extracting(HeaderValue::getValue)
                .contains(value);

    }

    public void assertBasicAuth(String username, String password) {
        assertMultiMap(headers).containsKeys("Authorization");
        var value = headers.get("Authorization").get(0);
        assertThat(value.getValue()).as("Missing value scope of Basic").contains("Basic ");
        String decoded = new String(Base64.getDecoder().decode(value.getValue().replace("Basic ", "")));
        assertThat(decoded).isEqualTo(username + ":" + password);
    }

    public void assertHeaderSize(String name, int size) {
        assertEquals(size, headers.get(name).size());
    }

    public void assertMultiPartContentType() {
        List<HeaderValue> h = headers.get("Content-Type");
        assertEquals(1, h.size(), "Expected exactly 1 Content-Type header");
        HeaderValue value = h.get(0);
        value.assertMainValue("multipart/form-data");
        value.assertHasParam("boundary");
        value.assertParam("charset", "UTF-8");
    }

    public void assertHeaderWithParam(String headerKey, String headerValue, String paramKey, String paramValue) {
        assertMultiMap(headers).containsKeys(headerKey);
        var value = headers.get(headerKey).get(0);
        value.assertMainValue(headerValue);
        value.assertParam(paramKey, paramValue);
    }

    public void assertRawValue(String key, String value) {
        assertThat(headers.get(key))
                .isNotEmpty()
                .extracting(HeaderValue::rawValue)
                .contains(value);
    }

    private static class HeaderValue {
        @JsonIgnore private static final Splitter partSplitter = Splitter.on(";").trimResults().omitEmptyStrings();
        @JsonIgnore private static final Splitter paramSplitter = Splitter.on("=").trimResults().omitEmptyStrings();
        @JsonProperty("rawValue")
        private String rawValue;
        @JsonProperty("value")
        private String value;
        @JsonProperty("params")
        private ArrayListMultimap<String, String> params = ArrayListMultimap.create();

        public HeaderValue(){}

        public HeaderValue(String value) {
            this.rawValue = value;
            if(value.contains(";")){
                int pos=0;
                for(String part : partSplitter.split(value)){
                    if(pos == 0){
                        this.value = part;
                    } else if (part.contains("=")) {
                        var param = paramSplitter.splitToList(part);
                        params.put(param.get(0), param.get(1));
                    }
                    pos++;
                }
            } else {
                this.value = value;
            }
        }

        public String getValue() {
            return value;
        }

        public void assertHasParam(String name) {
            assertMultiMap(params)
                    .as("Header Param")
                    .containsKeys(name);
        }

        public void assertMainValue(String expectedValue) {
            assertEquals(expectedValue, value);
        }

        public void assertParam(String name, String value) {
            assertMultiMap(params)
                    .as("Header Param")
                    .contains(MapEntry.entry(name, value));
        }

        public String rawValue() {
            return rawValue;
        }
    }

    public static <K, V> MultimapAssert<K, V> assertMultiMap(final Multimap<K, V> actual) {
        return org.assertj.guava.api.Assertions.assertThat(actual);
    }
}
