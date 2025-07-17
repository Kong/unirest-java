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

import kong.unirest.core.java.Event;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class SseRequestImpl implements SseRequest {
    private final Config config;
    private final Path url;
    protected Headers headers = new Headers();



    public SseRequestImpl(Config config, String url) {
        Objects.requireNonNull(config, "Config cannot be null");
        Objects.requireNonNull(url,    "URL cannot be null");

        this.config = config;
        this.url = new Path(url, config.getDefaultBaseUrl());
        headers.putAll(config.getDefaultHeaders());
    }

    @Override
    public SseRequest routeParam(String name, String value) {
        url.param(name, value);
        return this;
    }

    @Override
    public SseRequest routeParam(Map<String, Object> params) {
        url.param(params);
        return this;
    }

    @Override
    public SseRequest basicAuth(String username, String password) {
        headers.setBasicAuth(username, password);
        return this;
    }

    @Override
    public SseRequest accept(String value) {
        headers.accepts(value);
        return this;
    }

    @Override
    public SseRequest header(String name, String value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public SseRequest headerReplace(String name, String value) {
        headers.replace(name, value);
        return this;
    }

    @Override
    public SseRequest headers(Map<String, String> headerMap) {
        headers.add(headerMap);
        return this;
    }

    @Override
    public SseRequest cookie(String name, String value) {
        headers.cookie(new Cookie(name, value));
        return this;
    }

    @Override
    public SseRequest cookie(Cookie cookie) {
        headers.cookie(cookie);
        return this;
    }

    @Override
    public SseRequest cookie(Collection<Cookie> cookies) {
        headers.cookie(cookies);
        return this;
    }

    @Override
    public SseRequest queryString(String name, Object value) {
        url.queryString(name, value);
        return this;
    }

    @Override
    public SseRequest queryString(String name, Collection<?> value) {
        url.queryString(name, value);
        return this;
    }

    @Override
    public SseRequest queryString(Map<String, Object> parameters) {
        url.queryString(parameters);
        return this;
    }

    @Override
    public SseRequest lastEventId(String id) {
        return header("Last-Event-ID", id);
    }

    @Override
    public CompletableFuture<Void> connect(SseHandler handler) {
        return config.getClient().sse(this, handler);
    }

    @Override
    public Stream<Event> connect() {
        return config.getClient().sse(this);
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public String getUrl() {
        return url.toString();
    }
}
