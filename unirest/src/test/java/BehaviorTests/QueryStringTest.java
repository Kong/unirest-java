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
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.Test;

import java.util.Arrays;

public class QueryStringTest extends BddTest {
    @Test
    public void testGetQueryStrings() {
       Unirest.get(MockServer.GET)
                .queryString("name", "mark")
                .queryString("nick", "thefosk")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "mark")
                .assertParam("nick", "thefosk");
    }

    @Test
    public void canPassQueryParamsDirectlyOnUriOrWithMethod() {
        Unirest.get(MockServer.GET + "?name=mark")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "mark");

        Unirest.get(MockServer.GET)
                .queryString("name", "mark2")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "mark2");
    }

    @Test
    public void canPassInACharSequence() {
        Unirest.get(MockServer.GET)
                .queryString("foo", new StringBuilder("bar"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    public void multipleParams() {
        Unirest.get(MockServer.GET + "?name=ringo")
                .queryString("name", "paul")
                .queryString("name", "john")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "ringo")
                .assertParam("name", "paul")
                .assertParam("name", "john");
    }

    @Test
    public void testGetUTF8() {
        Unirest.get(MockServer.GET)
                .queryString("param3", "こんにちは")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("param3", "こんにちは");
    }

    @Test
    public void testGetMultiple() {
        for (int i = 1; i <= 20; i++) {
            HttpResponse<JsonNode> response = Unirest.get(MockServer.GET + "?try=" + i).asJson();
            parse(response).assertParam("try", String.valueOf(i));
        }
    }

    @Test
    public void testQueryStringEncoding() {
        String testKey = "email2=someKey&email";
        String testValue = "hello@hello.com";

        Unirest.get(MockServer.GET)
                .queryString(testKey, testValue)
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam(testKey, testValue);
    }

    @Test
    public void testGetQuerystringArray() {
        Unirest.get(MockServer.GET)
                .queryString("name", "Mark")
                .queryString("name", "Tom")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testGetArray() {
        Unirest.get(MockServer.GET)
                .queryString("name", Arrays.asList("Mark", "Tom"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }
}
