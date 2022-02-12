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

import java.net.http.WebSocket;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class WebSocketRequestImpl implements WebSocketRequest {
    private Instant creation = Util.now();
    private Optional<ObjectMapper> objectMapper = Optional.empty();
    private String responseEncoding;
    protected Headers headers = new Headers();
    protected final Config config;
    protected HttpMethod method;
    protected Path url;
    private Integer connectTimeout;

    public WebSocketRequestImpl(Config config, String url) {
        this.config = config;
        this.url = new Path(url, config.getDefaultBaseUrl());
        headers.putAll(config.getDefaultHeaders());
    }

    @Override
    public WebSocketRequest routeParam(String name, String value) {
        url.param(name, value);
        return this;
    }

    @Override
    public WebSocketRequest routeParam(Map<String, Object> params) {
        url.param(params);
        return this;
    }

    @Override
    public WebSocketRequest basicAuth(String username, String password) {
        headers.setBasicAuth(username, password);
        return this;
    }

    @Override
    public WebSocketRequest accept(String value) {
        headers.accepts(value);
        return this;
    }

    @Override
    public WebSocketRequest responseEncoding(String encoding) {
        this.responseEncoding = encoding;
        return this;
    }

    @Override
    public WebSocketRequest header(String name, String value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public WebSocketRequest headerReplace(String name, String value) {
        headers.replace(name, value);
        return this;
    }

    @Override
    public WebSocketRequest headers(Map<String, String> headerMap) {
        headers.add(headerMap);
        return this;
    }

    @Override
    public WebSocketRequest cookie(String name, String value) {
        headers.cookie(new Cookie(name, value));
        return this;
    }

    @Override
    public WebSocketRequest cookie(Cookie cookie) {
        headers.cookie(cookie);
        return this;
    }

    @Override
    public WebSocketRequest cookie(Collection<Cookie> cookies) {
        headers.cookie(cookies);
        return this;
    }

    @Override
    public WebSocketRequest queryString(String name, Object value) {
        url.queryString(name, value);
        return this;
    }

    @Override
    public WebSocketRequest queryString(String name, Collection<?> value) {
        url.queryString(name, value);
        return this;
    }

    @Override
    public WebSocketRequest queryString(Map<String, Object> parameters) {
        url.queryString(parameters);
        return this;
    }

    @Override
    public WebSocketResponse connect(WebSocket.Listener listener) {
        return config.getClient().websocket(this, listener);
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
