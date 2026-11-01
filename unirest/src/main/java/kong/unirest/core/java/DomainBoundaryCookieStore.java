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

package kong.unirest.core.java;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wraps the JDK's default {@link CookieStore}, which compares a cookie's domain to the request
 * host as a plain string suffix, so a cookie from example.com also matches notexample.com.
 * A domain without a leading dot is the normal case because the JDK uses the request host as
 * the domain of any cookie that does not set one.
 * <p>
 * Everything else is left to the JDK, this only drops matches that do not start on a dot.
 * </p>
 */
class DomainBoundaryCookieStore implements CookieStore {

    private final CookieStore store = new CookieManager().getCookieStore();

    @Override
    public void add(URI uri, HttpCookie cookie) {
        store.add(uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return store.get(uri).stream()
                .filter(cookie -> !matchesPartOfALabel(cookie.getDomain(), uri.getHost()))
                .collect(Collectors.toList());
    }

    @Override
    public List<HttpCookie> getCookies() {
        return store.getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return store.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return store.remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return store.removeAll();
    }

    private static boolean matchesPartOfALabel(String domain, String host) {
        if (domain == null || host == null || domain.startsWith(".")) {
            return false;
        }
        int start = host.length() - domain.length();
        return start > 0
                && host.regionMatches(true, start, domain, 0, domain.length())
                && host.charAt(start - 1) != '.';
    }
}
