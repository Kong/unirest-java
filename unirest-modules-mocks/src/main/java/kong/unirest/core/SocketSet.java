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

/**
 * Represents a pairing of a WebSocket and its associated listener for one side of a WebSocket communication.
 * <p>
 * A WebSocket connection involves two endpoints, each with its own socket and listener.
 * This class encapsulates one side of that communication, bundling together the socket
 * instance, its listener, and a descriptive name for identification purposes.
 * </p>
 * <p>
 * This is primarily used in the mocking framework to simulate WebSocket connections
 * for testing purposes.
 * </p>
 *
 * @param <S> the type of WebSocket, must extend {@link WebSocket}
 * @param <L> the type of WebSocket listener, must extend {@link WebSocket.Listener}
 * @see WebSocket
 * @see WebSocket.Listener
 */
public class SocketSet<S extends WebSocket, L extends WebSocket.Listener> {

    private final S socket;
    private final L listener;
    private final String name;

    /**
     * Creates a new SocketSet with the specified socket, listener, and name.
     *
     * @param socket   the WebSocket instance for this side of the connection
     * @param listener the listener that will receive WebSocket events
     * @param name     a descriptive name for this socket set, used for identification and in {@link #toString()}
     */
    public SocketSet(S socket, L listener, String name){
        this.socket = socket;
        this.listener = listener;
        this.name = name;
    }

    /**
     * Returns the WebSocket instance for this socket set.
     *
     * @return the WebSocket instance
     */
    public S getSocket() {
        return socket;
    }

    /**
     * Returns the WebSocket listener for this socket set.
     *
     * @return the WebSocket listener
     */
    public L getListener() {
        return listener;
    }

    /**
     * Returns the descriptive name of this socket set.
     *
     * @return the name of this socket set
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of this socket set.
     *
     * @return the name of this socket set
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Simulates opening the WebSocket connection by invoking the listener's {@code onOpen} callback.
     * <p>
     * This method triggers the {@link WebSocket.Listener#onOpen(WebSocket)} method on the
     * associated listener, passing this socket set's WebSocket instance.
     * </p>
     */
    public void open() {
        listener.onOpen(socket);
    }
}
