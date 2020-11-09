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

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedirectHandlingTest extends BddTest {

    @Test
    void willFollowRedirectsByDefault() {
        Unirest.get(MockServer.REDIRECT)
                .asObject(RequestCapture.class)
                .getBody()
                .assertUrl("http://localhost:4567/get");
    }

    @Test
    void canDisableRedirects(){
        Unirest.config().followRedirects(false);
        HttpResponse response = Unirest.get(MockServer.REDIRECT).asEmpty();

        assertEquals(301, response.getStatus());
    }

    @Test
    void willFollowRedirectsByDefaultAsync() throws ExecutionException, InterruptedException {
        Unirest.get(MockServer.REDIRECT)
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody()
                .assertUrl("http://localhost:4567/get");
    }

    @Test
    void canDisableRedirectsAsync() throws Exception {
        Unirest.config().followRedirects(false);
        HttpResponse response = Unirest.get(MockServer.REDIRECT).asEmptyAsync().get();

        assertEquals(301, response.getStatus());
    }
}
