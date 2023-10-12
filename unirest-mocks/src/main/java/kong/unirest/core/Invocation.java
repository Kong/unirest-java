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

import kong.unirest.core.json.JSONElement;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;

class Invocation implements Expectation {
    private Routes routes;
    private List<HttpRequest> requests = new ArrayList<>();
    private Headers expectedHeaders = new Headers();
    private Headers expectedQueryParams = new Headers();
    private Boolean expected = false;
    private BodyMatcher expectedBody;
    private MatchStatus expectedBodyStatus;
    private ExpectedResponseRecord expectedResponse = new ExpectedResponseRecord();
    private Function<HttpRequest<?>, ExpectedResponse> functionalResponse = r -> expectedResponse;

    public Invocation(Routes routes){
        this.routes = routes;
        this.expected = true;
    }

    public Invocation(Routes routes, HttpRequest request) {
        this.routes = routes;
        this.expectedHeaders = request.getHeaders();
    }

    Invocation(Routes routes, Invocation other) {
        this.routes = routes;
        this.expectedResponse = other.expectedResponse;
    }

    Invocation() {

    }

    @Override
    public ExpectedResponse thenReturn(String body) {
        return expectedResponse.thenReturn(body);
    }

    @Override
    public ExpectedResponse thenReturn(JSONElement jsonObject) {
        return expectedResponse.thenReturn(jsonObject);
    }

    @Override
    public ExpectedResponse thenReturn(Object pojo) {
        return expectedResponse.thenReturn(pojo);
    }

    @Override
    public ExpectedResponse thenReturn(Supplier<String> supplier) {
        return expectedResponse.thenReturn(supplier);
    }

    @Override
    public void thenReturn(Function<HttpRequest<?>, ExpectedResponse> fun) {
        this.functionalResponse = fun;
    }

    RawResponse getResponse(Config config, HttpRequest request) {
        return tryCast(functionalResponse.apply(request), ExpectedResponseRecord.class)
                .map(e -> e.toRawResponse(config, request))
                .orElseThrow(() -> new UnirestException("No Result Configured For Response"));
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
    public Expectation header(String key, String value) {
        expectedHeaders.add(key, value);
        return this;
    }

    @Override
    public Expectation queryString(String key, String value) {
        expectedQueryParams.add(key, value);
        return this;
    }

    @Override
    public Expectation body(String body) {
        expectedBody = new EqualsBodyMatcher(body);
        return this;
    }

    @Override
    public Expectation body(BodyMatcher matcher) {
        expectedBody = matcher;
        return this;
    }

    @Override
    public ExpectedResponse thenReturn() {
        return expectedResponse;
    }

    public void verify() {
        if (requests.isEmpty()) {
            throw new UnirestAssertion("A expectation was never invoked! %s", details());
        }
    }

    private String details() {
        StringBuilder sb  = new StringBuilder(routes.getMethod().name())
                .append(" ")
                .append(routes.getPath()).append(lineSeparator());
        if(expectedHeaders.size() > 0){
            sb.append("Headers:\n");
            sb.append(expectedHeaders);
        }
        if(expectedQueryParams.size() > 0){
            sb.append("Params:\n");
            sb.append(expectedQueryParams);
        }
        if(expectedBody != null){
            sb.append("Body:\n");
            if(expectedBodyStatus != null) {
                sb.append("\t" + expectedBodyStatus.getDescription());
            } else {
                sb.append("\t null");
            }
        }
        return sb.toString();
    }


    public boolean hasExpectedHeader(String key, String value) {
        Headers allH = allHeaders();
        return allH.containsKey(key) && allH.get(key).contains(value);
    }

    private Stream<Body> getBodyStream() {
        return requests.stream()
                .filter(r -> r.getBody().isPresent())
                .map(r -> (Body) r.getBody().get());
    }

    public boolean hasBody(String body){
        return getBodyStream()
                .anyMatch(b -> uniBodyMatches(body, b));
    }

    private boolean uniBodyMatches(String body, Body o) {
        return tryCast(o, HttpRequestUniBody.class)
                .map(h -> h.uniPart())
                .map(u -> u.getValue().equals(body))
                .orElse(false);
    }

    public boolean hasField(String name, String value) {
        return getBodyStream()
                .anyMatch(b -> hasField(name, value, b));
    }

    private boolean hasField(String name, String value, Body b) {
        return tryCast(b, HttpRequestMultiPart.class)
                .map(h -> h.multiParts())
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(part -> part.getName().equals(name) && part.getValue().equals(value));
    }

    public Integer requestSize() {
        return requests.size();
    }

    public List<HttpRequest> getRequests() {
        return requests;
    }

    public Boolean isExpected() {
        return expected;
    }

    public Integer scoreMatch(HttpRequest request) {
        int score = 0;
        score += scoreHeaders(request);
        score += scoreQuery(request);
        score += scoreBody(request);
        return score;
    }

    private int scoreBody(HttpRequest request) {
        if(expectedBody != null){
            return tryCast(request, Body.class)
                    .map(this::matchBody)
                    .orElse(-1000);
        }
        return 0;
    }

    private Integer matchBody(Body b) {
        if(b.isEntityBody()){
            BodyPart bodyPart = b.uniPart();
            if(bodyPart == null && expectedBody == null){
                return 1;
            } else if(String.class.isAssignableFrom(bodyPart.getPartType())){
                expectedBodyStatus = expectedBody.matches(Arrays.asList((String) bodyPart.getValue()));
                if(expectedBodyStatus.isSuccess()){
                    return 1;
                } else {
                    return -1000;
                }
            } else if (expectedBody != null) {
                return -1000;
            }
        } else {
            List<String> parts = b.multiParts().stream().map(p -> p.toString()).collect(Collectors.toList());
            if(parts.isEmpty() && expectedBody == null){
                return 1;
            } else {
                expectedBodyStatus = expectedBody.matches(parts);
                if(expectedBodyStatus.isSuccess()){
                    return 1;
                } else {
                    return -1000;
                }
            }
        }
        return 0;
    }

    private int scoreHeaders(HttpRequest request) {
        if(expectedHeaders.size() > 0){
            long b = expectedHeaders.all().stream().filter(h ->
                    request.getHeaders().get(h.getName()).contains(h.getValue()))
                    .count();

            if(b != expectedHeaders.size()){
                return -1000;
            }
            return Long.valueOf(b).intValue();
        }
        return 0;
    }

    private int scoreQuery(HttpRequest request) {
        if(expectedQueryParams.size() > 0){
            QueryParams p = QueryParams.fromURI(request.getUrl());
            long b = expectedQueryParams.all().stream().filter(h ->
                    p.getQueryParams().stream().anyMatch(q -> q.getName().equalsIgnoreCase(h.getName())
                            && q.getValue().equalsIgnoreCase(h.getValue())))
                    .count();
            if(b != expectedQueryParams.size()){
                return -1000;
            }
            return Long.valueOf(b).intValue();
        }
        return 0;
    }

    private static <T, M extends T> Optional<M> tryCast(T original, Class<M> too) {
        if (original != null && too.isAssignableFrom(original.getClass())) {
            return Optional.of((M) original);
        }
        return Optional.empty();
    }

}
