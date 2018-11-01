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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.util.Locale;
import java.util.stream.Collectors;

public class MockApacheResponse implements HttpResponse {
    private Multimap<String, String> headers = HashMultimap.create();
    private StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("",1,1), 0, "");
    private BasicHttpEntity entity = make("");

    @Override
    public StatusLine getStatusLine() {
        return statusLine;
    }

    @Override
    public void setStatusLine(StatusLine statusline) {
        this.statusLine = statusline;
    }

    @Override
    public void setStatusLine(ProtocolVersion ver, int code) {
        throw new NotImplimented();
    }

    @Override
    public void setStatusLine(ProtocolVersion ver, int code, String reason) {
        throw new NotImplimented();
    }

    @Override
    public void setStatusCode(int code) throws IllegalStateException {
        throw new NotImplimented();
    }

    @Override
    public void setReasonPhrase(String reason) throws IllegalStateException {
        throw new NotImplimented();
    }

    @Override
    public HttpEntity getEntity() {
        return entity;
    }

    @Override
    public void setEntity(HttpEntity entity) {
        throw new NotImplimented();
    }

    @Override
    public Locale getLocale() {
        throw new NotImplimented();
    }

    @Override
    public void setLocale(Locale loc) {
        throw new NotImplimented();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        throw new NotImplimented();
    }

    @Override
    public boolean containsHeader(String name) {
        throw new NotImplimented();
    }

    @Override
    public Header[] getHeaders(String name) {
        throw new NotImplimented();
    }

    @Override
    public Header getFirstHeader(String name) {
        throw new NotImplimented();
    }

    @Override
    public Header getLastHeader(String name) {
        throw new NotImplimented();
    }

    @Override
    public Header[] getAllHeaders() {
        return headers.entries().stream()
                .map(e -> new BasicHeader(e.getKey(), e.getValue()))
                .collect(Collectors.toList())
                .toArray(new Header[headers.values().size()]);
    }

    @Override
    public void addHeader(Header header) {
        throw new NotImplimented();
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void setHeader(Header header) {
        throw new NotImplimented();
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void setHeaders(Header[] headers) {
        throw new NotImplimented();
    }

    @Override
    public void removeHeader(Header header) {
        throw new NotImplimented();
    }

    @Override
    public void removeHeaders(String name) {
        throw new NotImplimented();
    }

    @Override
    public HeaderIterator headerIterator() {
        throw new NotImplimented();
    }

    @Override
    public HeaderIterator headerIterator(String name) {
        throw new NotImplimented();
    }

    @Override
    public HttpParams getParams() {
        throw new NotImplimented();
    }

    @Override
    public void setParams(HttpParams params) {
        throw new NotImplimented();
    }

    public void setBody(String body) {
        this.entity = make(body);
    }

    public static BasicHttpEntity make(String body){
        BasicHttpEntity e = new BasicHttpEntity();
        e.setContent(new ByteArrayInputStream(body.getBytes()));
        return e;
    }
}
