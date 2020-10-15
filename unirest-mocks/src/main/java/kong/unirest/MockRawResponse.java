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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MockRawResponse implements RawResponse {
    private final String response;
    private final Headers responseHeaders;
    private final int status;
    private final String statusMessage;
    private final Config config;

    public MockRawResponse(String responseBody, Headers responseHeaders, int status,
                           String statusMessage, Config config) {
        this.response = responseBody;
        this.responseHeaders = responseHeaders;
        this.status = status;
        this.statusMessage = statusMessage;
        this.config = config;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getStatusText() {
        return statusMessage;
    }

    @Override
    public Headers getHeaders() {
        return responseHeaders;
    }

    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(response.getBytes());
    }

    @Override
    public byte[] getContentAsBytes() {
        return response.getBytes();
    }

    @Override
    public String getContentAsString() {
        return response;
    }

    @Override
    public String getContentAsString(String charset) {
        if(Objects.isNull(response)){
            return null;
        }
        return new String(response.getBytes(), tryGetCharset(charset));
    }

    private Charset tryGetCharset(String charset) {
        if(Objects.isNull(charset)){
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(charset);
    }

    @Override
    public InputStreamReader getContentReader() {
        return new InputStreamReader(getContent());
    }

    @Override
    public boolean hasContent() {
        return response != null;
    }

    @Override
    public String getContentType() {
        return responseHeaders.getFirst("Content-Type");
    }

    @Override
    public String getEncoding() {
        return responseHeaders.getFirst("Content-Encoding");
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public HttpResponseSummary toSummary() {
        return new ResponseSummary(this);
    }
}
