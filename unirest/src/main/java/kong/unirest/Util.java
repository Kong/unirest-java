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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

class Util {
    public static final DateTimeFormatter DEFAULT_PATTERN = DateTimeFormatter.ofPattern("EEE, dd-MMM-yyyy HH:mm:ss zzz");
    static final List<DateTimeFormatter> FORMATS = Arrays.asList(
            DEFAULT_PATTERN,
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz")
    );
    private static Supplier<Instant> clock = Instant::now;

    static void freezeClock(Instant instant) {
        clock = () -> instant;
    }

    static void resetClock() {
        clock = Instant::now;
    }

    static Instant now() {
        return clock.get();
    }

    static <T, M extends T> Optional<M> tryCast(T original, Class<M> too) {
        if (original != null && too.isAssignableFrom(original.getClass())) {
            return Optional.of((M) original);
        }
        return Optional.empty();
    }

    //In Java 9 this has been added as Optional::stream. Remove this whenever we get there.
    static <T> Stream<T> stream(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }

    static String nullToEmpty(Object v) {
        if (v == null) {
            return "";
        }
        return String.valueOf(v);
    }

    static String encode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnirestException(e);
        }
    }

    static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String toBasicAuthValue(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    static FileInputStream getFileInputStream(String location) {
        try {
            return new FileInputStream(location);
        } catch (FileNotFoundException e) {
            throw new UnirestConfigException(e);
        }
    }

    public static ZonedDateTime tryParseToDate(String text) {
        return FORMATS.stream().map(f -> {
                    try {
                        return ZonedDateTime.parse(text, f);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
