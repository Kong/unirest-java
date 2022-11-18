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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilTest {
    private static final Locale defaultLocal = Locale.getDefault();


    @AfterEach
    void tearDown() {
        Locale.setDefault(defaultLocal);
    }

    @Test
    void parseCookieFormat() {
        assertEquals(ZonedDateTime.of(2020,1,5, 15,0,20,0, ZoneId.of("GMT")),
                Util.tryParseToDate("Sun, 05-Jan-2020 15:00:20 GMT"));
    }

    @Test
    void parseLegacyFormat() {
        assertEquals(ZonedDateTime.of(2020,3,6, 16,5,35,0, ZoneId.of("GMT")),
                Util.tryParseToDate("Fri, 06 Mar 2020 16:05:35 GMT"));
    }

    @Test
    void parseCookieFormat_whenDefaultLocalIsChinese() {
        Locale.setDefault(Locale.CHINESE);
        assertEquals(ZonedDateTime.of(2020,1,5, 15,0,20,0, ZoneId.of("GMT")),
                Util.tryParseToDate("Sun, 05-Jan-2020 15:00:20 GMT"));
    }

    @Test
    void parseLegacyFormat_whenChinese() {
        Locale.setDefault(Locale.CHINESE);
        assertEquals(ZonedDateTime.of(2020,3,6, 16,5,35,0, ZoneId.of("GMT")),
                Util.tryParseToDate("Fri, 06 Mar 2020 16:05:35 GMT"));
    }
}