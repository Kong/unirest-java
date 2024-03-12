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

package kong.unirest.mappers.gson;

import kong.unirest.core.UnirestException;
import kong.unirest.core.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class JsonObjectMapperTest {

    GsonObjectMapper om = new GsonObjectMapper();

    @BeforeEach
    void before() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    }

    @Test
    void serializeEverythingNull() {
        var test = new TestDates();
        String actual = om.writeValue(test);
        assertEquals("{}", actual);
    }

    @Test
    void serializeDate_iso_with_time() {
        var date = getDate("1985-07-03T18:00:00.042Z");

        String actual = om.writeValue(date);

        assertEquals("{\"date\":\"1985-07-03T18:00:00.042Z\"}", actual);
    }

    @Test
    void serializeDate_iso_no_time() {
        var date = getDate(new Date(489196800000L));

        String actual = om.writeValue(date);

        assertEquals("{\"date\":\"1985-07-03T00:00:00Z\"}", actual);
    }

    @Test
    void deserializeDate_null() {
        var back = getTestDate("date", null);

        assertNull(back.getDate());
    }

    @Test
    void deserializeDate_iso_with_time() {
        var back = getTestDate("date", "1985-07-03T18:30:00.042Z");

        assertEquals(489263400042L, back.getDate().getTime());
    }

    @Test
    void deserializeDate_iso_with_NoTime() {
        var back = getTestDate("date", "1985-07-03");

        assertEquals(489196800000L, back.getDate().getTime());
    }

    @Test
    void deserializeDate_iso_datetime_noMillies() {
        var back = getTestDate("date", "1985-07-03T18:30:00Z");

        assertEquals(489263400000L, back.getDate().getTime());
    }

    @Test
    void deserializeDate_iso_datetime_Errors() {
        var ex = assertThrows(UnirestException.class, () -> getTestDate("date", "Leeeeeeeroy Jenkins!"));
        assertEquals("Could Not Parse as java.util.Date: Leeeeeeeroy Jenkins!", ex.getMessage());
    }

    @Test
    void deserializeDate_iso_datetime_noSeconds() {
        var back = getTestDate("date", "1985-07-03T18:30Z");

        assertEquals(489263400000L, back.getDate().getTime());
    }

    @Test
    void getDatesFromNumbers() {
        var back = getTestDate("date", 42);

        assertEquals(new Date(42), back.getDate());
    }

    @Test
    void canSerializeCalendar() {
        var test = getCalendar("1985-07-03T18:00:00.042Z");

        String actual = om.writeValue(test);
        assertEquals("{\"calendar\":\"1985-07-03T18:00:00.042Z\"}", actual);
    }

    @Test
    @SuppressWarnings("MagicConstant")
    void canSerializeCalendar_no_time() {
        var cal = GregorianCalendar.getInstance();
        cal.set(1985, 6, 3, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        var test = getCalendar(cal);

        String actual = om.writeValue(test);
        assertEquals("{\"calendar\":\"1985-07-03T00:00:00Z\"}", actual);
    }

    @Test
    void deserializeCalendar_iso_datetime() {
        var back = getTestDate("calendar", "1985-07-03T18:30:00.042Z");

        assertEquals(489263400042L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void deserializeCalendar_iso_datetime_noMillies() {
        var back = getTestDate("calendar", "1985-07-03T18:30:00Z");

        assertEquals(489263400000L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void deserializeCalendar_iso_datetime_noSeconds() {
        var back = getTestDate("calendar", "1985-07-03T18:30Z");

        assertEquals(489263400000L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void deserializeCalendar_iso_date() {
        var back = getTestDate("calendar", "1985-07-03");

        assertEquals(489196800000L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void deserializeCalendar_iso_datetime_Errors() {
        var ex = assertThrows(UnirestException.class, () -> getTestDate("calendar", "Leeeeeeeroy Jenkins!"));
        assertEquals("Could Not Parse as java.util.Calendar: Leeeeeeeroy Jenkins!", ex.getMessage());
    }

    @Test
    void canSerializeZonedDateTimes() {
        var zonedDt = ZonedDateTime.parse("1985-07-03T18:00:00.042Z").withFixedOffsetZone();
        var test = new TestDates();
        test.setZonedDateTime(zonedDt);

        String actual = om.writeValue(test);
        assertEquals("{\"zonedDateTime\":\"1985-07-03T18:00:00.042Z\"}", actual);
    }

    @Test
    void deserializeZonedDateTime_iso_datetime() {
        var back = getTestDate("zonedDateTime", "1985-07-03T18:30:00.042Z");

        assertEquals(ZonedDateTime.parse("1985-07-03T18:30:00.042Z"), back.getZonedDateTime());
    }

    @Test
    void deserializeZonedDateTime_iso_with_offset() {
        var back = getTestDate("zonedDateTime", "1985-07-03T18:30:00.042+02:00");

        assertEquals(ZonedDateTime.parse("1985-07-03T18:30:00.042+02:00"), back.getZonedDateTime());
    }

    @Test
    void canSerializeLocalDateTimes() {
        var zonedDt = LocalDateTime.parse("1985-07-03T18:00:00.042");
        var test = new TestDates();
        test.setLocalDateTime(zonedDt);

        String actual = om.writeValue(test);
        assertEquals("{\"localDateTime\":\"1985-07-03T18:00:00.042\"}", actual);
    }

    @Test
    void deserializeLocalDateTime_iso_datetime() {
        var back = getTestDate("localDateTime", "1985-07-03T18:00:00.042");

        assertEquals(LocalDateTime.parse("1985-07-03T18:00:00.042"), back.getLocalDateTime());
    }

    @Test
    void deserializeLocalDateTime_iso_datetime_noTime() {
        var back = getTestDate("localDateTime", "1985-07-03");

        assertEquals(LocalDateTime.parse("1985-07-03T00:00"), back.getLocalDateTime());
    }

    @Test
    void canSerializeLocalDate() {
        var zonedDt = LocalDate.parse("1985-07-03");
        var test = new TestDates();
        test.setLocalDate(zonedDt);

        String actual = om.writeValue(test);
        assertEquals("{\"localDate\":\"1985-07-03\"}", actual);
    }

    @Test
    void deserializeLocalDate_iso_datetime() {
        var back = getTestDate("localDate", "1985-07-03T18:00:00.042");

        assertEquals(LocalDate.parse("1985-07-03"), back.getLocalDate());
    }

    @Test
    void deserializeLocalDate_iso_datetime_noTime() {
        var back = getTestDate("localDate", "1985-07-03");

        assertEquals(LocalDate.parse("1985-07-03"), back.getLocalDate());
    }

    @Test
    void doNotEscapeHTML() {
        var s = new TestString();
        s.test = "it's a && b || c + 1!?";

        String res = om.writeValue(s);

        assertEquals("{\"test\":\"it's a && b || c + 1!?\"}", res);
    }

    private static class TestString {
        private String test;
    }

    private TestDates getTestDate(String key, Object date) {
        return om.readValue(getJson(key, date), TestDates.class);
    }

    private String getJson(String key, Object value){
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private TestDates getDate(String date) {
        Date from = Date.from(ZonedDateTime.parse(date).withFixedOffsetZone().toInstant());
        return getDate(from);
    }

    private TestDates getDate(Date from) {
        var test = new TestDates();
        test.setDate(from);
        return test;
    }

    @SuppressWarnings("SameParameterValue")
    private TestDates getCalendar(String date) {
        Calendar from = GregorianCalendar.from(ZonedDateTime.parse(date));
        return getCalendar(from);
    }

    private TestDates getCalendar(Calendar from) {
        var test = new TestDates();
        test.setCalendar(from);
        return test;
    }

    public static class TestDates {

        private Date date;
        private Calendar calendar;
        private ZonedDateTime zonedDateTime;
        private LocalDateTime localDateTime;
        private LocalDate localDate;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }

        public ZonedDateTime getZonedDateTime() {
            return zonedDateTime;
        }

        public void setZonedDateTime(ZonedDateTime zonedDateTime) {
            this.zonedDateTime = zonedDateTime;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }
    }
}