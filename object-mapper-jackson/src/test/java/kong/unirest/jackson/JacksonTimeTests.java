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

package kong.unirest.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class JacksonTimeTests {

    JacksonObjectMapper om = new JacksonObjectMapper();

    @BeforeEach
    void before() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    void serializeEverythingNull() {
        TestDates test = new TestDates();
        String actual = om.writeValue(test);
        assertEquals("{}", actual);
    }

    @Test
    void serializeDate_iso_with_time() {
        TestDates date = getDate("1985-07-03T18:00:00.042Z");

        String actual = om.writeValue(date);

        assertEquals("{\"date\":489261600042}", actual);
    }

    @Test
    void serializeDate_iso_no_time() {
        TestDates date = getDate(new Date(489196800000L));

        String actual = om.writeValue(date);

        assertEquals("{\"date\":489196800000}", actual);
    }

    @Test
    void deserializeDate_null() {
        TestDates back = getTestDate("date", null);

        assertNull(back.getDate());
    }

    @Test
    void deserializeDate_iso_with_time() {
        TestDates back = getTestDate("date", "1985-07-03T18:30:00.042Z");

        assertEquals(489263400042L, back.getDate().getTime());
    }

    @Test
    void deserializeDate_iso_with_NoTime() {
        TestDates back = getTestDate("date", "1985-07-03");

        assertEquals(489196800000L, back.getDate().getTime());
    }

    @Test
    void deserializeDate_iso_datetime_noMillies() {
        TestDates back = getTestDate("date", "1985-07-03T18:30:00Z");

        assertEquals(489263400000L, back.getDate().getTime());
    }

    @Test
    void deserializeDate_iso_datetime_noSeconds() {
        TestDates back = getTestDate("date", "1985-07-03T18:30Z");

        assertEquals(489263400000L, back.getDate().getTime());
    }

    @Test
    void getDatesFromNumbers() {
        TestDates back = getTestDate("date", 42);

        assertEquals(new Date(42), back.getDate());
    }

    @Test
    void canSerializeCalendar() {
        TestDates test = getCalendar("1985-07-03T18:00:00.042Z");

        String actual = om.writeValue(test);
        assertEquals("{\"calendar\":489261600042}", actual);
    }

    @Test
    @SuppressWarnings("MagicConstant")
    void canSerializeCalendar_no_time() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(1985, 6, 3, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        TestDates test = getCalendar(cal);

        String actual = om.writeValue(test);
        assertEquals("{\"calendar\":489196800000}", actual);
    }

    @Test
    void deserializeCalendar_iso_unixDates() {
        TestDates back = getTestDate("calendar", 489263400042L);

        assertEquals(489263400042L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void deserializeCalendar_iso_datetime() {
        TestDates back = getTestDate("calendar", "1985-07-03T18:30:00.042Z");

        assertEquals(489263400042L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void deserializeCalendar_iso_datetime_noMillies() {
        TestDates back = getTestDate("calendar", "1985-07-03T18:30:00Z");

        assertEquals(489263400000L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void deserializeCalendar_iso_datetime_noSeconds() {
        TestDates back = getTestDate("calendar", "1985-07-03T18:30Z");

        assertEquals(489263400000L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void deserializeCalendar_iso_date() {
        TestDates back = getTestDate("calendar", "1985-07-03");

        assertEquals(489196800000L, back.getCalendar().getTimeInMillis());
    }

    @Test
    void canSerializeZonedDateTimes() {
        ZonedDateTime zonedDt = ZonedDateTime.parse("1985-07-03T18:00:00.042Z").withFixedOffsetZone();
        TestDates test = new TestDates();
        test.setZonedDateTime(zonedDt);

        String actual = om.writeValue(test);
        assertEquals("{\"zonedDateTime\":489261600.042000000}", actual);
    }

    @Test
    void deserializeZonedDateTime_iso_datetime() {
        TestDates back = getTestDate("zonedDateTime", "1985-07-03T18:30:00.042Z");
        ZonedDateTime parse = ZonedDateTime.parse("1985-07-03T18:30:00.042+02:00").withZoneSameLocal(ZoneId.of("UTC"));

        assertEquals(parse, back.getZonedDateTime());
    }

    @Test
    void deserializeZonedDateTime_iso_with_offset() {
        TestDates back = getTestDate("zonedDateTime", "1985-07-03T18:30:00.042+02:00");

        ZonedDateTime parse = ZonedDateTime.parse("1985-07-03T16:30:00.042+02:00").withZoneSameLocal(ZoneId.of("UTC"));
        assertEquals(parse, back.getZonedDateTime());
    }

    @Test
    void canSerializeLocalDateTimes() {
        LocalDateTime zonedDt = LocalDateTime.parse("1985-07-03T18:00:00.042");
        TestDates test = new TestDates();
        test.setLocalDateTime(zonedDt);

        String actual = om.writeValue(test);
        assertEquals("{\"localDateTime\":[1985,7,3,18,0,0,42000000]}", actual);
    }

    @Test
    void deserializeLocalDateTime_iso_datetime() {
        TestDates back = getTestDate("localDateTime", "1985-07-03T18:00:00.042");

        assertEquals(LocalDateTime.parse("1985-07-03T18:00:00.042"), back.getLocalDateTime());
    }



    @Test
    void canSerializeLocalDate() {
        LocalDate zonedDt = LocalDate.parse("1985-07-03");
        TestDates test = new TestDates();
        test.setLocalDate(zonedDt);

        String actual = om.writeValue(test);
        assertEquals("{\"localDate\":[1985,7,3]}", actual);
    }

    @Test
    void deserializeLocalDate_iso_datetime() {
        TestDates back = getTestDate("localDate", "1985-07-03T18:00:00.042");

        assertEquals(LocalDate.parse("1985-07-03"), back.getLocalDate());
    }

    @Test
    void deserializeLocalDate_iso_datetime_noTime() {
        TestDates back = getTestDate("localDate", "1985-07-03");

        assertEquals(LocalDate.parse("1985-07-03"), back.getLocalDate());
    }

    @Test
    void doNotEscapeHTML() {
        TestString s = new TestString();
        s.test = "it's a && b || c + 1!?";

        String res = om.writeValue(s);

        assertEquals("{\"test\":\"it's a && b || c + 1!?\"}", res);
    }

    public static class TestString {
        public String test;
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
        TestDates test = new TestDates();
        test.setDate(from);
        return test;
    }

    @SuppressWarnings("SameParameterValue")
    private TestDates getCalendar(String date) {
        Calendar from = GregorianCalendar.from(ZonedDateTime.parse(date));
        return getCalendar(from);
    }

    private TestDates getCalendar(Calendar from) {
        TestDates test = new TestDates();
        test.setCalendar(from);
        return test;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
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