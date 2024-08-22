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

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsBytesTest extends BddTest {
    JacksonObjectMapper om = new JacksonObjectMapper();

    @Test
    void getGetResultAsBytes() {
        var content = Unirest.get(MockServer.GET)
                .asBytes()
                .getBody();

        var cap = om.readValue(content, RequestCapture.class);

        cap.assertStatus(200);
    }

    @Test
    void getGetBinaryResultAsBytes() {
        var content = Unirest.get(MockServer.BINARYFILE)
                .asBytes()
                .getBody();

        assertEquals(46246, content.length);
    }

    @Test
    void getGetResultAsBytesAsync() throws Exception {
        var content = Unirest.get(MockServer.GET)
                .asBytesAsync()
                .get()
                .getBody();

        var cap = om.readValue(content, RequestCapture.class);

        cap.assertStatus(200);
    }

    @Test
    void getGetResultAsBytesAsyncCallback() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("fruit", "apple")
                .asBytesAsync(r -> {
                    RequestCapture cap = om.readValue(r.getBody(), RequestCapture.class);
                    cap.assertParam("fruit", "apple");
                    asyncSuccess();
                })
                .get()
                .getBody();

        assertAsync();
    }

    @Test // https://github.com/Kong/unirest-java/issues/424
    void mappingErrorsFromAsBytes() {
        MockServer.setStringResponse("howdy");
        var r = Unirest.get(MockServer.ERROR_RESPONSE)
                .asBytes()
                .mapError(String.class);

        assertEquals("howdy", r);
    }

    @Test // https://github.com/Kong/unirest-java/issues/424
    void mappingErrorsFromAsBytesMapped() {
        MockServer.setJsonAsResponse(new Foo("howdy"));
        var r = Unirest.get(MockServer.ERROR_RESPONSE)
                .asBytes()
                .mapError(Foo.class);

        assertEquals("howdy", r.bar);
    }
}
