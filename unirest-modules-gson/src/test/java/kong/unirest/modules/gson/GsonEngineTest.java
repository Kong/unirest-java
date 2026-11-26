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
package kong.unirest.modules.gson;

import kong.unirest.core.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GsonEngineTest {

    @Test
    void maps() {
        List<Number> list = toMapArray("[1,2,3]");
        assertThat(list)
                .containsExactly(1,2,3);
    }

    @Test
    void longNumbers() {
        List<Number> list = toMapArray("[3000000000]");
        assertThat(list)
                .containsExactly(3000000000L);
    }

    @Test
    void tinyNumbers() {
        List<Number> list = toMapArray("[-3000000000]");
        assertThat(list)
                .containsExactly(-3000000000L);
    }

    @Test
    void bigNumbers() {
        List<Number> list = toMapArray("[99999999999999999999]");
        assertThat(list)
                .containsExactly(new BigInteger("99999999999999999999"));
    }

    private static List<Number> toMapArray(String s, Object... n) {
        var formatted = String.format(s, n);
        var obj = new JSONObject("{\"foo\": " + formatted + "}");
        var map = obj.toMap();
        List<Number> list = (List<Number>) map.get("foo");
        return list;
    }

}