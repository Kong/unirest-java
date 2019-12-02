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

import java.util.ArrayList;
import java.util.List;

class Paths implements Assert  {
    private final String path;
    private final HttpMethod method;
    private final List<Invocation> invokes = new ArrayList<>();

    Paths(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    Paths(HttpRequest request) {
        this.method = request.getHttpMethod();
        this.path = request.getUrl();
        invokes.add(new Invocation(this, request));
    }

    Expectation newExpectation() {
        Invocation inv = new Invocation(this);
        invokes.add(inv);
        return inv;
    }

    boolean matches(HttpRequest request) {
        return this.method.equals(request.getHttpMethod())
                && this.path.equalsIgnoreCase(request.getUrl());
    }

    RawResponse exchange(HttpRequest request) {
                return invokes.stream()
                        .findFirst()
                        .map(e -> {
                            e.log(request);
                            return e.getResponse();
                        })
                .orElseGet(() -> {
                    Invocation i = new Invocation(this);
                    i.log(request);
                    invokes.add(i);
                    return i.getResponse();
                });
    }

    boolean matches(HttpMethod httpMethod, String url) {
        return this.method.equals(httpMethod)
                && this.path.equals(url);
    }

    @Override
    public void assertHeader(String key, String value) {
        if(invokes.stream().noneMatch(i -> i.hasExpectedHeader(key, value))){
            throw new UnirestAssertion(
                        "No invocation found with header [%s: %s]\nFound:\n%s",
                            key, value, allHeaders());
        }
    }

    private Headers allHeaders() {
        return invokes.stream()
                .flatMap(i -> i.getRequests().stream())
                .map(HttpRequest::getHeaders)
                .reduce((l,r) -> {
                    l.putAll(r);
                    return l;
                }).orElseGet(Headers::new);
    }

    @Override
    public void assertInvokedTimes(int i) {
        Integer sum = sumInvokes();
        if(sum != i){
             throw new UnirestAssertion(
                     "Incorrect number of invocations. Expected %s got %s\n%s %s",
                    i, sum, method, path);
         }
    }

    private Integer sumInvokes() {
        return invokes.stream()
                .map(Invocation::requestSize)
                .reduce(0, Integer::sum);
    }

    @Override
    public void verifyAll() {
        invokes.forEach(Invocation::verify);
    }

    HttpMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }
}
