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

import kong.tests.Base;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;

class MockWebSocketResponseTest extends Base {

    private MockListener listener;

    @BeforeEach
    public void setUp() {
        super.setUp();
        listener = new MockListener();
    }

    @Test
    void sendTextFromServer() {
        createConnection();

        client.serversSocket().getSocket().sendText("All hail the listener", false);

        listener.assertReceivedMessage("All hail the listener", false);
    }

    @Test
    void sendPingFromServer() {
        createConnection();

        ByteBuffer buffer = ByteBuffer.wrap("ping".getBytes());
        client.serversSocket().getSocket().sendPing(buffer);

        client.serversSocket().getListener().assertPong(buffer);
    }

    @Test
    void serverSendsBinary(){
        createConnection();
        ByteBuffer bytes = ByteBuffer.wrap("hi".getBytes());
        client.serversSocket().getSocket().sendBinary(bytes, false);
        listener.assertReceivedMessage(bytes, false);
    }

    @Test
    void serverSendsClose() {
        createConnection();

        client.serversSocket().getSocket().sendClose(418, "I am a teapot");

        listener.assertIsClosed(418, "I am a teapot");
    }

    @Test
    void localSendsText() throws Exception {
        createConnection().socket().get().sendText("Hail Sithis!", false);

        client.serversSocket()
                .getListener()
                .assertReceivedMessage("Hail Sithis!", false);
    }

    @Test
    void localSendsBinary() throws Exception {
        ByteBuffer sithis = ByteBuffer.wrap("Hail Sithis!".getBytes());
        createConnection().socket().get().sendBinary(sithis, false);

        client.serversSocket()
                .getListener()
                .assertReceivedMessage(sithis, false);
    }

    @Test
    void localSendsClose() throws Exception  {
        createConnection().socket().get().sendClose(418, "I am a teapot");

        client.serversSocket()
                .getListener()
                .assertIsClosed(418, "I am a teapot");
    }

    @Test
    void localSendsPingPong() throws Exception  {
        ByteBuffer buffer = ByteBuffer.wrap("ping".getBytes());
        createConnection().socket().get().sendPing(buffer);

        listener.assertPong(buffer);
    }

    @Test
    void onConnectFiresFromLocal() throws Exception {
        MockListener listener = new MockListener(){
            @Override
            public void onOpen(WebSocket webSocket) {
                webSocket.sendText("Hello Mother!", false);
            }
        };
        Unirest.webSocket("ws://localhost")
                .connect(listener)
                .socket()
                .get();

        client.serversSocket().getListener().assertReceivedMessage("Hello Mother!", false);
    }

    private WebSocketResponse createConnection() {
        return Unirest.webSocket("ws://localhost").connect(listener);
    }


}