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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class HashKeyTest {

    @Test
    void basicDifferences() {
        var key1 = createKey(Unirest.get("http://somewhere"), false, String.class);
        var key2 = createKey(Unirest.get("http://somewhere"), false, String.class);

        assertThat(key1)
                .isEqualTo(key2);
    }

    @Test
    void differentReturnTypesMakesThemDifferent() {
        var key1 = createKey(Unirest.get("http://somewhere"), false, String.class);
        var key2 = createKey(Unirest.get("http://somewhere"), false, Integer.class);

        assertThat(key1)
                .isNotEqualTo(key2);
    }

    @Test
    void asyncModeMakesThemDifferent() {
        var key1 = createKey(Unirest.get("http://somewhere"), false, String.class);
        var key2 = createKey(Unirest.get("http://somewhere"), true, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);
    }

    @Test
    void pathsMakeThemDifferent() {
        var key1 = createKey(Unirest.get("http://somewhere"), false, String.class);
        var key2 = createKey(Unirest.get("http://else"),      false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);
    }

    @Test
    void methodMakeThemDifferent() {
        var key1 = createKey(Unirest.get("http://somewhere"), false, String.class);
        var key2 = createKey(Unirest.put("http://somewhere"), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);
    }

    @Test
    void headersMakeThemDiffereent() {
        var key1 = createKey(Unirest.get("http://somewhere").header("foo", "bar"), false, String.class);
        var key2 = createKey(Unirest.put("http://somewhere").header("bar", "baz"), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);
    }

    @Test
    void headersOrderMatters() {
        var key1 = createKey(Unirest.get("http://somewhere").header("foo", "bar").header("fruit", "apple"), false, String.class);
        var key2 = createKey(Unirest.put("http://somewhere").header("fruit", "apple").header("foo", "bar"), false, String.class);
        var key3 = createKey(Unirest.put("http://somewhere").header("fruit", "apple").header("foo", "bar"), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);

        assertThat(key2)
                .isEqualTo(key3);
    }

    @Test
    void queryParamsMatter() {
        var key1 = createKey(Unirest.get("http://somewhere").queryString("foo", "bar").queryString("fruit", "apple"), false, String.class);
        var key2 = createKey(Unirest.put("http://somewhere").queryString("fruit", "apple").queryString("foo", "bar"), false, String.class);
        var key3 = createKey(Unirest.put("http://somewhere").queryString("fruit", "apple").queryString("foo", "bar"), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);

        assertThat(key2)
                .isEqualTo(key3);
    }

    @Test
    void headerValuesContainingTheSeparatorAreNotSplit() {
        var key1 = createKey(Unirest.get("http://somewhere").header("foo", "bar|fruit: apple"), false, String.class);
        var key2 = createKey(Unirest.get("http://somewhere").header("foo", "bar").header("fruit", "apple"), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);
    }

    @Test
    void bodiesMakeThemDifferent() {
        var key1 = createKey(Unirest.post("http://somewhere").body("{\"user\":\"alice\"}"), false, String.class);
        var key2 = createKey(Unirest.post("http://somewhere").body("{\"user\":\"bob\"}"), false, String.class);
        var key3 = createKey(Unirest.post("http://somewhere").body("{\"user\":\"bob\"}"), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);

        assertThat(key2)
                .isEqualTo(key3);
    }

    @Test
    void byteBodiesAreComparedByContent() {
        var key1 = createKey(Unirest.post("http://somewhere").body(new byte[]{1, 2}), false, String.class);
        var key2 = createKey(Unirest.post("http://somewhere").body(new byte[]{1, 3}), false, String.class);
        var key3 = createKey(Unirest.post("http://somewhere").body(new byte[]{1, 3}), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);

        assertThat(key2)
                .isEqualTo(key3);
    }

    @Test
    void formFieldsMakeThemDifferent() {
        var key1 = createKey(Unirest.post("http://somewhere").field("user", "alice").field("password", "right"), false, String.class);
        var key2 = createKey(Unirest.post("http://somewhere").field("user", "alice").field("password", "wrong"), false, String.class);
        var key3 = createKey(Unirest.post("http://somewhere").field("user", "alice").field("password", "wrong"), false, String.class);
        var key4 = createKey(Unirest.post("http://somewhere").field("user", "alice|password|wrong"), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);

        assertThat(key2)
                .isEqualTo(key3)
                .isNotEqualTo(key4);
    }

    @Test
    void entityAndFormBodiesAreDifferent() {
        var key1 = createKey(Unirest.post("http://somewhere").body("user=alice"), false, String.class);
        var key2 = createKey(Unirest.post("http://somewhere").field("user", "alice"), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);
    }

    @Test
    void filesAndStreamsNeverShareAKey() {
        var file = new File("pom.xml");
        var key1 = createKey(Unirest.post("http://somewhere").field("upload", file), false, String.class);
        var key2 = createKey(Unirest.post("http://somewhere").field("upload", file), false, String.class);
        var key3 = createKey(Unirest.post("http://somewhere").body(new ByteArrayInputStream(new byte[]{1})), false, String.class);
        var key4 = createKey(Unirest.post("http://somewhere").body(new ByteArrayInputStream(new byte[]{1})), false, String.class);

        assertThat(key1)
                .isNotEqualTo(key2);

        assertThat(key3)
                .isNotEqualTo(key4);
    }

    private CacheManager.Key createKey(HttpRequest request, boolean isAsync, Class<?> responseType) {
        return new CacheManager.CacheMap.HashKey(request, isAsync, responseType);
    }
}