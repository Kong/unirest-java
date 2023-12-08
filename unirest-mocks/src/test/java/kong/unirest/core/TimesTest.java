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

package kong.unirest.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimesTest {

    @Test
    void exactly() {
        assertPass(Times.exactlyOnce().matches(1));
        assertFail(Times.exactlyOnce().matches(2), "Expected exactly 1 invocations but got 2");
        assertFail(Times.exactlyOnce().matches(0), "Expected exactly 1 invocations but got 0");
    }

    @Test
    void exactlySpecified() {
        assertPass(Times.exactly(42).matches(42));
        assertFail(Times.exactly(1).matches(2),  "Expected exactly 1 invocations but got 2");
        assertFail(Times.exactly(42).matches(2), "Expected exactly 42 invocations but got 2");
        assertFail(Times.exactly(1).matches(0),  "Expected exactly 1 invocations but got 0");
    }

    @Test
    void atLeast() {
        assertPass(Times.atLeastOnce().matches(1));
        assertPass(Times.atLeastOnce().matches(2));
        assertFail(Times.atLeastOnce().matches(0), "Expected at least 1 invocations but got 0");
    }

    @Test
    void atLeastSpecific() {
        assertPass(Times.atLeast(1).matches(1));
        assertPass(Times.atLeast(1).matches(2));
        assertFail(Times.atLeast(1).matches(0), "Expected at least 1 invocations but got 0");
    }

    @Test
    void never() {
        assertPass(Times.never().matches(0));
        assertFail(Times.never().matches(2), "Expected exactly 0 invocations but got 2");
    }

    @Test
    void atMost(){
        assertPass(Times.atMost(2).matches(1));
        assertPass(Times.atMost(2).matches(2));
        assertFail(Times.atMost(2).matches(3), "Expected no more than 2 invocations but got 3");
    }

    void assertPass(Times.EvaluationResult matches){
        assertTrue(matches.isSuccess());
        assertNull(matches.getMessage());
    }

    void assertFail(Times.EvaluationResult matches, String message) {
        assertFalse(matches.isSuccess());
        assertEquals(message, matches.getMessage());
    }
}