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

import java.util.Objects;

/**
 * Represents invocation count expectations for mock verification.
 * <p>
 * This abstract class provides factory methods to create various invocation count matchers
 * that can be used to verify how many times a mocked HTTP request was invoked.
 * </p>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Verify a request was called exactly once
 * mockClient.verify(HttpMethod.GET, "/api/users", Times.exactlyOnce());
 *
 * // Verify a request was called exactly 3 times
 * mockClient.verify(HttpMethod.POST, "/api/data", Times.exactly(3));
 *
 * // Verify a request was called at least once
 * mockClient.verify(HttpMethod.GET, "/api/health", Times.atLeastOnce());
 *
 * // Verify a request was never called
 * mockClient.verify(HttpMethod.DELETE, "/api/forbidden", Times.never());
 *
 * // Verify a request was called at most 5 times
 * mockClient.verify(HttpMethod.GET, "/api/resource", Times.atMost(5));
 * }</pre>
 *
 * @see EvaluationResult
 */
public abstract class Times {

    /**
     * Creates an expectation that the invocation occurred exactly once.
     * <p>
     * This is a convenience method equivalent to {@code exactly(1)}.
     * </p>
     *
     * @return a Times instance expecting exactly one invocation
     */
    public static Times exactlyOnce() {
        return exactly(1);
    }

    /**
     * Creates an expectation that the invocation occurred exactly the specified number of times.
     *
     * @param times the exact number of expected invocations
     * @return a Times instance expecting exactly the specified number of invocations
     */
    public static Times exactly(int times) {
        return new Exactly(times);
    }

    /**
     * Creates an expectation that the invocation occurred at least once.
     * <p>
     * This is a convenience method equivalent to {@code atLeast(1)}.
     * </p>
     *
     * @return a Times instance expecting at least one invocation
     */
    public static Times atLeastOnce() {
        return atLeast(1);
    }

    /**
     * Creates an expectation that the invocation occurred at least the specified number of times.
     *
     * @param times the minimum number of expected invocations
     * @return a Times instance expecting at least the specified number of invocations
     */
    public static Times atLeast(int times) {
        return new AtLeast(times);
    }

    /**
     * Creates an expectation that the invocation never occurred.
     * <p>
     * This is a convenience method equivalent to {@code exactly(0)}.
     * </p>
     *
     * @return a Times instance expecting zero invocations
     */
    public static Times never() {
        return exactly(0);
    }

    /**
     * Creates an expectation that the invocation occurred at most the specified number of times.
     *
     * @param times the maximum number of expected invocations
     * @return a Times instance expecting at most the specified number of invocations
     */
    public static Times atMost(int times) {
        return new AtMost(times);
    }

    /**
     * Evaluates whether the actual invocation count matches this expectation.
     *
     * @param number the actual number of invocations
     * @return an {@link EvaluationResult} indicating success or failure with a descriptive message
     */
    public abstract EvaluationResult matches(int number);

    /**
     * Times implementation that expects exactly a specific number of invocations.
     */
    private static class Exactly extends Times {
        private final int times;
        Exactly(int times) {
            this.times = times;
        }

        @Override
        public EvaluationResult matches(int number) {
            if(Objects.equals(number, times)){
                return EvaluationResult.success();
            } else {
                return EvaluationResult.fail("Expected exactly %s invocations but got %s", times, number);
            }
        }
    }

    /**
     * Times implementation that expects at least a specific number of invocations.
     */
    private static class AtLeast extends Times {
        private final int times;
        AtLeast(int times) {
            this.times = times;
        }

        @Override
        public EvaluationResult matches(int number) {
            if(number >= times) {
                return EvaluationResult.success();
            } else {
                return EvaluationResult.fail("Expected at least %s invocations but got %s", times, number);
            }
        }
    }

    /**
     * Times implementation that expects at most a specific number of invocations.
     */
    private static class AtMost extends Times {
        private final int times;

        public AtMost(int times) {
            this.times = times;
        }

        @Override
        public EvaluationResult matches(int number) {
            if (number <= times){
                return EvaluationResult.success();
            } else {
                return EvaluationResult.fail("Expected no more than %s invocations but got %s", times, number);
            }
        }
    }

    /**
     * Represents the result of evaluating an invocation count against a {@link Times} expectation.
     * <p>
     * An evaluation result contains a success flag and, in case of failure, a descriptive message
     * explaining why the expectation was not met.
     * </p>
     */
    public static class EvaluationResult {
        private static final EvaluationResult SUCCESS = new EvaluationResult(true, null);
        private final boolean success;
        private final String message;

        /**
         * Returns a successful evaluation result.
         * <p>
         * This method returns a cached singleton instance for efficiency.
         * </p>
         *
         * @return a successful EvaluationResult
         */
        public static EvaluationResult success(){
            return SUCCESS;
        }

        /**
         * Creates a failed evaluation result with a formatted message.
         *
         * @param message the message format string
         * @param values  the values to substitute into the message format
         * @return a failed EvaluationResult with the formatted message
         */
        public static EvaluationResult fail(String message, Object... values){
            return new EvaluationResult(false, String.format(message, values));
        }

        /**
         * Constructs a new EvaluationResult with the specified success status and message.
         *
         * @param success {@code true} if the evaluation succeeded, {@code false} otherwise
         * @param message the message describing the result, typically {@code null} for success
         *                and a descriptive error message for failure
         */
        public EvaluationResult(boolean success, String message){
            this.success = success;
            this.message = message;
        }

        /**
         * Returns whether the evaluation was successful.
         *
         * @return {@code true} if the invocation count matched the expectation, {@code false} otherwise
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Returns the message associated with this evaluation result.
         *
         * @return the message, or {@code null} if the evaluation was successful
         */
        public String getMessage() {
            return message;
        }
    }


}
