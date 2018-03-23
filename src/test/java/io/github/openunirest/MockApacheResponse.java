package io.github.openunirest;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.http.*;
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
