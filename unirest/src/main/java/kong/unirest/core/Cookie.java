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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an HTTP cookie parsed from the Set-Cookie header.
 * <p>
 * This class implements cookie parsing and formatting according to
 * <a href="https://tools.ietf.org/html/rfc6265">RFC 6265</a>.
 *
 * @see <a href="https://en.wikipedia.org/wiki/HTTP_cookie">HTTP cookie (Wikipedia)</a>
 */
public class Cookie {
    private String name;
    private String value;
    private String domain;
    private String path;
    private boolean httpOnly;
    private Integer maxAge;
    private ZonedDateTime expires;
    private boolean secure;
    private SameSite sameSite;
    private boolean partitioned;

    /**
     * Creates a new cookie with the specified name and value.
     *
     * @param name the cookie name
     * @param value the cookie value
     */
    public Cookie(String name, String value){
        this.name = name;
        this.value = value;
    }

    /**
     * Constructs a cookie by parsing a Set-Cookie header value.
     *
     * @param v the Set-Cookie header value to parse
     */
    public Cookie(String v) {
        this(v.split(";"));
    }

    private Cookie(String[] split){
        int pos = 0;
        for(String s : split){
            if(pos == 0){
                String[] sub = s.split("=",2);
                name = sub[0];
                if (sub.length == 2) {
                    value = stripQuoteWrapper(sub[1]);
                } else {
                    value = "";
                }
            } else {
                String[] sub = s.split("=");
                parseSection(sub);
            }
            pos++;
        }
    }

    private String getDecode(String sub) {
        try {
            return URLDecoder.decode(sub, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return sub;
        }
    }

    private String stripQuoteWrapper(String sub) {
        if(sub.startsWith("\"") && sub.endsWith("\"") && sub.length() > 1){
            return sub.substring(1, sub.length() -1);
        }
        return sub;
    }

    private void parseSection(String[] sub) {
        switch (sub[0].toLowerCase().trim()) {
            case "path": {
                path = sub[1];
                break;
            }
            case "domain": {
                domain = sub[1];
                break;
            }
            case "expires": {
                parseExpires(sub[1]);
                break;
            }
            case "max-age": {
                maxAge = Integer.parseInt(sub[1]);
                break;
            }
            case "httponly": {
                httpOnly = true;
                break;
            }
            case "partitioned":
                partitioned = true;
                break;
            case "secure": {
                secure = true;
                break;
            }
            case "samesite": {
                sameSite = SameSite.parse(sub[1]);
            }
        }
    }

    private void parseExpires(String text) {
        expires = Util.tryParseToDate(text);
    }

    @Override
    public String toString() {
        List<Pair> pairs = new ArrayList<>();
        pairs.add(new Pair(name, value));
        if(path != null){
            pairs.add(new Pair("Path", path));
        }
        if(domain != null){
            pairs.add(new Pair("Domain", domain));
        }
        if(expires != null){
            pairs.add(new Pair("Expires", expires.format(Util.DEFAULT_PATTERN)));
        }
        if(maxAge != null){
            pairs.add(new Pair("Max-Age", String.valueOf(maxAge)));
        }
        if(httpOnly){
            pairs.add(new Pair("HttpOnly", null));
        }
        if(secure){
            pairs.add(new Pair("Secure", null));
        }
        if(partitioned){
            pairs.add(new Pair("Partitioned", null));
        }
        return pairs.stream().map(Pair::toString).collect(Collectors.joining(";"));
    }

    /**
     * Sets the domain attribute of the cookie.
     *
     * @param domain the domain value
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Sets the path attribute of the cookie.
     *
     * @param path the path value
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Sets the HttpOnly attribute of the cookie.
     *
     * @param httpOnly {@code true} to mark the cookie as HttpOnly
     */
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    /**
     * Returns whether the cookie has the Partitioned attribute set.
     *
     * @return {@code true} if the cookie is partitioned, {@code false} otherwise
     */
    public boolean isPartitioned() {
        return this.partitioned;
    }

    /**
     * Sets the Partitioned attribute of the cookie.
     *
     * @param partitionedFlag {@code true} to mark the cookie as partitioned
     */
    public void setPartitioned(boolean partitionedFlag) {
        this.partitioned = partitionedFlag;
    }

    /**
     * Sets the Secure attribute of the cookie.
     *
     * @param secureFlag {@code true} to mark the cookie as secure
     */
    public void setSecured(boolean secureFlag) {
        this.secure = secureFlag;
    }

    private static class Pair {
        final String key;
        final String value;

        public Pair(String key, String value){
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            if(value == null){
                return key;
            }
            return key + "=" + value;
        }
    }

    /**
     * Returns the cookie name.
     *
     * @return the cookie name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the cookie value.
     *
     * @return the cookie value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the cookie value, URL-decoded.
     *
     * @return the URL-decoded cookie value
     */
    public String getUrlDecodedValue() {
        return getDecode(value);
    }

    /**
     * Returns the domain attribute of the cookie.
     *
     * @return the domain value, or {@code null} if not set
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Returns the path attribute of the cookie.
     *
     * @return the path value, or {@code null} if not set
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns whether the cookie has the HttpOnly attribute set.
     * <p>
     * The HttpOnly attribute directs browsers not to expose cookies through
     * channels other than HTTP (and HTTPS) requests. This means that the cookie
     * cannot be accessed via client-side scripting languages (notably JavaScript),
     * and therefore cannot be stolen easily via cross-site scripting.
     *
     * @return {@code true} if the cookie is HttpOnly, {@code false} otherwise
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * Returns whether the cookie has the Secure attribute set.
     * <p>
     * The Secure attribute directs browsers to use cookies only via
     * secure/encrypted connections (HTTPS).
     *
     * @return {@code true} if the cookie is secure, {@code false} otherwise
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Returns the Max-Age attribute of the cookie.
     * <p>
     * The Max-Age attribute specifies the cookie's expiration as an interval
     * of seconds in the future, relative to the time the browser received the cookie.
     *
     * @return the Max-Age value in seconds, or {@code null} if not set
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Returns the Expires attribute of the cookie.
     * <p>
     * The Expires attribute defines a specific date and time for when
     * the browser should delete the cookie.
     *
     * @return the expiration date and time, or {@code null} if not set
     */
    public ZonedDateTime getExpiration() {
        return expires;
    }

    /**
     * Returns the SameSite attribute of the cookie.
     *
     * @return the SameSite attribute, or {@code null} if not set
     */
    public SameSite getSameSite() {
        return sameSite;
    }

    /**
     * Represents the SameSite cookie attribute values.
     * <p>
     * The SameSite attribute controls whether the cookie is sent with
     * cross-site requests, providing protection against cross-site
     * request forgery attacks.
     */
    public enum SameSite {
        /** Cookie is sent with all requests, including cross-site. */
        None,
        /** Cookie is only sent with same-site requests or top-level navigations. */
        Strict,
        /** Cookie is sent with same-site requests and cross-site top-level navigations. */
        Lax;

        private static EnumSet<SameSite> all = EnumSet.allOf(SameSite.class);

        /**
         * Parses a string value into a SameSite enum constant.
         *
         * @param value the string value to parse (case-insensitive)
         * @return the matching SameSite value, or {@code null} if no match
         */
        public static SameSite parse(String value) {
            return all.stream()
                    .filter(e -> e.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(null);
        }
    }
}
