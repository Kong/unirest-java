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
 * Represents a cookie parsed from the Set-Cookie HTTP response header.
 * <p>
 * This class provides functionality to parse, store, and serialize HTTP cookies
 * as defined in <a href="https://tools.ietf.org/html/rfc6265">RFC 6265</a>.
 * </p>
 * <p>
 * Cookies can be constructed either by providing name-value pairs directly,
 * or by parsing a Set-Cookie header string. The class supports all standard
 * cookie attributes including:
 * </p>
 * <ul>
 *   <li><b>Domain</b> - The domain to which the cookie applies</li>
 *   <li><b>Path</b> - The URL path for which the cookie is valid</li>
 *   <li><b>Expires</b> - The expiration date/time of the cookie</li>
 *   <li><b>Max-Age</b> - The maximum age in seconds before the cookie expires</li>
 *   <li><b>Secure</b> - Whether the cookie should only be sent over HTTPS</li>
 *   <li><b>HttpOnly</b> - Whether the cookie is inaccessible to JavaScript</li>
 *   <li><b>SameSite</b> - Cross-site request restrictions (None, Strict, Lax)</li>
 *   <li><b>Partitioned</b> - Whether the cookie is partitioned (CHIPS)</li>
 * </ul>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Create a simple cookie
 * Cookie cookie = new Cookie("sessionId", "abc123");
 *
 * // Parse a Set-Cookie header
 * Cookie parsed = new Cookie("sessionId=abc123; Path=/; HttpOnly; Secure");
 *
 * // Access cookie attributes
 * String name = cookie.getName();
 * String value = cookie.getValue();
 * boolean isSecure = cookie.isSecure();
 * }</pre>
 *
 * <p>
 * Note: The RFC 6265 specification is complex. For a more accessible explanation,
 * see the <a href="https://en.wikipedia.org/wiki/HTTP_cookie">Wikipedia article on HTTP cookies</a>.
 * </p>
 *
 * @see <a href="https://tools.ietf.org/html/rfc6265">RFC 6265 - HTTP State Management Mechanism</a>
 * @see <a href="https://en.wikipedia.org/wiki/HTTP_cookie">Wikipedia - HTTP cookie</a>
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
     * Constructs a cookie with the specified name and value.
     * <p>
     * This creates a simple cookie without any additional attributes.
     * Attributes can be set using the setter methods.
     * </p>
     *
     * @param name  the cookie name (cookie-name)
     * @param value the cookie value (cookie-value)
     */
    public Cookie(String name, String value){
        this.name = name;
        this.value = value;
    }

    /**
     * Constructs a cookie by parsing a Set-Cookie header value.
     * <p>
     * The string is expected to be in the format defined by RFC 6265,
     * with the name-value pair first, followed by semicolon-separated attributes.
     *
     * <p>
     * Example formats:
     * <pre>{@code
     * "sessionId=abc123"
     * "sessionId=abc123; Path=/; HttpOnly"
     * "sessionId=abc123; Domain=.example.com; Secure; SameSite=Strict"
     * }</pre>
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

    /**
     * Returns the string representation of this cookie in Set-Cookie header format.
     * <p>
     * The output includes the name-value pair followed by all set attributes,
     * separated by semicolons, suitable for use in a Set-Cookie header.
     * </p>
     *
     * @return the cookie as a Set-Cookie header string
     */
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
     * Sets the domain attribute of this cookie.
     * <p>
     * The domain attribute specifies which hosts can receive the cookie.
     * If not specified, the cookie is only sent to the origin server.
     * </p>
     *
     * @param domain the domain value (e.g., ".example.com")
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Sets the path attribute of this cookie.
     * <p>
     * The path attribute specifies the URL path that must exist in the
     * requested URL for the browser to send the cookie.
     * </p>
     *
     * @param path the path value (e.g., "/api")
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Sets the HttpOnly attribute of this cookie.
     * <p>
     * When {@code true}, the cookie is inaccessible to JavaScript's
     * {@code Document.cookie} API, helping to mitigate cross-site
     * scripting (XSS) attacks.
     * </p>
     *
     * @param httpOnly {@code true} to make the cookie HTTP-only
     */
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    /**
     * Returns whether this cookie has the Partitioned attribute set.
     * <p>
     * Partitioned cookies (also known as CHIPS - Cookies Having Independent
     * Partitioned State) are stored separately for each top-level site,
     * providing better privacy by preventing cross-site tracking.
     * </p>
     *
     * @return {@code true} if the cookie is partitioned
     */
    public boolean isPartitioned() {
        return this.partitioned;
    }

    /**
     * Sets the Partitioned attribute of this cookie.
     * <p>
     * Partitioned cookies (CHIPS) are isolated by top-level site,
     * which helps prevent cross-site tracking while still allowing
     * embedded third-party content to maintain state.
     * </p>
     *
     * @param partitionedFlag {@code true} to make the cookie partitioned
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/Privacy/Privacy_sandbox/Partitioned_cookies">CHIPS Documentation</a>
     */
    public void setPartitioned(boolean partitionedFlag) {
        this.partitioned = partitionedFlag;
    }

    /**
     * Sets the Secure attribute of this cookie.
     * <p>
     * When {@code true}, the cookie is only sent to the server over
     * HTTPS connections, never over unencrypted HTTP.
     * </p>
     *
     * @param secureFlag {@code true} to make the cookie secure-only
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
     * @return the cookie-name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the cookie-value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the cookie-value, url-decoded
     */
    public String getUrlDecodedValue() {
        return getDecode(value);
    }

    /**
     * @return the domain value of the cookie
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @return the path value of the cookie
     */
    public String getPath() {
        return path;
    }

    /**
     * Per Wikipedia:
     * The HttpOnly attribute directs browsers not to expose cookies through channels other than HTTP (and HTTPS) requests.
     * This means that the cookie cannot be accessed via client-side scripting languages (notably JavaScript),
     * and therefore cannot be stolen easily via cross-site scripting (a pervasive attack technique)
     * @return a boolean if the cookie is httpOnly
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * Per Wikipedia:
     * The Secure attribute is meant to keep cookie communication limited to encrypted transmission,
     * directing browsers to use cookies only via secure/encrypted connections.
     * @return a boolean of if the cookie is secure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Per Wikipedia:
     * the Max-Age attribute can be used to set the cookie's expiration as an interval of seconds in the future,
     * relative to the time the browser received the cookie.
     * @return Max-Age attribute
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Per Wikipedia:
     * The Expires attribute defines a specific date and time for when the browser should delete the cookie.
     * @return a ZonedDateTime of the expiration
     */
    public ZonedDateTime getExpiration() {
        return expires;
    }

    /**
     * returns the SameSite attribute
     * @return the SameSite attribute if set. or null
     */
    public SameSite getSameSite() {
        return sameSite;
    }

    /**
     * The SameSite cookie attribute values.
     * <p>
     * The SameSite attribute controls whether a cookie is sent with cross-site requests,
     * providing protection gegen cross-site request forgery (CSRF) attacks.
     * </p>
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie/SameSite">MDN SameSite Documentation</a>
     */
    public enum SameSite {
        /**
         * The cookie is sent with both cross-site and same-site requests.
         * Requires the Secure attribute when used.
         */
        None,

        /**
         * The cookie is only sent with same-site requests (not with cross-site requests).
         * Provides the strictest CSRF protection.
         */
        Strict,

        /**
         * The cookie is sent with same-site requests and top-level navigations
         * from external sites (e.g., clicking a link), but not with cross-site
         * subrequests (e.g., loading images or frames).
         */
        Lax;

        private static EnumSet<SameSite> all = EnumSet.allOf(SameSite.class);

        /**
         * Parses a string value to a SameSite enum constant.
         * <p>
         * The comparison is case-insensitive.
         * </p>
         *
         * @param value the string value to parse (e.g., "Strict", "lax", "NONE")
         * @return the matching SameSite constant, or {@code null} if no match is found
         */
        public static SameSite parse(String value) {
            return all.stream()
                    .filter(e -> e.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(null);
        }
    }
}
