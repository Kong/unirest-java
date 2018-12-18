/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NByteArrayEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static unirest.HttpMethod.*;

class RequestPrep {
    private static final String CONTENT_TYPE = "content-type";
    private static final String ACCEPT_ENCODING_HEADER = "accept-encoding";
    private static final String USER_AGENT_HEADER = "user-agent";
    private static final String USER_AGENT = "unirest-java/3.0.00";
    private static final Map<HttpMethod, Function<String, HttpRequestBase>> FACTORIES;
    private final HttpRequest request;
    private final boolean async;

    static {
        FACTORIES = new HashMap<>();
        FACTORIES.put(GET, HttpGet::new);
        FACTORIES.put(POST, HttpPost::new);
        FACTORIES.put(PUT, HttpPut::new);
        FACTORIES.put(DELETE, HttpDeleteWithBody::new);
        FACTORIES.put(PATCH, HttpPatchWithBody::new);
        FACTORIES.put(OPTIONS, HttpOptions::new);
        FACTORIES.put(HEAD, HttpHead::new);
    }

    RequestPrep(HttpRequest request, boolean async){
        this.request = request;
        this.async = async;
    }

    HttpRequestBase prepare(){
        HttpRequestBase reqObj = getHttpRequestBase();

        setBody(reqObj);

        return reqObj;
    }

    private HttpRequestBase getHttpRequestBase() {
        if (!request.getHeaders().containsKey(USER_AGENT_HEADER)) {
            request.header(USER_AGENT_HEADER, USER_AGENT);
        }
        if (!request.getHeaders().containsKey(ACCEPT_ENCODING_HEADER)) {
            request.header(ACCEPT_ENCODING_HEADER, "gzip");
        }

        try {
            HttpRequestBase reqObj = FACTORIES.get(request.getHttpMethod()).apply(request.getUrl());
            request.getHeaders().stream().map(this::toEntries).forEach(reqObj::addHeader);
            return reqObj;
        }catch (RuntimeException e){
            throw new UnirestException(e);
        }
    }


    private Header toEntries(Headers.Entry k) {
        return new BasicHeader(k.getName(), k.getValue());
    }

    private void setBody(HttpRequestBase reqObj) {
        if (!(request.getHttpMethod() == GET || request.getHttpMethod() == HttpMethod.HEAD)) {
            if (request.getBody() != null) {
                HttpEntity entity = request.getBody().getEntity();
                if (async) {
                    if (reqObj.getHeaders(CONTENT_TYPE) == null || reqObj.getHeaders(CONTENT_TYPE).length == 0) {
                        reqObj.setHeader(entity.getContentType());
                    }
                    try {
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        entity.writeTo(output);
                        NByteArrayEntity en = new NByteArrayEntity(output.toByteArray());
                        ((HttpEntityEnclosingRequestBase) reqObj).setEntity(en);
                    } catch (IOException e) {
                        throw new UnirestException(e);
                    }
                } else {
                    ((HttpEntityEnclosingRequestBase) reqObj).setEntity(entity);
                }
            }
        }
    }
}
