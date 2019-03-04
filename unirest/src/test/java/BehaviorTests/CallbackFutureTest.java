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

import kong.unirest.Unirest;
import kong.unirest.NoopCallback;
import org.junit.Ignore;
import org.junit.Test;

import static kong.unirest.MockCallback.json;

public class CallbackFutureTest extends BddTest {

    @Test(timeout = 5000)
    public void onSuccess() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("Snazzy", "Shoes")
                .asJsonAsync()
                .thenAccept(r -> {
                    parse(r).assertParam("Snazzy", "Shoes");
                    asyncSuccess();
                }).get();

        assertAsync();
    }

    @Test(timeout = 5000)
    public void onSuccessSupplyCallback() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("Snazzy", "Shoes")
                .asJsonAsync(new NoopCallback<>())
                .thenAccept(r -> {
                    parse(r).assertParam("Snazzy", "Shoes");
                    asyncSuccess();
                }).get();

        assertAsync();
    }

    @Test(timeout = 5000) @Ignore
    public void onFailure() throws Exception {
        Unirest.get("http://localhost:0000")
                .asJsonAsync(json(this))
                .isCompletedExceptionally();

        assertFailed("java.net.ConnectException: Connection refused");
    }
}