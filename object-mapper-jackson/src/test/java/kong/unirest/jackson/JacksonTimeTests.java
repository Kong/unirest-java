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
import kong.unirest.core.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
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
        String actual = om.writeValue(new TestDates());
        assertEquals("{}", actual);
    }

    @Test
    void serializeDate_iso_with_time() {
        var date = getDate("1985-07-03T18:00:00.042Z");

        String actual = om.writeValue(date);

        assertEquals("{\"date\":489261600042}", actual);
    }

    @Test
    void serializeDate_iso_no_time() {
        var date = getDate(new Date(489196800000L));

        String actual = om.writeValue(date);

        assertEquals("{\"date\":489196800000}", actual);
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
        assertEquals("{\"calendar\":489261600042}", actual);
    }

    @Test
    @SuppressWarnings("MagicConstant")
    void canSerializeCalendar_no_time() {
        var cal = GregorianCalendar.getInstance();
        cal.set(1985, 6, 3, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        var test = getCalendar(cal);

        String actual = om.writeValue(test);
        assertEquals("{\"calendar\":489196800000}", actual);
    }

    @Test
    void deserializeCalendar_iso_unixDates() {
        var back = getTestDate("calendar", 489263400042L);

        assertEquals(489263400042L, back.getCalendar().getTimeInMillis());
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
    void canSerializeZonedDateTimes() {
        var test = new TestDates();
        test.setZonedDateTime(ZonedDateTime.parse("1985-07-03T18:00:00.042Z").withFixedOffsetZone());

        String actual = om.writeValue(test);
        assertEquals("{\"zonedDateTime\":489261600.042000000}", actual);
    }

    @Test
    void deserializeZonedDateTime_iso_datetime() {
        var back = getTestDate("zonedDateTime", "1985-07-03T18:30:00.042Z");
        var parse = ZonedDateTime.parse("1985-07-03T18:30:00.042Z");

        assertEquals(parse, back.getZonedDateTime());
    }

    @Test
    void deserializeZonedDateTime_iso_with_offset() {
        var back = getTestDate("zonedDateTime", "1985-07-03T18:30:00.042+02:00");
        var parse = ZonedDateTime.parse("1985-07-03T16:30:00.042Z");

        assertEquals(parse, back.getZonedDateTime());
    }

    @Test
    void canSerializeLocalDateTimes() {
        var test = new TestDates();
        test.setLocalDateTime(LocalDateTime.parse("1985-07-03T18:00:00.042"));

        String actual = om.writeValue(test);
        assertEquals("{\"localDateTime\":[1985,7,3,18,0,0,42000000]}", actual);
    }

    @Test
    void deserializeLocalDateTime_iso_datetime() {
        var back = getTestDate("localDateTime", "1985-07-03T18:00:00.042");

        assertEquals(LocalDateTime.parse("1985-07-03T18:00:00.042"), back.getLocalDateTime());
    }

    @Test
    void canSerializeLocalDate() {
        var test = new TestDates();
        test.setLocalDate(LocalDate.parse("1985-07-03"));

        String actual = om.writeValue(test);
        assertEquals("{\"localDate\":[1985,7,3]}", actual);
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