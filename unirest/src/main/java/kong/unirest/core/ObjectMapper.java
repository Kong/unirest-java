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

/**
 * Interface for object mappers that can transform response bodies to other structures.
 */
public interface ObjectMapper {

	/**
	 * reads the content from the request as a string and transforms to a type passed by the
	 * asObject method on the Unirest builder.
	 * @param value the content as a string.
	 * @param valueType the type to map to
	 * @return the object mapped into the class type
	 * @param <T> the type
	 */
	<T> T readValue(String value, Class<T> valueType);

	/**
	 * reads the content from the request as a string and transforms to a type passed by the
	 * asObject method on the Unirest builder.
	 * This method takes a GenericType which retains Generics information for types lke List&lt;Foo&gt;
	 * @param value the content as a string.
	 * @param genericType the generic type
	 * @return the object mapped into the class type
	 * @param <T> the type
	 */
	default <T> T readValue(String value, GenericType<T> genericType){
		throw new UnirestException("Please implement me");
	}

	/**
	 * Takes a object and serialize it as a string.
	 * This is used to map objects to bodies to pass to requests
	 * @param value the object to serialize to a string
	 * @return the serialized string of the object
	 */
	String writeValue(Object value);
}
