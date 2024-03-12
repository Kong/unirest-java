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

import java.util.function.Function;
import java.util.function.Supplier;

class ExpectedResponseRecord implements ExpectedResponse, ResponseBuilder {
    private Expectation expectation;
    private Function<ObjectMapper, String> response = o -> null;
    private Headers responseHeaders = new Headers();
    private int responseStatus = 200;
    private String responseText = "Ok";

    ExpectedResponseRecord(Expectation expectation){
        this.expectation = expectation;
    }

    @Override
    public ExpectedResponse withHeader(String key, String value) {
        this.responseHeaders.add(key, value);
        return this;
    }

    @Override
    public ExpectedResponse withHeaders(Headers value) {
        value.all().forEach(h -> withHeader(h.getName(), h.getValue()));
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

    @Override
    public ExpectedResponse thenReturn(String body) {
        this.response = o -> body;
        return this;
    }

    @Override
    public ExpectedResponse thenReturn(JSONElement jsonObject) {
        this.response = o -> jsonObject.toString();
        return this;
    }

    @Override
    public ExpectedResponse thenReturn(Supplier<String> supplier){
        this.response = o -> supplier.get();
        return this;
    }

    @Override
    public void verify() {
        verify(null);
    }

    @Override
    public void verify(Times times) {
        if(expectation == null){
            throw new UnirestAssertion("A expectation was never invoked!");
        }
        expectation.verify(times);
    }

    @Override
    public ExpectedResponse thenReturn(Object pojo) {
        if(pojo instanceof MockResponse){
            var res = (MockResponse)pojo;
            return thenReturn(res);
        } else {
            this.response = o -> o.writeValue(pojo);
        }
        return this;
    }

    private ExpectedResponse thenReturn(MockResponse res) {
        this.response = o -> res.getBody() == null ? null : String.valueOf(res.getBody());
        return withStatus(res.getStatus(), res.getStatusText())
                .withHeaders(res.getHeaders());
    }

    public RawResponse toRawResponse(Config config, HttpRequest request) {
        return new MockRawResponse(response.apply(getObjectMapper(request, config)),
                responseHeaders, responseStatus, responseText, config, request.toSummary());
    }

    private ObjectMapper getObjectMapper(HttpRequest request, Config config) {
        return Util.tryCast(request, BaseRequest.class)
                .map(BaseRequest::getObjectMapper)
                .orElseGet(() -> config.getObjectMapper());
    }

    void setExpectation(Expectation invocation) {
        this.expectation = invocation;
    }
}
