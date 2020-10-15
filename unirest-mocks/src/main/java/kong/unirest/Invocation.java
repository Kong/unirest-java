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
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.System.lineSeparator;

class Invocation implements Expectation, ExpectedResponse {
    private Routes routes;
    private String response;
    private Headers expectedHeaders = new Headers();
    private Headers expectedQueryParams = new Headers();
    private List<HttpRequest> requests = new ArrayList<>();
    private Headers responseHeaders = new Headers();
    private Boolean expected = false;
    private String expectedBody;
    private int responseStatus = 200;
    private String responseText = "Ok";

    public Invocation(Routes routes){
        this.routes = routes;
        this.expected = true;
    }

    public Invocation(Routes routes, HttpRequest request) {
        this.routes = routes;
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

    RawResponse getResponse(Config config) {
        return new MockRawResponse(response, responseHeaders, responseStatus, responseText, config);
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
        expectedBody = body;
        return this;
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
            sb.append("\t" + expectedBody);
        }
        return sb.toString();
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

    @Override
    public ExpectedResponse withStatus(int httpStatus) {
        return withStatus(httpStatus,"");
    }

    @Override
    public ExpectedResponse withStatus(int httpStatus, String statusMessage) {
        this.responseStatus = httpStatus;
        this.responseText = statusMessage;
        return this;
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
            return tryCast(request, HttpRequestUniBody.class)
                    .map(HttpRequestUniBody::getBody)
                    .flatMap(b -> b)
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
            } else if(String.class.isAssignableFrom(bodyPart.getPartType())
            && expectedBody.equalsIgnoreCase((String) bodyPart.getValue())){
                return 1;
            } else if (expectedBody != null) {
                return -1000;
            }
        }
        return 0;
    }

    private URIBuilder geturi(HttpRequest request) {
        try {
            URIBuilder b = new URIBuilder(request.getUrl());
            return b;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
            URIBuilder p = geturi(request);
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
