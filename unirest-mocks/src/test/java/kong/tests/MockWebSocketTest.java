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

package kong.tests;


import kong.unirest.core.MockWebSocket;
import kong.unirest.core.SocketSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MockWebSocketTest {
    ByteBuffer buff = ByteBuffer.wrap("Hail Sithis!".getBytes());
    WebSocket remoteSocket;
    WebSocket.Listener remoteListener;
    MockWebSocket testSocket;

    @BeforeEach
    void setUp() {
        remoteSocket = mock(WebSocket.class);
        remoteListener = mock(WebSocket.Listener.class);
        testSocket = new MockWebSocket();
        testSocket.init(new SocketSet(remoteSocket, remoteListener, "test"));
    }

    @Test
    void sendText() {
        testSocket.sendText("Hail Sithis!", false);
        verify(remoteListener).onText(remoteSocket, "Hail Sithis!", false);
    }

    @Test
    void sendBinary() {
        testSocket.sendBinary(buff, false);
        verify(remoteListener).onBinary(remoteSocket, buff, false);
    }

    @Test
    void sendPing() {
        testSocket.sendPing(buff);
        verify(remoteListener).onPing(remoteSocket, buff);
    }

    @Test
    void sendPong() {
        testSocket.sendPong(buff);
        verify(remoteListener).onPong(remoteSocket, buff);
    }

    @Test
    void sendClose() {
        testSocket.sendClose(418, "I am a Teapot");
        verify(remoteListener).onClose(remoteSocket, 418, "I am a Teapot");
    }
}
