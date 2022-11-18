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