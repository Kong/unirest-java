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

import kong.unirest.core.*;

import java.io.*;
import java.net.http.HttpResponse;
import java.util.zip.GZIPInputStream;

import static kong.unirest.core.HeaderNames.CONTENT_TYPE;

class JavaResponse extends RawResponseBase {
    private final HttpResponse<InputStream> response;

    public JavaResponse(HttpResponse<InputStream> response, Config config, HttpRequestSummary summary) {
        super(config, summary);
        this.response = response;
    }

    @Override
    public int getStatus() {
        return response.statusCode();
    }

    @Override
    public String getStatusText() {
        return "";
    }

    @Override
    public Headers getHeaders() {
        Headers h = new Headers();
        response.headers().map()
                .entrySet()
                .forEach(e -> {
                    e.getValue().forEach(v -> h.add(e.getKey(), v));
                });
        return h;
    }

    @Override
    public InputStream getContent() {
        try {
            InputStream body = response.body();
            if (isGzipped(getEncoding()) && !(body instanceof GZIPInputStream)) {
                body = new GZIPInputStream(body);
            }
            return body;
        } catch (EOFException e){
            return new ByteArrayInputStream(new byte[]{});
        } catch (Exception e){
            throw new UnirestException(e);
        }
    }

    @Override
    public byte[] getContentAsBytes() {
        if (!hasContent()) {
            return new byte[0];
        }
        try {
            InputStream is = getContent();
            return getBytes(is);
        } catch (IOException e2) {
            throw new UnirestException(e2);
        }
    }

    private static byte[] getBytes(InputStream is) throws IOException {
        try {
            int len;
            int size = 1024;
            byte[] buf;

            if (is instanceof ByteArrayInputStream) {
                size = is.available();
                buf = new byte[size];
                len = is.read(buf, 0, size);
            } else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                buf = new byte[size];
                while ((len = is.read(buf, 0, size)) != -1) {
                    bos.write(buf, 0, len);
                }
                buf = bos.toByteArray();
            }
            return buf;
        } finally {
            is.close();
        }
    }

    private static boolean isGzipped(String value) {
        return "gzip".equalsIgnoreCase(value.toLowerCase().trim());
    }

    @Override
    public String getContentAsString() {
        return getContentAsString(null);
    }

    @Override
    public String getContentAsString(String charset) {
        if (!hasContent()) {
            return "";
        }
        try {
            String charSet = getCharset(charset);
            return new String(getContentAsBytes(), charSet);
        } catch (IOException e) {
            throw new UnirestException(e);
        }
    }

    private String getCharset(String charset) {
        if (charset == null || charset.trim().isEmpty()) {
            return getCharSet();
        }
        return charset;
    }

    @Override
    public InputStreamReader getContentReader() {
        return new InputStreamReader(getContent());
    }

    @Override
    public boolean hasContent() {
        return response.body() != null;
    }

    @Override
    public String getContentType() {
        return response.headers()
                .firstValue(CONTENT_TYPE)
                .orElse("");
    }

    @Override
    public String getEncoding() {
        if (hasContent()) {
            String s = response.headers().firstValue(HeaderNames.CONTENT_ENCODING)
                    .orElse("");
            return s;
        }
        return "";
    }
}
