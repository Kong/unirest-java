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

package kong.unirest;


/**
 * Standard cookie specifications supported by Unirest.
 */
public final class CookieSpecs {

    /**
     * The default policy. This policy provides a higher degree of compatibility
     * with common cookie management of popular HTTP agents for non-standard
     * (Netscape style) cookies.
     */
    public static final String DEFAULT = "default";

    /**
     * The Netscape cookie draft compliant policy.
     */
    public static final String NETSCAPE = "netscape";

    /**
     * The RFC 6265 compliant policy (interoprability profile).
     */
    public static final String STANDARD = "standard";

    /**
     * The RFC 6265 compliant policy (strict profile)
     */
    public static final String STANDARD_STRICT = "standard-strict";

    /**
     * The policy that ignores cookies.
     */
    public static final String IGNORE_COOKIES = "ignoreCookies";
}
