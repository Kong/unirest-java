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

package kong.unirest.apache;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

class Util {
    static <T, M extends T> Optional<M> tryCast(T original, Class<M> too) {
        if (original != null && too.isAssignableFrom(original.getClass())) {
            return Optional.of((M) original);
        }
        return Optional.empty();
    }

    static Stream<Exception> collectExceptions(Optional<Exception>... ex) {
        return Stream.of(ex).flatMap(Util::stream);
    }

    //In Java 9 this has been added as Optional::stream. Remove this whenever we get there.
    static <T> Stream<T> stream(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }

    static <T> Optional<Exception> tryDo(T c, ExConsumer<T> consumer) {
        try {
            if (Objects.nonNull(c)) {
                consumer.accept(c);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    @FunctionalInterface
    public interface ExConsumer<T>{
        void accept(T t) throws Exception;
    }

    static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
