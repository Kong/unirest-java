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
 * A mock implementation of {@link WebSocket.Listener} that records WebSocket messages and provides assertion methods.
 * <p>
 * This class is used in conjunction with {@link MockClient} to test WebSocket communication.
 * It records all incoming messages (text, binary, ping, pong) and connection events (open, close, error),
 * allowing tests to verify that expected WebSocket interactions occurred.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Get the server-side socket from the mock client
 * SocketSet<MockWebSocket, MockListener> serverSocket = mockClient.serversSocket();
 * MockListener serverListener = serverSocket.getListener();
 *
 * // Send a message from the client side
 * clientWebSocket.sendText("Hello Server", true);
 *
 * // Assert the server received the message
 * serverListener.assertReceivedMessage("Hello Server", true);
 * serverListener.assertIsOpen();
 * }</pre>
 *
 * @see WebSocket.Listener
 * @see MockClient
 * @see MockWebSocket
 */
public class MockListener implements WebSocket.Listener {
    private List<Message> messagesReceived = new ArrayList<>();
    private ByteBuffer ping;
    private ByteBuffer pong;
    private boolean open = false;
    private int closedStatus;
    private String closedMessage;

    /**
     * {@inheritDoc}
     * <p>
     * Records that the WebSocket connection has been opened and marks this listener as open.
     * </p>
     */
    @Override
    public void onOpen(WebSocket webSocket) {
        open = true;
        WebSocket.Listener.super.onOpen(webSocket);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Records the received text message for later assertion via {@link #assertReceivedMessage(Object, boolean)}.
     * </p>
     */
    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        messagesReceived.add(new Message(String.valueOf(data), last));
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Records the received binary message for later assertion via {@link #assertReceivedMessage(Object, boolean)}.
     * </p>
     */
    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        messagesReceived.add(new Message(data, last));
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Records the ping message and automatically sends a pong response.
     * The ping message can be verified using {@link #assertPing(ByteBuffer)}.
     * </p>
     */
    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        ping = message;
        webSocket.sendPong(message);
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Records the pong message for later assertion via {@link #assertPong(ByteBuffer)}.
     * </p>
     */
    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        this.pong = message;
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Records the close event including status code and reason.
     * The close can be verified using {@link #assertIsClosed(int, String)}.
     * </p>
     */
    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        open = false;
        closedStatus = statusCode;
        closedMessage = reason;
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the default error handling behavior.
     * </p>
     */
    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }

    /**
     * Asserts that a specific message was received by this listener.
     * <p>
     * This method checks both text and binary messages that have been recorded.
     * </p>
     *
     * @param message the expected message content (String for text messages, ByteBuffer for binary)
     * @param last    whether this was expected to be the last message in a sequence
     * @throws UnirestAssertion if no matching message was received
     */
    public void assertReceivedMessage(Object message, boolean last) {
        if(!messagesReceived.stream().anyMatch(e -> Objects.equals(e.data, message) && Objects.equals(e.last, last))){
            throw new UnirestAssertion("Did not receive any message: [%s : %s] ", message, last);
        }
    }

    /**
     * Asserts that the WebSocket connection was closed with the expected status code and message.
     *
     * @param status  the expected close status code
     * @param message the expected close reason message
     * @throws UnirestAssertion if the connection is still open or was closed with different status/message
     */
    public void assertIsClosed(int status, String message) {
        if(open){
            throw new UnirestAssertion("Expected to be closed but was not");
        } else if (closedStatus != status || !Objects.equals(closedMessage, message)){
            throw new UnirestAssertion("Incorrect Closed Status/Message. Expected [%s : %s] but got [%s : %s]",
                    status, message, closedStatus, closedMessage);
        }
    }

    /**
     * Asserts that a ping message was received with the expected content.
     * <p>
     * Note that the {@link #onPing(WebSocket, ByteBuffer)} method automatically sends a pong
     * response to the WebSocket when a ping is received.
     * </p>
     *
     * @param message the expected ping message content
     * @throws UnirestAssertion if no ping was received or the content doesn't match
     */
    public void assertPing(ByteBuffer message) {
        if(!Objects.equals(ping, message)){
            throw new UnirestAssertion("Expected Ping Call with buffer %s but got %s", message, ping);
        }
    }

    /**
     * Asserts that a pong message was received with the expected content.
     *
     * @param message the expected pong message content
     * @throws UnirestAssertion if no pong was received or the content doesn't match
     */
    public void assertPong(ByteBuffer message) {
        if(!message.equals(pong)){
            throw new UnirestAssertion("Expected Pong Message %s but got %s", message, pong);
        }
    }

    /**
     * Asserts that the WebSocket connection is currently open.
     *
     * @throws UnirestAssertion if the connection is closed
     */
    public void assertIsOpen() {
        if(!open){
            throw new UnirestAssertion("Expected socket to be open but was closed.");
        }
    }

    /**
     * Internal class representing a recorded WebSocket message.
     */
    private class Message {
        private final Object data;
        private final boolean last;

        /**
         * Creates a new Message record.
         *
         * @param data the message content (String or ByteBuffer)
         * @param last whether this is the last message in a sequence
         */
        public Message(Object data, boolean last) {
            this.data = data;
            this.last = last;
        }
    }
}
