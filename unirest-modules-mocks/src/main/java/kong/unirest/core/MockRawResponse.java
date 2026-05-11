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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A mock implementation of {@link RawResponse} for testing purposes.
 * <p>
 * This class provides a simulated HTTP response that can be configured with
 * custom status codes, headers, and body content. It is used internally by
 * {@link MockClient} to return predefined responses for mocked requests.
 * </p>
 * <p>
 * The response body is stored as a string and converted to the appropriate
 * format (bytes, stream, reader) when requested.
 * </p>
 *
 * @see RawResponse
 * @see MockClient
 */
public class MockRawResponse implements RawResponse {
    private final String response;
    private final Headers responseHeaders;
    private final int status;
    private final String statusMessage;
    private final Config config;
    private final HttpRequestSummary summary;

    /**
     * Constructs a new MockRawResponse with the specified response details.
     *
     * @param responseBody    the response body content as a string, or {@code null} for no content
     * @param responseHeaders the response headers
     * @param status          the HTTP status code
     * @param statusMessage   the HTTP status message (e.g., "OK", "Not Found")
     * @param config          the Unirest configuration
     * @param summary         the summary of the original request
     */
    public MockRawResponse(String responseBody, Headers responseHeaders, int status,
                           String statusMessage, Config config, HttpRequestSummary summary) {
        this.response = responseBody;
        this.responseHeaders = responseHeaders;
        this.status = status;
        this.statusMessage = statusMessage;
        this.config = config;
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStatusText() {
        return statusMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Headers getHeaders() {
        return responseHeaders;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the response body as a new {@link ByteArrayInputStream}.
     * </p>
     */
    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(response.getBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContentAsBytes() {
        return response.getBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentAsString() {
        return response;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the charset is {@code null}, defaults to UTF-8.
     * </p>
     */
    @Override
    public String getContentAsString(String charset) {
        if(Objects.isNull(response)){
            return null;
        }
        return new String(response.getBytes(), tryGetCharset(charset));
    }

    /**
     * Attempts to get a Charset from the provided name.
     *
     * @param charset the charset name, or {@code null}
     * @return the corresponding Charset, or UTF-8 if charset is {@code null}
     */
    private Charset tryGetCharset(String charset) {
        if(Objects.isNull(charset)){
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(charset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStreamReader getContentReader() {
        return new InputStreamReader(getContent());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if the response body is not {@code null}
     */
    @Override
    public boolean hasContent() {
        return response != null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the value of the "Content-Type" header.
     * </p>
     */
    @Override
    public String getContentType() {
        return responseHeaders.getFirst("Content-Type");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the value of the "Content-Encoding" header.
     * </p>
     */
    @Override
    public String getEncoding() {
        return responseHeaders.getFirst("Content-Encoding");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Config getConfig() {
        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponseSummary toSummary() {
        return new ResponseSummary(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestSummary getRequestSummary() {
        return summary;
    }
}
