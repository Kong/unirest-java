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
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * A mock implementation of {@link WebSocket} for testing WebSocket communication.
 * <p>
 * This class simulates a WebSocket connection by directly sending messages to a paired
 * listener on the "other side" of the connection. When a message is sent through this
 * mock WebSocket, it is immediately delivered to the remote listener, simulating
 * network communication without actual network calls.
 * </p>
 * <p>
 * MockWebSocket instances are used in pairs (client and server) connected via
 * {@link SocketSet} objects. Each socket sends messages directly to the other's listener.
 * </p>
 *
 * <h2>Usage:</h2>
 * <p>
 * This class is typically used internally by {@link MockClient#websocket} and should not
 * need to be instantiated directly. Access the server-side socket through
 * {@link MockClient#serversSocket()} to simulate server behavior in tests.
 * </p>
 *
 * @see WebSocket
 * @see MockClient
 * @see MockListener
 * @see SocketSet
 */
public class MockWebSocket implements WebSocket {
    private SocketSet remoteSocketSet;

    /**
     * Sends a message to the remote listener by invoking the provided consumer.
     *
     * @param consumer a bi-consumer that receives the remote WebSocket and Listener
     * @return a completed {@link CompletableFuture} containing this WebSocket
     * @throws UnirestAssertion if this socket has not been initialized with {@link #init(SocketSet)}
     */
    private CompletableFuture<WebSocket> sendToOtherSide(BiConsumer<WebSocket, Listener> consumer){
        if(remoteSocketSet == null){
            throw new UnirestAssertion("Socket is not initialized. Make sure to call init(SocketSet) with the remote set.");
        }
        consumer.accept(remoteSocketSet.getSocket(), remoteSocketSet.getListener());
        return CompletableFuture.completedFuture(this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sends the text message directly to the remote listener's {@code onText} method.
     * </p>
     */
    @Override
    public CompletableFuture<WebSocket> sendText(CharSequence data, boolean last) {
        return sendToOtherSide((s,l) -> l.onText(s, data, last));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sends the binary message directly to the remote listener's {@code onBinary} method.
     * </p>
     */
    @Override
    public CompletableFuture<WebSocket> sendBinary(ByteBuffer data, boolean last) {
        return sendToOtherSide((s,l) -> l.onBinary(s, data, last));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sends the ping message directly to the remote listener's {@code onPing} method.
     * </p>
     */
    @Override
    public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
        return sendToOtherSide((s,l) -> l.onPing(s, message));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sends the pong message directly to the remote listener's {@code onPong} method.
     * </p>
     */
    @Override
    public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
        return sendToOtherSide((s,l) -> l.onPong(s, message));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sends the close message directly to the remote listener's {@code onClose} method.
     * </p>
     */
    @Override
    public CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
        return sendToOtherSide((s,l) -> l.onClose(s, statusCode, reason));
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is a no-op in the mock.
     * </p>
     */
    @Override
    public void request(long n) {

    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code null} in this mock implementation
     */
    @Override
    public String getSubprotocol() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code false} in this mock implementation
     */
    @Override
    public boolean isOutputClosed() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code false} in this mock implementation
     */
    @Override
    public boolean isInputClosed() {
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is a no-op in the mock.
     * </p>
     */
    @Override
    public void abort() {

    }

    /**
     * Initializes this WebSocket with a connection to the other side.
     * <p>
     * This method establishes the pairing between this WebSocket and the remote
     * {@link SocketSet}, enabling bidirectional communication. It also triggers
     * the {@code onOpen} event on the remote listener.
     * </p>
     *
     * @param otherSide the {@link SocketSet} representing the remote endpoint
     */
    public void init(SocketSet otherSide) {
        this.remoteSocketSet = otherSide;
        otherSide.open();
    }
}
