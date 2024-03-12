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
 *  A Mock of a websocket that sends messages directly to a single listener on the other side
 */
public class MockWebSocket implements WebSocket {
    private SocketSet remoteSocketSet;

    private CompletableFuture<WebSocket> sendToOtherSide(BiConsumer<WebSocket, Listener> consumer){
        if(remoteSocketSet == null){
            throw new UnirestAssertion("Socket is not initialized. Make sure to call init(SocketSet) with the remote set.");
        }
        consumer.accept(remoteSocketSet.getSocket(), remoteSocketSet.getListener());
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public CompletableFuture<WebSocket> sendText(CharSequence data, boolean last) {
        return sendToOtherSide((s,l) -> l.onText(s, data, last));
    }

    @Override
    public CompletableFuture<WebSocket> sendBinary(ByteBuffer data, boolean last) {
        return sendToOtherSide((s,l) -> l.onBinary(s, data, last));
    }

    @Override
    public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
        return sendToOtherSide((s,l) -> l.onPing(s, message));
    }

    @Override
    public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
        return sendToOtherSide((s,l) -> l.onPong(s, message));
    }

    @Override
    public CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
        return sendToOtherSide((s,l) -> l.onClose(s, statusCode, reason));
    }

    @Override
    public void request(long n) {

    }

    @Override
    public String getSubprotocol() {
        return null;
    }

    @Override
    public boolean isOutputClosed() {
        return false;
    }

    @Override
    public boolean isInputClosed() {
        return false;
    }

    @Override
    public void abort() {

    }

    public void init(SocketSet otherSide) {
        this.remoteSocketSet = otherSide;
        otherSide.open();
    }
}
