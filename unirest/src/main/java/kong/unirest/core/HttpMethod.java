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
 * Represents an HTTP request method.
 * <p>
 * This class provides constants for standard HTTP methods (GET, POST, PUT, DELETE, etc.)
 * and supports custom method names through the {@link #valueOf(String)} factory method.
 * Method instances are cached and reused, ensuring that the same method name always
 * returns the same instance.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods">HTTP request methods (MDN)</a>
 */
public class HttpMethod {
	private static final Map<String, HttpMethod> REGISTRY = new HashMap<>();

	/** The HTTP GET method requests a representation of the specified resource. */
	public static final HttpMethod GET = valueOf("GET");

	/** The HTTP POST method sends data to the server to create a resource. */
	public static final HttpMethod POST = valueOf("POST");

	/** The HTTP PUT method replaces all current representations of the target resource. */
	public static final HttpMethod PUT = valueOf("PUT");

	/** The HTTP DELETE method deletes the specified resource. */
	public static final HttpMethod DELETE = valueOf("DELETE");

	/** The HTTP PATCH method applies partial modifications to a resource. */
	public static final HttpMethod PATCH = valueOf("PATCH");

	/** The HTTP HEAD method requests headers identical to GET, but without the response body. */
	public static final HttpMethod HEAD = valueOf("HEAD");

	/** The HTTP OPTIONS method describes the communication options for the target resource. */
	public static final HttpMethod OPTIONS = valueOf("OPTIONS");

	/** The HTTP TRACE method performs a message loop-back test along the path to the target resource. */
	public static final HttpMethod TRACE = valueOf("TRACE");

	/** Represents a WebSocket connection upgrade request. */
	public static final HttpMethod WEBSOCKET = valueOf("WEBSOCKET");

	private final String name;

	private HttpMethod(String name){
		this.name = name;
	}

	/**
	 * Returns an HttpMethod instance for the specified method name.
	 * <p>
	 * Method names are case-insensitive and will be converted to uppercase.
	 * Instances are cached, so calling this method multiple times with the
	 * same name returns the same instance.
	 *
	 * @param verb the HTTP method name (e.g., "GET", "POST", "CUSTOM")
	 * @return the HttpMethod instance for the specified name
	 */
	public static HttpMethod valueOf(String verb){
		return REGISTRY.computeIfAbsent(String.valueOf(verb).toUpperCase(), HttpMethod::new);
	}

	/**
	 * Returns a set of all registered HTTP methods.
	 *
	 * @return a new {@link Set} containing all HttpMethod instances that have been created
	 */
	public Set<HttpMethod> all(){
		return new HashSet<>(REGISTRY.values());
	}

	/**
	 * Returns the name of this HTTP method.
	 *
	 * @return the method name in uppercase (e.g., "GET", "POST")
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the string representation of this HTTP method.
	 *
	 * @return the method name
	 */
	@Override
	public String toString() {
		return name;
	}
}
