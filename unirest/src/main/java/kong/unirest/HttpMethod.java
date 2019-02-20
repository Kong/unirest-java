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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HttpMethod {
	private static final Map<String, HttpMethod> REGISTRY = new HashMap<>();

	public static final HttpMethod GET = valueOf("GET");
	public static final HttpMethod POST = valueOf("POST");
	public static final HttpMethod PUT = valueOf("PUT");
	public static final HttpMethod DELETE = valueOf("DELETE");
	public static final HttpMethod PATCH = valueOf("PATCH");
	public static final HttpMethod HEAD = valueOf("HEAD");
	public static final HttpMethod OPTIONS = valueOf("OPTIONS");
	public static final HttpMethod TRACE = valueOf("TRACE");

	private final String name;

	private HttpMethod(String name){
		this.name = name;
	}

	public static HttpMethod valueOf(String verb){
		return REGISTRY.computeIfAbsent(verb, HttpMethod::new);
	}

	public Set<HttpMethod> all(){
		return new HashSet<>(REGISTRY.values());
	}

	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
