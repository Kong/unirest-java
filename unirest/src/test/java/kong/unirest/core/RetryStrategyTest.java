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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class RetryStrategyTest {

    @AfterEach
    void tearDown() {
        Util.resetClock();
    }

    @Test
    void parseInts() {
        assertEquals(1000, parseToMillies("1"));
        assertEquals(10000, parseToMillies("10"));
    }

    @Test
    void parseDoubles() {
        assertEquals(1500, parseToMillies("1.5"));
        assertEquals(50, parseToMillies(".05"));
    }

    @Test
    void parseHttpDateFormat() {
        Util.freezeClock(ZonedDateTime.parse("2015-10-21T07:28:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant());

        assertEquals(1000, parseToMillies("Wed, 21 Oct 2015 07:28:01 GMT"));
    }

    @Test
    void nullRespoinseIsAFalse() {
        assertFalse(new RetryStrategy.Standard(1).isRetryable(null));
    }

    @Test
    void nullRespoinseIsZeroSeconds() {
        assertEquals(0, new RetryStrategy.Standard(1).getWaitTime(null));
    }

    @Test
    void unparseableRetruIsZeroSeconds() {
        assertEquals(0, parseToMillies("Love Shack Baby"));
    }

    private long parseToMillies(String s) {
        Headers h = new Headers();
        h.add("Retry-After", s);
        return new RetryStrategy.Standard(1).getWaitTime(new MockResponse(h));
    }

}