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

import kong.unirest.json.JSONElement;

import java.util.ArrayList;
import java.util.List;

class Invocation implements Expectation, ExpectedResponse {
    private Paths paths;
    private String response;
    private Headers expectedHeaders = new Headers();
    private List<HttpRequest> requests = new ArrayList<>();
    private Headers responseHeaders = new Headers();

    public Invocation(Paths paths){
        this.paths = paths;
    }

    public Invocation(Paths paths, HttpRequest request) {
        this.paths = paths;
        this.expectedHeaders = request.getHeaders();
    }

    @Override
    public ExpectedResponse thenReturn(String body) {
        this.response = body;
        return this;
    }

    @Override
    public ExpectedResponse thenReturn(JSONElement jsonObject) {
        this.response = jsonObject.toString();
        return this;
    }

    @Override
    public ExpectedResponse thenReturn(Object pojo) {
        this.response = new JsonObjectMapper().writeValue(pojo);
        return this;
    }

    RawResponse getResponse() {
        return new MockRawResponse(response, responseHeaders);
    }

    private Headers allHeaders() {
        return requests.stream()
                .map(i -> i.getHeaders())
                .reduce((h1, h2) -> {
                    h1.putAll(h2);
                    return h1;
                })
                .orElseGet(Headers::new);
    }

    public void log(HttpRequest request) {
        this.requests.add(request);
    }

    @Override
    public void andHeader(String key, String value) {
        expectedHeaders.add(key, value);
    }

    @Override
    public ExpectedResponse thenReturn() {
        return this;
    }

    public void verify() {
        if (requests.isEmpty()) {
            throw new UnirestAssertion("A expectation was never invoked! %s", details());
        }
    }

    private String details() {
        return String.format("%s %s\nHeaders:\n%s", paths.getMethod(), paths.getPath(), expectedHeaders);
    }


    public boolean hasExpectedHeader(String key, String value) {
        Headers allH = allHeaders();
        return allH.containsKey(key) && allH.get(key).contains(value);
    }

    public Integer requestSize() {
        return requests.size();
    }

    public List<HttpRequest> getRequests() {
        return requests;
    }

    @Override
    public ExpectedResponse withHeader(String key, String value) {
        this.responseHeaders.add(key, value);
        return this;
    }
}
