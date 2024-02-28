package kong.unirest.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class AwsSignerV4Test {

    @Test
    void getMethod() {
        var sig = getSig(Unirest.get("http://foo.com"));
        assertEquals("GET", sig.getHttpMethod());
    }

    @Test
    void getCanonicalUri_noPath() {
        var sig = getSig(Unirest.get("http://foo.com?fruit=apples"));

        assertEquals("http%3A%2F%2Ffoo.com%2F", sig.getCanonicalUri());
    }

    @Test
    void getCanonicalUri_withPath() {
        var sig = getSig(Unirest.get("http://foo.com/bar/baz?fruit=apples"));

        assertEquals("http%3A%2F%2Ffoo.com%2Fbar%2Fbaz", sig.getCanonicalUri());
    }

    @Test
    void canonicalQueryString_empty() {
        var req = Unirest.get("http://foo.com/bar/baz");
        var sig = getSig(req);
        assertEquals("", sig.getCanonicalQueryString());
    }

    @Test
    void canonicalQueryString_One() {
        var req = Unirest.get("http://foo.com/bar/baz")
                .queryString("fruit", "apples");
        var sig = getSig(req);

        assertEquals("fruit=apples", sig.getCanonicalQueryString());
    }

    @Test
    void canonicalQueryString_Two() {
        var req = Unirest.get("http://foo.com/bar/baz")
                .queryString("tool", "hammer")
                .queryString("fruit", "apples");

        var sig = getSig(req);

        assertEquals("fruit=apples&tool=hammer", sig.getCanonicalQueryString());
    }

    @Test
    void canonicalQueryString_WithEncoding() {
        var req = Unirest.get("http://foo.com/bar/baz")
                .queryString("to ol", "ham mer")
                .queryString("fruit", "app+les");

        var sig = getSig(req);

        assertEquals("fruit=app%2Bles&to%20ol=ham%20mer", sig.getCanonicalQueryString());
    }

    @Test
    void canonicalHeadersDefaults() {
        var req = Unirest.get("http://foo.com/bar/baz");

        var sig = getSig(req);

        assertEquals("host:foo.com", sig.getCanonicalHeaders());
    }

    @Test
    void canonicalHeadersWithAwsHeaders() {
        var req = Unirest.get("http://foo.com/bar/baz")
                .header("x-amz-beta", "   monkeys")
                .header("x-AMZ-alpha", "cheese")
                .header("x-amz-alpha", "lol")
                .header("something", "else");

        var sig = getSig(req);

        assertEquals("host:foo.com\n" +
                "x-amz-alpha:cheese,lol\n" +
                "x-amz-beta:monkeys", sig.getCanonicalHeaders());
    }

    @Test
    void signedHeaders() {
        var req = Unirest.get("http://foo.com/bar/baz")
                .header("x-amz-zulu", "   monkeys")
                .header("x-AMZ-alpha", "cheese")
                .header("x-amz-alpha", "lol")
                .header("something", "else");

        var sig = getSig(req);

        assertEquals("host;x-amz-alpha;x-amz-zulu", sig.getSignedHeaders());
    }

    @Test
    void hashedPayload_get() {
        var req = Unirest.get("http://foo.com/bar/baz");

        var sig = getSig(req);

        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
                ,sig.getHashedPayload());
    }

    private static AwsSignerV4.CanonicalRequest getSig(GetRequest req) {
        return new AwsSignerV4.CanonicalRequest(req);
    }
}