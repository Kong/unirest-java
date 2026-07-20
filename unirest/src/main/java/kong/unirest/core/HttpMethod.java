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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents an HTTP method (verb) such as GET, POST, or PUT.
 * <p>
 * Unlike a fixed enum, instances are interned in an internal registry, so custom
 * or non-standard verbs can be created and reused via {@link #valueOf(String)}.
 * Standard verbs are provided as constants for convenience.
 * </p>
 */
public class HttpMethod {
	private static final Map<String, HttpMethod> REGISTRY = new HashMap<>();

	/** The HTTP GET method. */
	public static final HttpMethod GET = valueOf("GET");
	/** The HTTP POST method. */
	public static final HttpMethod POST = valueOf("POST");
	/** The HTTP QUERY method. */
	public static final HttpMethod QUERY = valueOf("QUERY");
	/** The HTTP PUT method. */
	public static final HttpMethod PUT = valueOf("PUT");
	/** The HTTP DELETE method. */
	public static final HttpMethod DELETE = valueOf("DELETE");
	/** The HTTP PATCH method. */
	public static final HttpMethod PATCH = valueOf("PATCH");
	/** The HTTP HEAD method. */
	public static final HttpMethod HEAD = valueOf("HEAD");
	/** The HTTP OPTIONS method. */
	public static final HttpMethod OPTIONS = valueOf("OPTIONS");
	/** The HTTP TRACE method. */
	public static final HttpMethod TRACE = valueOf("TRACE");
	/** The pseudo-method used for WebSocket connections. */
	public static final HttpMethod WEBSOCKET = valueOf("WEBSOCKET");

	private final String name;

	private HttpMethod(String name){
		this.name = name;
	}

	/**
	 * Return the interned {@link HttpMethod} for the given verb, creating it if necessary.
	 * The verb is normalized to upper case, so lookups are case-insensitive.
	 * @param verb the HTTP method name
	 * @return the corresponding HttpMethod instance
	 */
	public static HttpMethod valueOf(String verb){
		return REGISTRY.computeIfAbsent(String.valueOf(verb).toUpperCase(), HttpMethod::new);
	}

	/**
	 * Return all HttpMethod instances currently registered.
	 * @return a set of all known HttpMethods
	 */
	public Set<HttpMethod> all(){
		return new HashSet<>(REGISTRY.values());
	}

	/**
	 * Return the name of this HTTP method.
	 * @return the method name in upper case
	 */
	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
