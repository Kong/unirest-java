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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A type reference holder that preserves generic type information at runtime.
 * <p>
 * This class works around Java's type erasure by capturing generic type parameters
 * through subclassing. Create an anonymous subclass to preserve the type information:
 * <pre>{@code
 * GenericType<List<Integer>> ref = new GenericType<List<Integer>>() { };
 * }</pre>
 * <p>
 * This technique is commonly used in libraries like Jackson, GSON, and Spring
 * for object mapping with generic types.
 * <p>
 * Parts of this implementation were derived from Jackson's TypeReference
 * under the Apache License 2.0.
 *
 * @param <T> the generic type to represent
 * @see <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>
 */
public abstract class GenericType<T> implements Comparable<GenericType<T>> {
    protected final Type type;

    /**
     * Creates a new GenericType instance, capturing the generic type information.
     * <p>
     * This constructor must be called from a subclass (typically an anonymous class)
     * that provides the actual type argument.
     *
     * @throws IllegalArgumentException if instantiated without actual type information
     */
    protected GenericType() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this.type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        }
    }

    /**
     * Returns the captured generic type.
     *
     * @return the {@link Type} which includes generic type information
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Compares this GenericType with another for ordering.
     * <p>
     * This implementation always returns 0, indicating all GenericType instances
     * are considered equal for ordering purposes.
     *
     * @param o the GenericType to compare with
     * @return always returns 0
     */
    public int compareTo(GenericType<T> o) {
        return 0;
    }

    /**
     * Returns the runtime class of the type.
     *
     * @return the {@link Class} object representing the type's runtime class
     */
    public Class<?> getTypeClass(){
        return this.type.getClass();
    }
}
