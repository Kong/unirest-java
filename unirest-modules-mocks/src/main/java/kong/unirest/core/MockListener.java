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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

/**
 * A Mock WebSocket.Lister that records messages and has custom asserts.
 */
public class MockListener implements WebSocket.Listener {
    private List<Message> messagesReceived = new ArrayList<>();
    private ByteBuffer ping;
    private ByteBuffer pong;
    private boolean open = false;
    private int closedStatus;
    private String closedMessage;

    @Override
    public void onOpen(WebSocket webSocket) {
        open = true;
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        messagesReceived.add(new Message(String.valueOf(data), last));
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        messagesReceived.add(new Message(data, last));
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        ping = message;
        webSocket.sendPong(message);
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        this.pong = message;
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        open = false;
        closedStatus = statusCode;
        closedMessage = reason;
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }

    public void assertReceivedMessage(Object message, boolean last) {
        if(!messagesReceived.stream().anyMatch(e -> Objects.equals(e.data, message) && Objects.equals(e.last, last))){
            throw new UnirestAssertion("Did not receive any message: [%s : %s] ", message, last);
        }
    }

    public void assertIsClosed(int status, String message) {
        if(open){
            throw new UnirestAssertion("Expected to be closed but was not");
        } else if (closedStatus != status || !Objects.equals(closedMessage, message)){
            throw new UnirestAssertion("Incorrect Closed Status/Message. Expected [%s : %s] but got [%s : %s]",
                    status, message, closedStatus, closedMessage);
        }
    }

    /**
     * assert that a ping message was received.
     * Note that the onPing method will automatically send a pong to the WebSocket
     * @param message the message
     */
    public void assertPing(ByteBuffer message) {
        if(!Objects.equals(ping, message)){
            throw new UnirestAssertion("Expected Ping Call with buffer %s but got %s", message, ping);
        }
    }

    /**
     * assert that a pong message was received.
     * Note that the onPing method will automatically send a pong to the WebSocket
     * @param message the message
     */
    public void assertPong(ByteBuffer message) {
        if(!message.equals(pong)){
            throw new UnirestAssertion("Expected Pong Message %s but got %s", message, pong);
        }
    }

    public void assertIsOpen() {
        if(!open){
            throw new UnirestAssertion("Expected socket to be open but was closed.");
        }
    }

    private class Message {
        private final Object data;
        private final boolean last;

        public Message(Object data, boolean last) {
            this.data = data;
            this.last = last;
        }
    }
}
