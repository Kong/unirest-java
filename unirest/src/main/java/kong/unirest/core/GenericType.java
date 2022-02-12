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
 * Parts of this file were taken from Jackson/core TypeReference under the Apache License:
 *
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  
 * A class to hold onto generic type params for object mapping by creating a anonymous subtype.
 * This is a common "trick" commonly used in Java to avoid issues with type erasure.
 *
 * Other examples can be found in popular libraries like Jackson, GSON, and Spring
 *
 * <pre>
 *  GenericType ref = new GenericType&lt;List&lt;Integer&gt;&gt;() { };
 * </pre>
 * @param <T> the generic type you wish to represent.
 */
public abstract class GenericType<T> implements Comparable<GenericType<T>> {
    protected final Type type;

    protected GenericType() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this.type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        }
    }

    /**
     * @return the Type which includes generic type information
     */
    public Type getType() {
        return this.type;
    }

    public int compareTo(GenericType<T> o) {
        return 0;
    }

    public Class<?> getTypeClass(){
        return this.type.getClass();
    }
}
