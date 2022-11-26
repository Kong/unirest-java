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

import java.util.*;

class Routes implements Assert {
    private final String path;
    private final HttpMethod method;
    private final List<Invocation> invokes = new ArrayList<>();


    Routes(HttpRequest request) {
        Path p = new Path(request.getUrl());
        this.method = request.getHttpMethod();
        this.path = p.baseUrl();
        invokes.add(new Invocation(this, request));
    }

    public Routes(HttpMethod method, Path p) {
        this.method = method;
        this.path = p.baseUrl();
    }

    Expectation newExpectation() {
        Invocation inv = new Invocation(this);
        invokes.add(inv);
        return inv;
    }

    boolean matches(HttpRequest request) {
        Path p = new Path(request.getUrl());
        return this.method.equals(request.getHttpMethod())
                && this.path == null || this.path.equalsIgnoreCase(p.baseUrl());
    }

    RawResponse exchange(HttpRequest request, Config config) {
        return getBestMatch(request)
                .map(invocation -> invocation.getResponse(config, request))
                .orElseGet(() -> {
                    Invocation i = new Invocation(this);
                    i.log(request);
                    invokes.add(i);
                    return i.getResponse(config, request);
                });
    }

    boolean matches(HttpMethod httpMethod, Path url) {
        return this.method.equals(httpMethod)
                && this.path.equals(url.baseUrl());
    }

    private Optional<Invocation> getBestMatch(HttpRequest request) {
        Optional<Invocation> i = getBestMatch(request, true);
        if(i.isPresent()){
            return i;
        }
        return getBestMatch(request, false);
    }

    private Optional<Invocation> getBestMatch(HttpRequest request, boolean expected) {
        Map<Integer, Invocation> map = new TreeMap<>();
        invokes.stream()
                .forEach(i -> {
                    Integer score = i.scoreMatch(request);
                    if(score >= 0) {
                        map.put(score, i);
                    }
                });
        if (map.size() == 0) {
            return Optional.empty();
        }
        Invocation value = map.get(Collections.max(map.keySet()));
        value.log(request);
        return Optional.of(value);
    }

    @Override
    public Assert hadHeader(String key, String value) {
        if (invokes.stream().noneMatch(i -> i.hasExpectedHeader(key, value))) {
            throw new UnirestAssertion(
                    "No invocation found with header [%s: %s]\nFound:\n%s",
                    key, value, allHeaders());
        }
        return this;
    }

    @Override
    public Assert hadBody(String expected) {
        if(invokes.stream().noneMatch(i -> i.hasBody(expected))){
            throw new UnirestAssertion(
                    "No invocation found with body");
        }
        return this;
    }

    @Override
    public Assert hadField(String name, String value) {
        if(invokes.stream().noneMatch(i -> i.hasField(name, value))){
            throw new UnirestAssertion(
                    "No invocation found with body");
        }
        return this;
    }

    private Headers allHeaders() {
        return invokes.stream()
                .flatMap(i -> i.getRequests().stream())
                .map(HttpRequest::getHeaders)
                .reduce((l, r) -> {
                    l.putAll(r);
                    return l;
                }).orElseGet(Headers::new);
    }

    @Override
    public Assert wasInvokedTimes(int i) {
        Integer sum = sumInvokes();
        if (sum != i) {
            throw new UnirestAssertion(
                    "Incorrect number of invocations. Expected %s got %s\n%s %s",
                    i, sum, method, path);
        }
        return this;
    }

    private Integer sumInvokes() {
        return invokes.stream()
                .map(Invocation::requestSize)
                .reduce(0, Integer::sum);
    }

    @Override
    public Assert verifyAll() {
        invokes.forEach(Invocation::verify);
        return this;
    }


    HttpMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    void addInvoke(Invocation invocation) {
        if(invocation != null){
            invokes.add(new Invocation(this, invocation));
        }
    }
}
