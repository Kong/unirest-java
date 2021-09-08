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
import java.time.temporal.ChronoUnit;
import java.util.Optional;

class RetryAfter {
    private long millies;

    public RetryAfter(Long millies) {
        this.millies = millies;
    }

    public static RetryAfter parse(Headers response) {
        String value = response.getFirst("Retry-After");
        return tryAsDouble(value)
                .orElseGet(() -> tryAsDateTime(value));
    }

    private static RetryAfter tryAsDateTime(String value) {
        Instant now = Util.now();
        Instant zdt = Util.tryParseToDate(value).toInstant();
        long diff = ChronoUnit.MILLIS.between(now, zdt);
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

    public long millies() {
        return millies;
    }

    public void waitForIt() {
        try {
            Thread.currentThread().sleep(millies);
        } catch (InterruptedException e) {
            throw new UnirestException(e);
        }
    }
}