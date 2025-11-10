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

package kong.unirest.core.java;

import kong.unirest.core.ContentType;
import kong.unirest.core.HttpMethod;
import kong.unirest.core.SseRequest;

import java.net.URI;
import java.net.http.HttpRequest;

public class SseRequestBuilder {
    static java.net.http.HttpRequest getHttpRequest(SseRequest request) {
        var a = HttpRequest
                .newBuilder()
                .header("Accept", ContentType.EVENT_STREAMS.getMimeType())
                .uri(URI.create(request.getUrl()));

        var accept = addMethod(request, a);

        request.getHeaders().all().forEach(h -> {
            accept.header(h.getName(), h.getValue());
        });


        return accept.build();
    }

    private static HttpRequest.Builder addMethod(SseRequest request, HttpRequest.Builder a) {
        if(request.getMethod().equals(HttpMethod.GET)){
            return a.GET();
        } else {
            return a.method(request.getMethod().toString(), HttpRequest.BodyPublishers.noBody());
        }
    }


}
