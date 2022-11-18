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

package BehaviorTests;

import kong.unirest.core.Unirest;
import org.junit.jupiter.api.Test;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class WebsocketTest extends BddTest {
    @Test
    void makeAWebSocket() throws Exception {
        TheListener listener = new TheListener();
        MockServer.WebSocketHandler.expectOpeningMessage("Darkness Rises When Silence Dies");
        assertEquals("ws://localhost:4567/websocket", MockServer.WEBSOCKET);
        Unirest.webSocket(MockServer.WEBSOCKET)
                .connect(listener)
                .socket()
                .get()
                .sendText("...", false);
        Thread.sleep(50);
        assertTrue(listener.isOpen);
        assertEquals("Darkness Rises When Silence Dies", listener.messages.get(0));

    }


    @Test
    void sendsHeaders() throws Exception {
        assertEquals("ws://localhost:4567/websocket", MockServer.WEBSOCKET);
        Unirest.webSocket(MockServer.WEBSOCKET)
                .header("Authentication", "Sanguine, my Brother.")
                .connect(new TheListener())
                .socket()
                .get();

        assertEquals("Sanguine, my Brother.",
                MockServer.WebSocketHandler.headers.get("Authentication"));
    }

    public class TheListener implements WebSocket.Listener {
        public boolean isOpen = false;
        public List<String> messages = new ArrayList<>();

        @Override
        public void onOpen(WebSocket webSocket) {
            this.isOpen = true;
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            messages.add(String.valueOf(data));
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }
    }
}
