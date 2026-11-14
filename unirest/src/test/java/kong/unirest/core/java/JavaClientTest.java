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

import kong.unirest.core.Config;
import kong.unirest.core.Headers;
import kong.unirest.core.SseRequestImpl;
import kong.unirest.core.UnirestException;
import kong.unirest.core.UnirestInstance;
import kong.unirest.core.WebSocketRequestImpl;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.WebSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaClientTest {

    private static final String SECRET = "Bearer s3cr3t\n";
    private static final String MESSAGE = "invalid value for header \"Authorization\": illegal character 0x0a at index 13";

    @Test
    void invalidHeaderValueIsNotCopiedIntoTheException() {
        var unirest = new UnirestInstance(new Config());

        assertThatThrownBy(() -> unirest.get("http://localhost/").header("Authorization", SECRET).asEmpty())
                .isInstanceOf(UnirestException.class)
                .hasMessageNotContaining("s3cr3t")
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage(MESSAGE);
    }

    @Test
    void invalidSseHeaderValueIsNotCopiedIntoTheException() {
        var request = new SseRequestImpl(new Config(), "http://localhost/sse").header("Authorization", SECRET);

        assertThatThrownBy(() -> SseRequestBuilder.getHttpRequest(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MESSAGE);
    }

    @Test
    void invalidWebSocketHeaderValueIsNotCopiedIntoTheException() {
        var config = new Config();
        var request = new WebSocketRequestImpl(config, "ws://localhost/").header("Authorization", SECRET);

        assertThatThrownBy(() -> new JavaClient(config).websocket(request, new WebSocket.Listener() { }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MESSAGE);
    }

    @Test
    void acceptsTheSameHeaderValuesAsTheJdk() {
        for (int c = 0; c <= Character.MAX_VALUE; c++) {
            String value = "a" + (char) c + "b";
            assertThat(acceptedByUnirest(value)).as("char 0x%04x", c).isEqualTo(acceptedByJdk(value));
        }
    }

    private static boolean acceptedByUnirest(String value) {
        var headers = new Headers();
        headers.add("X-Test", value);
        try {
            JavaClient.validHeaderValue(headers.all().get(0));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean acceptedByJdk(String value) {
        try {
            HttpRequest.newBuilder(URI.create("http://localhost/")).header("X-Test", value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
