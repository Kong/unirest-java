package kong.unirest.core;

import org.junit.jupiter.api.Test;

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

    private CacheManager.Key createKey(HttpRequest request, boolean isAsync, Class<?> responseType) {
        return new CacheManager.CacheMap.HashKey(request, isAsync, responseType);
    }
}