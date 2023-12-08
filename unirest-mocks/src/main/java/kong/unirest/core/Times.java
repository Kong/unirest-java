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

public abstract class Times {

    public static Times exactlyOnce() {
        return exactly(1);
    }

    public static Times exactly(int times) {
        return new Exactly(times);
    }

    public static Times atLeastOnce() {
        return atLeast(1);
    }

    public static Times atLeast(int times) {
        return new AtLeast(times);
    }

    public static Times never() {
        return exactly(0);
    }

    public static Times atMost(int times) {
        return new AtMost(times);
    }

    public abstract EvaluationResult matches(int number);

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

    public static class EvaluationResult {
        private static final EvaluationResult SUCCESS = new EvaluationResult(true, null);
        private final boolean success;
        private final String message;

        public static EvaluationResult success(){
            return SUCCESS;
        }

        public static EvaluationResult fail(String message, Object... values){
            return new EvaluationResult(false, String.format(message, values));
        }

        public EvaluationResult(boolean success, String message){
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }


}
