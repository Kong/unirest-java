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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SseRequest {
    /**
     * add a route param that replaces the matching {name}
     * For example routeParam("name", "fred") will replace {name} in
     * https://localhost/users/{user}
     * to
     * https://localhost/users/fred
     *
     * @param name the name of the param (do not include curly braces {}
     * @param value the value to replace the placeholder with
     * @return this request builder
     */
    SseRequest routeParam(String name, String value);

    /**
     * add a route param map that replaces the matching {name}
     * For example routeParam(Map.of("name", "fred")) will replace {name} in
     * https://localhost/users/{user}
     * to
     * https://localhost/users/fred
     *
     * @param params a map of path params
     * @return this request builder
     */
    SseRequest routeParam(Map<String, Object> params);

    /**
     * Basic auth credentials
     * @param username the username
     * @param password the password
     * @return this request builder
     */
    SseRequest basicAuth(String username, String password);

    /**
     * The Accept header to send (e.g. application/json
     * @param value a valid mime type for the Accept header
     * @return this request builder
     */
    SseRequest accept(String value);

    /**
     * Add a http header, HTTP supports multiple of the same header. This will continue to append new values
     * @param name name of the header
     * @param value value for the header
     * @return this request builder
     */
    SseRequest header(String name, String value);

    /**
     * Replace a header value or add it if it doesn't exist
     * @param name name of the header
     * @param value value for the header
     * @return this request builder
     */
    SseRequest headerReplace(String name, String value);

    /**
     * Add headers as a map
     * @param headerMap a map of headers
     * @return this request builder
     */
    SseRequest headers(Map<String, String> headerMap);

    /**
     * Add a simple cookie header
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @return this request builder
     */
    SseRequest cookie(String name, String value);

    /**
     * Add a simple cookie header
     * @param cookie a cookie
     * @return this request builder
     */
    SseRequest cookie(Cookie cookie);

    /**
     * Add a collection of cookie headers
     * @param cookies a cookie
     * @return this request builder
     */
    SseRequest cookie(Collection<Cookie> cookies);

    /**
     * add a query param to the url. The value will be URL-Encoded
     * @param name the name of the param
     * @param value the value of the param
     * @return this request builder
     */
    SseRequest queryString(String name, Object value);

    /**
     * Add multiple param with the same param name.
     * queryString("name", Arrays.asList("bob", "linda")) will result in
     * ?name=bob&amp;name=linda
     * @param name the name of the param
     * @param value a collection of values
     * @return this request builder
     */
    SseRequest queryString(String name, Collection<?> value);

    /**
     * Add query params as a map of name value pairs
     * @param parameters a map of params
     * @return this request builder
     */
    SseRequest queryString(Map<String, Object> parameters);

    Headers getHeaders();

    String getUrl();

    CompletableFuture<Void> connect(SseListener listener);
}
