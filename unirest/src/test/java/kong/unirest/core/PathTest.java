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

import kong.unirest.core.Path;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PathTest {
    @Test
    void sameOriginIgnoresPathQueryAndCase() {
        Path p = new Path("https://Example.com/a?b=c", null);

        assertTrue(p.isSameOrigin(new Path("HTTPS://example.COM/other?page=2", null)));
    }

    @Test
    void sameOriginTreatsTheDefaultPortAsImplicit() {
        assertTrue(new Path("https://example.com/", null).isSameOrigin(new Path("https://example.com:443/", null)));
        assertTrue(new Path("http://example.com/", null).isSameOrigin(new Path("http://example.com:80/", null)));
    }

    @Test
    void differentHostSchemeOrPortIsAnotherOrigin() {
        Path p = new Path("https://example.com/", null);

        assertFalse(p.isSameOrigin(new Path("https://other.example.com/", null)));
        assertFalse(p.isSameOrigin(new Path("http://example.com/", null)));
        assertFalse(p.isSameOrigin(new Path("https://example.com:8443/", null)));
    }

    @Test
    void userInfoDoesNotChangeTheComparedHost() {
        Path p = new Path("https://example.com/", null);

        assertFalse(p.isSameOrigin(new Path("https://example.com@evil.example/", null)));
    }

    @Test
    void hostlessOrInvalidUrlsOnlyMatchThemselves() {
        Path relative = new Path("/a", null);

        assertTrue(relative.isSameOrigin(new Path("/a", null)));
        assertFalse(relative.isSameOrigin(new Path("/b", null)));
        assertFalse(new Path("http://a_b/", null).isSameOrigin(new Path("http://c_d/", null)));
        assertFalse(new Path("http://[bad/", null).isSameOrigin(new Path("http://example.com/", null)));
    }

    @Test
    void pathIsTheSameIfUrlIs() {
        Path p = new Path("https://localhost/apples", null);
        Path o = new Path("https://localhost/apples", null);
        assertEquals(p, o);
    }

    @Test
    void pathsWithDifferentParams() {
        String raw = "http://somewhere/fruits/{id}";
        Path q = new Path(raw, null);
        q.param("id", "apple");
        Path w = new Path(raw, null);
        w.param("id", "apple");
        Path e = new Path(raw, null);
        e.param("id", "oranges");

        assertEquals(q,w);
        assertNotEquals(q, e);
    }

    @Test
    void queryParamsMatter() {
        String raw = "http://somewhere/fruits/}";
        Path q = new Path(raw, null);
        q.queryString("id", "apple");
        Path w = new Path(raw, null);
        w.queryString("id", "apple");
        Path e = new Path(raw, null);
        e.queryString("id", "oranges");

        assertEquals(q,w);
        assertNotEquals(q, e);
    }

    @Test
    void canBuildUsingDefaultBase() {
        assertEquals("http://somwhere/fruit", new Path("/fruit","http://somwhere").toString());
    }

    @Test
    void willNotAddBaseIfPrimaryPathIsFull() {
        assertEquals("http://somwhere/fruit", new Path("http://somwhere/fruit","http://elsewhere/rocks").toString());
    }

    @Test
    void dotsInPaths() {
        var path = new Path("http://localhost/{a}/");

        path.param("a", ".");

        assertThat(path.toString())
                .isEqualTo("http://localhost/./");
    }

    @Test
    void doubleDotsInPaths() {
        var path = new Path("http://localhost/{a}/");

        path.param("a", "..");

        assertThat(path.toString())
                .isEqualTo("http://localhost/../");
    }
}