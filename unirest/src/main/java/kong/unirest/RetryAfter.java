package kong.unirest;

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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class RetryAfter {
    private static final Set<Integer> RETRY_CODES = new HashSet<>(Arrays.asList(429, 529, 301));
    private static final String RETRY_AFTER = "Retry-After";;
    private static final RetryAfter EMPTY = new RetryAfter(0L);
    private long millies;

    RetryAfter(Long millies) {
        this.millies = millies;
    }

    static RetryAfter from(HttpResponse response) {
        return from(response.getHeaders());
    }

    static boolean isRetriable(HttpResponse response) {
        return RETRY_CODES.contains(response.getStatus()) && response.getHeaders().containsKey(RETRY_AFTER);
    }

    static RetryAfter from(Headers response) {
        String value = response.getFirst(RETRY_AFTER);
        return tryAsDouble(value)
                .orElseGet(() -> tryAsDateTime(value));
    }

    private static RetryAfter tryAsDateTime(String value) {
        ZonedDateTime zdt = Util.tryParseToDate(value);
        if(zdt == null){
            return RetryAfter.EMPTY;
        }
        Instant now = Util.now();
        long diff = ChronoUnit.MILLIS.between(now, zdt.toInstant());
        return new RetryAfter(diff);
    }

    private static Optional<RetryAfter> tryAsDouble(String s){
        try{
            long millies = parse(s);
            return Optional.of(new RetryAfter(millies));
        }catch (NumberFormatException e){
            return Optional.empty();
        }
    }

    private static long parse(String s) {
        if(s.contains(".")){
            double d = Double.parseDouble(s);
            return Math.round(d * 1000);
        }
        return Long.parseLong(s) * 1000;
    }

    long millies() {
        return millies;
    }

    void waitForIt() {
        try {
            TimeUnit.MILLISECONDS.sleep(millies);
        } catch (InterruptedException e) {
            throw new UnirestException(e);
        }
    }

    boolean canWait() {
        return millies > 0;
    }
}