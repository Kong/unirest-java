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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A Strategy for performing retries.
 * Comes with a standard implementation which follows spec
 */
public interface RetryStrategy {
    /**
     * Checks to see if the response is retryable
     * @param response the last response
     * @return a bool indicating if the request should be retried
     */
    boolean isRetryable(HttpResponse<?> response);

    /**
     * Get the number of milliseconds the system should wait before retrying.
     * A value less than 1 will result in the termination of the retry loop
     * @param response the last response
     * @return millies
     */
    long getWaitTime(HttpResponse<?> response);

    /**
     * Get the max number of times the Unirest should retry responses before giving up and allowing a final return
     * @return the max attempts
     */
    int getMaxAttempts();

    /**
     * Puts the current executing thread to sleep for the specified millis
     * @param millies sleepy time millies
     */
    default void waitFor(long millies) {
        try {
            TimeUnit.MILLISECONDS.sleep(millies);
        } catch (InterruptedException e) {
            throw new UnirestException(e);
        }
    }

    /**
     * A standard implementation of the RetryStrategy which follows spec based Retry-After logic
     * - Will attempt a retry on any 301, 429, 503, or 529 response which is accompanied by a Retry-After header.
     * - Retry-After can be either date or seconds based
     * see https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Retry-After
     */
    class Standard implements RetryStrategy {
        private static final Set<Integer> RETRY_CODES = Set.of(301, 429, 503, 529);
        private static final String RETRY_AFTER = "Retry-After";
        private final int maxAttempts;

        public Standard(int maxAttempts){
            this.maxAttempts = maxAttempts;
        }

        @Override
        public boolean isRetryable(HttpResponse response) {
            return response != null && RETRY_CODES.contains(response.getStatus())
                    && response.getHeaders().containsKey(RETRY_AFTER);
        }

        @Override
        public long getWaitTime(HttpResponse response) {
            if(response == null){
                return 0;
            }
            String value = response.getHeaders().getFirst(RETRY_AFTER);
            return parseToMillies(value);
        }

        protected Long parseToMillies(String value) {
            return trySeconds(value)
                    .orElseGet(() -> tryAsDateTime(value));
        }

        @Override
        public int getMaxAttempts() {
            return maxAttempts;
        }

        private static Long tryAsDateTime(String value) {
            ZonedDateTime zdt = Util.tryParseToDate(value);
            if(zdt == null){
                return 0L;
            }
            Instant now = Util.now();
            return ChronoUnit.MILLIS.between(now, zdt.toInstant());
        }

        private static Optional<Long> trySeconds(String s){
            try{
                long seconds = parse(s);
                return Optional.of(seconds);
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
    }
}
