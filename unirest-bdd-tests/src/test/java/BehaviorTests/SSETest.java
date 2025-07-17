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


import kong.unirest.core.SseRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static kong.unirest.core.Unirest.sse;

@Disabled
public class SSETest extends BddTest {

    TestHandler listener;
    ExecutorService pool;

    @BeforeEach
    public void setUp() {
        super.setUp();
        listener = new TestHandler();
        pool = Executors.newFixedThreadPool(1);
    }

    @AfterEach
    @Override
    public void tearDown() {
        super.tearDown();
        pool.shutdown();
    }

    @Test
    void basicConnection() {
        runWith(sse(MockServer.SSE), listener);

        MockServer.Sse.sendComment("hey1");
        MockServer.Sse.sendEvent("Whats Happening?");

        sleep(1000);

        listener.assertHasEvent("connect", "Welcome to Server Sent Events")
                .assertHasComment("hey1")
                .assertHasEvent("message", "Whats Happening?");
    }

    @Test
    void eventsWithIds() {
        runWith(sse(MockServer.SSE), listener);

        MockServer.Sse.sendEvent("123", "cheese", "cheddar");
        MockServer.Sse.sendEvent(       "cheese", "gouda");

        sleep(1000);

        listener.assertHasEvent("123", "cheese", "cheddar")
                .assertHasEvent("cheese", "gouda");
    }

    @Test
    void queryParams(){
        runWith(sse(MockServer.SSE)
                .queryString("foo", "bar")
                .queryString(Map.of("fruit", "apple", "number", 1))
                .queryString("droid", List.of("C3PO", "R2D2")), listener);

        MockServer.Sse.lastRequest()
                .assertParam("foo", "bar")
                .assertParam("fruit", "apple")
                .assertParam("number", "1")
                .assertParam("droid", "C3PO")
                .assertParam("droid", "R2D2");
    }

    @Test
    void headers(){
        runWith(sse(MockServer.SSE)
                .header("foo", "bar")
                .header("qux", "zip")
                .headerReplace("qux", "ok")
                .headers(Map.of("fruit", "apple", "number", "1"))
                .cookie("snack", "snickerdoodle")
                , listener);

        MockServer.Sse.lastRequest()
                .assertHeader("Accept", "text/event-stream")
                .assertHeader("foo", "bar")
                .assertHeader("qux", "ok")
                .assertHeader("number", "1")
                .assertHeader("fruit", "apple")
                .assertCookie("snack", "snickerdoodle");
    }

    @Test
    void canReplaceTheDefaultAcceptsHeader(){
        runWith(sse(MockServer.SSE)
                        .header("Accept", "application/json")
                , listener);

        MockServer.Sse.lastRequest()
                .assertHeader("Accept", "application/json");
    }

    private void runWith(SseRequest sse, TestHandler tl) {
        try {
            pool.submit(() -> {
                var future = sse.connect(tl);
                while(!future.isDone()){
                    // waitin'
                }
            });

            Thread.sleep(1000);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}