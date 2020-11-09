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

package kong.tests;

import kong.unirest.HttpMethod;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static kong.unirest.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorTest extends Base {

    @Test
    void mapError() {
        client.expect(HttpMethod.GET, path)
                .thenReturn(new ErrorObj("Boo!"))
                .withStatus(INTERNAL_SERVER_ERROR);

        ErrorObj err = Unirest.get(path).asJson().mapError(ErrorObj.class);

        assertEquals("Boo!", err.say);
    }

    private static class ErrorObj {
        public String say;

        public ErrorObj(String say) {
            this.say = say;
        }
    }
}
