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

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class JsonObjectMapper implements ObjectMapper {

    public static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm'Z'";
    private Gson gson;

    @Override
    public <T> T readValue(String value, Class<T> valueType) {
        return getGson().fromJson(value, valueType);
    }

    @Override
    public <T> T readValue(String value, GenericType<T> genericType) {
        return getGson().fromJson(value, genericType.getType());
    }

    @Override
    public String writeValue(Object value) {
        return getGson().toJson(value);
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setDateFormat(ISO_8601)
                    .registerTypeHierarchyAdapter(Calendar.class, new CalendarSerializer())
                    .registerTypeHierarchyAdapter(ZonedDateTime.class, new ZonedDateAdapter())
                    .registerTypeAdapter(Date.class, new DateAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

        }
        return gson;
    }

    private abstract static class DateTimeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        static final List<DateTimeFormatter> FORMATS = Arrays.asList(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME,
                DateTimeFormatter.ISO_DATE,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

        abstract T from(Long millies);
        abstract T from(TemporalAccessor dt);

        @Override
        public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if(jsonElement.isJsonPrimitive()) {
                JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
                if(prim.isNumber()){
                    return from(prim.getAsLong());
                } else if (prim.isString()){
                    String asString = prim.getAsString();
                    for (DateTimeFormatter format : FORMATS) {
                        try {
                            TemporalAccessor dt = format.parse(asString);
                            return from(dt);
                        } catch (Exception ignored) {
                            //System.out.println("ignored = " + ignored);
                        }
                    }
                }
            } else if (jsonElement.isJsonNull()){
                return null;
            }
            throw new UnirestException(String.format("Could Not Parse as %s: %s", type.getTypeName(), jsonElement.getAsString()));
        }

        @Override
        public JsonElement serialize(T dateObj, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dateObj.toString());
        }
    }

    private static class CalendarSerializer extends DateTimeAdapter<Calendar> {
        @Override
        public JsonElement serialize(Calendar calendar, Type type, JsonSerializationContext jsonSerializationContext) {
            GregorianCalendar gCal = (GregorianCalendar) calendar;
            String dateAsString = FORMATTER.format(gCal.toZonedDateTime());
            return jsonSerializationContext.serialize(dateAsString);
        }

        @Override
        Calendar from(Long millies) {
            return GregorianCalendar.from(ZonedDateTime.ofInstant(Instant.ofEpochMilli(millies), ZoneId.systemDefault()));
        }

        @Override
        Calendar from(TemporalAccessor dt) {
            return GregorianCalendar.from(toZonedInstance(dt));
        }

        private ZonedDateTime toZonedInstance(TemporalAccessor dt) {
            if(dt.isSupported(ChronoField.HOUR_OF_DAY)){
                return ZonedDateTime.from(dt);
            } else {
                return LocalDate.from(dt).atStartOfDay(ZoneId.systemDefault());
            }
        }
    }

    private static class DateAdapter extends DateTimeAdapter<Date> {
        @Override
        Date from(Long millies) {
            return new Date(millies);
        }

        @Override
        Date from(TemporalAccessor dt) {
            return Date.from(toInstant(dt));
        }

        private Instant toInstant(TemporalAccessor dt){
            if(dt.isSupported(ChronoField.HOUR_OF_DAY)){
                return LocalDateTime.from(dt).atZone(ZoneId.systemDefault()).toInstant();
            } else {
                return LocalDate.from(dt).atStartOfDay(ZoneId.systemDefault()).toInstant();
            }
        }

        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(FORMATTER.format(ZonedDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()))));
        }

    }

    private static class ZonedDateAdapter extends DateTimeAdapter<ZonedDateTime> {
        @Override
        ZonedDateTime from(Long millies) {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millies), ZoneId.systemDefault());
        }

        @Override
        ZonedDateTime from(TemporalAccessor dt) {
            if(dt.isSupported(ChronoField.HOUR_OF_DAY)){
                return ZonedDateTime.from(dt);
            } else {
                return LocalDateTime.from(dt).atZone(ZoneId.systemDefault());
            }
        }
    }

    private static class LocalDateTimeAdapter extends DateTimeAdapter<LocalDateTime> {
        @Override
        LocalDateTime from(Long millies) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(millies), ZoneId.systemDefault());
        }

        @Override
        LocalDateTime from(TemporalAccessor dt) {
            if(dt.isSupported(ChronoField.HOUR_OF_DAY)){
                return LocalDateTime.from(dt);
            } else {
                return LocalDate.from(dt).atStartOfDay();
            }
        }
    }

    private static class LocalDateAdapter extends DateTimeAdapter<LocalDate> {
        @Override
        LocalDate from(Long millies) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(millies), ZoneId.systemDefault()).toLocalDate();
        }

        @Override
        LocalDate from(TemporalAccessor dt) {
            return LocalDate.from(dt);
        }
    }
}
