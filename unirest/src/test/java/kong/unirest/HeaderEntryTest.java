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

package kong.unirest;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class HeaderEntryTest {

    @Test
    void entryNotEqual() {
        assertNotEquals(entry("foo", "qux"), entry("foo", "bar"));
        assertNotEquals(entry("qux", "bar"), entry("foo", "bar"));
    }

    @Test
    void entryEqual() {
        assertEquals(entry("foo", "bar"), entry("foo", "bar"));
    }

    @Test
    void entryEqualLambda() {
        assertEquals(entry("foo", () -> "bar"), entry("foo", "bar"));
        assertEquals(entry("foo", () -> "bar"), entry("foo", () -> "bar"));
    }

    @Test
    void entryLambdasCannotBeNull_butMayReturnNull() {
        assertNull(entry("foo", (Supplier<String>) null).getValue());
    }

    private Headers.Entry entry(String key, Supplier<String> value) {
        return new Headers.Entry(key, value);
    }

    private Headers.Entry entry(String foo, String qux) {
        return new Headers.Entry(foo, qux);
    }
}