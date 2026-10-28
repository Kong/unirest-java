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

import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Locale;

/**
 * A {@link CookiePolicy} that applies RFC 6265 section 5.1.3 domain matching: a cookie is
 * only accepted when its Domain is the request host itself or a parent of it, so one host
 * cannot set cookies that are later sent to another.
 * <p>
 * {@link CookiePolicy#ACCEPT_ORIGINAL_SERVER} is stricter than this: it rejects an explicit
 * Domain on dotless hosts such as localhost and Domain values without a leading dot.
 * </p>
 *
 * @see Config#cookiePolicy(CookiePolicy)
 */
public class DomainMatchCookiePolicy implements CookiePolicy {

    @Override
    public boolean shouldAccept(URI uri, HttpCookie cookie) {
        String domain = cookie.getDomain();
        if (domain == null) {
            return true;
        }
        String host = uri.getHost();
        if (host == null) {
            return false;
        }
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        host = host.toLowerCase(Locale.ROOT);
        domain = domain.toLowerCase(Locale.ROOT);
        return host.equals(domain)
                || host.endsWith("." + domain)
                || HttpCookie.domainMatches(domain, host);
    }
}
