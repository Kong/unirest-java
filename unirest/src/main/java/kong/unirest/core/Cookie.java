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
 * Represents a cookie parsed from the set-cookie header
 * per https://tools.ietf.org/html/rfc6265
 *
 * note that the RFC is awful.
 * The wikipedia article is far easier to understand https://en.wikipedia.org/wiki/HTTP_cookie
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

    public Cookie(String name, String value){
        this.name = name;
        this.value = value;
    }

    /**
     * Construct a cookie from a set-cookie value
     * @param v cookie string value
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

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public boolean isPartitioned() {
        return this.partitioned;
    }

    public void setPartitioned(boolean partitionedFlag) {
        this.partitioned = partitionedFlag;
    }

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

    public enum SameSite {
        None, Strict, Lax;

        private static EnumSet<SameSite> all = EnumSet.allOf(SameSite.class);

        public static SameSite parse(String value) {
            return all.stream()
                    .filter(e -> e.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(null);
        }
    }
}
