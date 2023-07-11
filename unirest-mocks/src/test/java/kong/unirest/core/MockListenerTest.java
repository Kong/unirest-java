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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MockListenerTest {
    WebSocket socket;
    MockListener listener;

    @BeforeEach
    void setUp() {
        socket = mock(WebSocket.class);
        listener = new MockListener();
    }

    @Test
    void assertPing() {
        var wrap = ByteBuffer.wrap("hi".getBytes());

        assertAssertionThrown(() -> listener.assertPing(wrap),
                "Expected Ping Call with buffer java.nio.HeapByteBuffer[pos=0 lim=2 cap=2] but got null");

        listener.onPing(socket, wrap);

        listener.assertPing(wrap);

        verify(socket).sendPong(wrap);
    }

    @Test
    void assertPong() {
        var wrap = ByteBuffer.wrap("hi".getBytes());

        assertAssertionThrown(() -> listener.assertPong(wrap),
                "Expected Pong Message java.nio.HeapByteBuffer[pos=0 lim=2 cap=2] but got null");

        listener.onPong(socket, wrap);

        listener.assertPong(wrap);
    }

    @Test
    void assertClosed() {
        listener.onOpen(socket);

        assertAssertionThrown(() -> listener.assertIsClosed(418, "I am a teapot"),
                "Expected to be closed but was not");

        listener.onClose(socket, 418, "I am a teapot");

        assertAssertionThrown(() -> listener.assertIsClosed(400, "Sad"),
                "Incorrect Closed Status/Message. Expected [400 : Sad] but got [418 : I am a teapot]");

        listener.assertIsClosed(418, "I am a teapot");
    }

    @Test
    void assertTextMessage() {
        assertAssertionThrown(() -> listener.assertReceivedMessage("Hail Sithis!", false),
                "Did not receive any message: [Hail Sithis! : false] ");

        listener.onText(socket, "Hail Sithis!", true);

        assertAssertionThrown(() -> listener.assertReceivedMessage("Hail Sithis!", false),
                "Did not receive any message: [Hail Sithis! : false] ");

        listener.assertReceivedMessage("Hail Sithis!", true);
    }

    @Test
    void assertBinaryMessage() {
        var buf = ByteBuffer.wrap("Hail Sithis!".getBytes());

        assertAssertionThrown(() -> listener.assertReceivedMessage(buf, false),
                "Did not receive any message: [java.nio.HeapByteBuffer[pos=0 lim=12 cap=12] : false] ");

        listener.onBinary(socket, buf, true);

        assertAssertionThrown(() -> listener.assertReceivedMessage(buf, false),
                "Did not receive any message: [java.nio.HeapByteBuffer[pos=0 lim=12 cap=12] : false] ");

        listener.assertReceivedMessage(buf, true);
    }

    @Test
    void assertOpen() {
        assertAssertionThrown(() -> listener.assertIsOpen(),
                "Expected socket to be open but was closed.");
        listener.onOpen(socket);

        listener.assertIsOpen();
    }

    private void assertAssertionThrown(Executable ex, String errorMessage) {
        var a = assertThrows(UnirestAssertion.class, ex);
        assertEquals(errorMessage, a.getMessage());
    }
}