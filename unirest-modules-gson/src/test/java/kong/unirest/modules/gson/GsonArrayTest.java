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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GsonArrayTest {

    @Test
    void nullNumberValues(){
        var o = new GsonArray();
        o.add(2);
        o.put(0, (Number)null);
        assertThat(o.get(0))
                .isEqualTo(new GsonNull());
    }

    @Test
    void nullStringValues(){
        var o = new GsonArray();
        o.add("not me");
        o.put(0, (String)null);
        assertThat(o.get(0))
                .isEqualTo(GsonNull.INSTANCE);
    }

    @Test
    void nullBooleanValues(){
        var o = new GsonArray();
        o.add(true);
        o.put(0, (Boolean) null);
        assertThat(o.get(0))
                .isEqualTo(GsonNull.INSTANCE);
    }
}