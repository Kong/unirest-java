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

import kong.unirest.Proxy;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import static org.junit.Assert.assertTrue;

@Ignore // The Janky Proxy is pretty janky and isn't entirely stable in CI
public class ProxyTest extends BddTest {

    @After @Override
    public void tearDown() {
        super.tearDown();
        Unirest.shutDown(true);
        JankyProxy.shutdown();
    }

    @Test
    public void canUseNonAuthProxy() {
        JankyProxy.runServer("localhost", 4567, 7777);

        Unirest.config().proxy(new Proxy("localhost", 7777));

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertStatus(200);

        assertTrue(JankyProxy.wasUsed());
    }

    @Test
    public void canUseNonAuthProxyWithEasyMethod() {
        JankyProxy.runServer("localhost", 4567, 7777);

        Unirest.config().proxy("localhost", 7777);

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertStatus(200);

        assertTrue(JankyProxy.wasUsed());
    }

    @Test
    public void canPassProxyOnRequest() {
        JankyProxy.runServer("localhost", 4567, 7777);

        Unirest.get(MockServer.GET)
                .proxy("localhost", 7777)
                .asObject(RequestCapture.class)
                .getBody()
                .assertStatus(200);

        assertTrue(JankyProxy.wasUsed());
    }

    @Test
    public void canSetAuthenticatedProxy(){
        JankyProxy.runServer("localhost", 4567, 7777);

        Unirest.config().proxy("localhost", 7777, "username", "password1!");

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertStatus(200);

        assertTrue(JankyProxy.wasUsed());
    }

    @Test
    @Ignore // there is some weird conflict between jetty and unirest here
    public void canFlagTheClientsToUseSystemProperties(){
        JankyProxy.runServer("localhost", 4567, 7777);

        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "7777");

        Unirest.config().useSystemProperties(true);

        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .assertStatus(200);

        assertTrue(JankyProxy.wasUsed());
    }

    @Test @Ignore // https://free-proxy-list.net/
    public void callSomethingRealThroughARealProxy() {
        Unirest.config().proxy("18.222.230.116",8080);
        //Unirest.config().proxy("34.73.62.46",3128, "myuser","pass1!");
        HttpResponse<String> r = Unirest.get("https://twitter.com/ryber").asString();
        System.out.println("status = " + r.getStatus());
        System.out.println("body= " + r.getBody());
    }
}
