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

package kong.unirest.core.json;

import kong.unirest.core.ObjectMapper;

import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

/**
 * Defines the contract for a JSON processing engine used by Unirest.
 * <p>
 * This interface abstracts the underlying JSON library (such as GSON or Jackson),
 * allowing Unirest to work with different JSON implementations interchangeably.
 * Implementations of this interface provide serialization, deserialization,
 * and manipulation capabilities for JSON data structures.
 * </p>
 *
 * @see Element
 * @see Array
 * @see Object
 * @see Primitive
 */
public interface JsonEngine {

    /**
     * Converts a JSON element to a pretty-printed JSON string with indentation.
     *
     * @param obj the JSON element to convert
     * @return a formatted JSON string representation
     */
    String toPrettyJson(Element obj);

    /**
     * Converts a JSON element to a compact JSON string.
     *
     * @param obj the JSON element to convert
     * @return a compact JSON string representation
     */
    String toJson(Element obj);

    /**
     * Writes a JSON element as a compact JSON string to a writer.
     *
     * @param obj the JSON element to write
     * @param sw  the writer to write the JSON to
     */
    void toJson(Element obj, Writer sw);

    /**
     * Writes a JSON element as a pretty-printed JSON string to a writer.
     *
     * @param engineElement the JSON element to write
     * @param sw            the writer to write the JSON to
     */
    void toPrettyJson(Element engineElement, Writer sw);

    /**
     * Converts a Java object to a JSON element tree representation.
     *
     * @param obj the Java object to convert
     * @return the JSON element representation of the object
     */
    Element toJsonTree(java.lang.Object obj);

    /**
     * Creates a new empty JSON object.
     *
     * @return a new empty JSON object
     */
    Object newEngineObject();

    /**
     * Parses a JSON string and creates a JSON object.
     *
     * @param string the JSON string to parse
     * @return the parsed JSON object
     * @throws JSONException if the string is not valid JSON
     */
    Object newEngineObject(String string) throws JSONException;

    /**
     * Parses a JSON string and creates a JSON array.
     *
     * @param jsonString the JSON string to parse
     * @return the parsed JSON array
     * @throws JSONException if the string is not a valid JSON array
     */
    Array newJsonArray(String jsonString) throws JSONException;

    /**
     * Creates a JSON array from a Java collection.
     *
     * @param collection the collection to convert
     * @return a JSON array containing the collection elements
     */
    Array newJsonArray(Collection<?> collection);

    /**
     * Creates a new empty JSON array.
     *
     * @return a new empty JSON array
     */
    Array newEngineArray();

    /**
     * Deserializes a JSON element to a Java object of the specified class.
     *
     * @param obj      the JSON element to deserialize
     * @param mapClass the target class type
     * @param <T>      the type of the target class
     * @return the deserialized Java object
     */
    <T> T fromJson(Element obj, Class<T> mapClass);

    /**
     * Creates a JSON primitive from an enum value.
     *
     * @param enumValue the enum value to wrap
     * @param <T>       the enum type
     * @return a JSON primitive representing the enum
     */
    <T extends Enum> Primitive newJsonPrimitive(T enumValue);

    /**
     * Creates a JSON primitive from a string value.
     *
     * @param string the string value to wrap
     * @return a JSON primitive representing the string
     */
    Primitive newJsonPrimitive(String string);

    /**
     * Creates a JSON primitive from a number value.
     *
     * @param number the number value to wrap
     * @return a JSON primitive representing the number
     */
    Primitive newJsonPrimitive(Number number);

    /**
     * Creates a JSON primitive from a boolean value.
     *
     * @param bool the boolean value to wrap
     * @return a JSON primitive representing the boolean
     */
    Primitive newJsonPrimitive(Boolean bool);

    /**
     * Returns the object mapper associated with this JSON engine.
     *
     * @return the object mapper instance
     */
    ObjectMapper getObjectMapper();

    /**
     * Quotes and escapes an object's string representation for use in JSON.
     *
     * @param s the object to quote
     * @return a properly quoted and escaped JSON string
     */
    String quote(java.lang.Object s);

    /**
     * Represents a JSON element which can be an object, array, primitive, or null.
     * <p>
     * This is the base interface for all JSON value types in the engine.
     * </p>
     */
    interface Element {

        /**
         * Returns this element as a JSON object.
         *
         * @return the underlying JSON object
         */
        Object getAsJsonObject();

        /**
         * Checks if this element represents a JSON null value.
         *
         * @return {@code true} if this element is null
         */
        boolean isJsonNull();

        /**
         * Returns this element as a JSON primitive.
         *
         * @return the JSON primitive representation
         */
        Primitive getAsJsonPrimitive();

        /**
         * Returns this element as a JSON array.
         *
         * @return the JSON array representation
         */
        Array getAsJsonArray();

        /**
         * Returns this element's value as a float.
         *
         * @return the float value
         */
        float getAsFloat();

        /**
         * Returns this element's value as a double.
         *
         * @return the double value
         */
        double getAsDouble();

        /**
         * Returns this element's value as a string.
         *
         * @return the string value
         */
        String getAsString();

        /**
         * Returns this element's value as a long.
         *
         * @return the long value
         */
        long getAsLong();

        /**
         * Returns this element's value as an int.
         *
         * @return the int value
         */
        int getAsInt();

        /**
         * Returns this element's value as a boolean.
         *
         * @return the boolean value
         */
        boolean getAsBoolean();

        /**
         * Returns this element's value as a BigInteger.
         *
         * @return the BigInteger value
         */
        BigInteger getAsBigInteger();

        /**
         * Returns this element's value as a BigDecimal.
         *
         * @return the BigDecimal value
         */
        BigDecimal getAsBigDecimal();

        /**
         * Returns this element as a primitive.
         *
         * @return the primitive representation
         */
        Primitive getAsPrimitive();

        /**
         * Checks if this element is a JSON array.
         *
         * @return {@code true} if this element is an array
         */
        boolean isJsonArray();

        /**
         * Checks if this element is a JSON primitive.
         *
         * @return {@code true} if this element is a primitive
         */
        boolean isJsonPrimitive();

        /**
         * Checks if this element is a JSON object.
         *
         * @return {@code true} if this element is an object
         */
        boolean isJsonObject();

        /**
         * Returns the underlying engine-specific element.
         *
         * @param <T> the type of the engine element
         * @return the native JSON engine element
         */
        <T> T getEngineElement();
    }

    /**
     * Represents a JSON array containing an ordered list of elements.
     */
    interface Array extends Element {

        /**
         * Returns the number of elements in this array.
         *
         * @return the size of the array
         */
        int size();

        /**
         * Returns the element at the specified index.
         *
         * @param index the index of the element to retrieve
         * @return the element at the specified index
         */
        Element get(int index);

        /**
         * Removes and returns the element at the specified index.
         *
         * @param index the index of the element to remove
         * @return the removed element
         */
        Element remove(int index);

        /**
         * Sets a number value at the specified index.
         *
         * @param index  the index to set
         * @param number the number value to set
         * @return this array for method chaining
         */
        Element put(int index, Number number);

        /**
         * Sets a string value at the specified index.
         *
         * @param index  the index to set
         * @param number the string value to set
         * @return this array for method chaining
         */
        Element put(int index, String number);

        /**
         * Sets a boolean value at the specified index.
         *
         * @param index  the index to set
         * @param number the boolean value to set
         * @return this array for method chaining
         */
        Element put(int index, Boolean number);

        /**
         * Adds an element to the end of this array.
         *
         * @param obj the element to add
         */
        void add(Element obj);

        /**
         * Sets an element at the specified index.
         *
         * @param index the index to set
         * @param o     the element to set
         */
        void set(int index, Element o);

        /**
         * Adds a number to the end of this array.
         *
         * @param num the number to add
         */
        void add(Number num);

        /**
         * Adds a string to the end of this array.
         *
         * @param str the string to add
         */
        void add(String str);

        /**
         * Adds a boolean to the end of this array.
         *
         * @param bool the boolean to add
         */
        void add(Boolean bool);

        /**
         * Joins all elements in this array with the specified token.
         *
         * @param token the separator token
         * @return a string with all elements joined by the token
         */
        String join(String token);
    }

    /**
     * Represents a JSON object containing key-value pairs.
     */
    interface Object extends Element {

        /**
         * Returns the number of key-value pairs in this object.
         *
         * @return the size of the object
         */
        int size();

        /**
         * Checks if this object contains the specified key.
         *
         * @param key the key to check
         * @return {@code true} if the key exists
         */
        boolean has(String key);

        /**
         * Returns the element associated with the specified key.
         *
         * @param key the key to look up
         * @return the element associated with the key, or null if not found
         */
        Element get(String key);

        /**
         * Adds an element with the specified key.
         *
         * @param key   the key
         * @param value the element value
         */
        void add(String key, Element value);

        /**
         * Adds a boolean property with the specified key.
         *
         * @param key   the key
         * @param value the boolean value
         */
        void addProperty(String key, Boolean value);

        /**
         * Adds a string property with the specified key.
         *
         * @param key   the key
         * @param value the string value
         */
        void addProperty(String key, String value);

        /**
         * Adds a number property with the specified key.
         *
         * @param key   the key
         * @param value the number value
         */
        void addProperty(String key, Number value);

        /**
         * Adds an element property with the specified key.
         *
         * @param key   the key
         * @param value the element value
         */
        void addProperty(String key, Element value);

        /**
         * Removes the property with the specified key.
         *
         * @param key the key to remove
         */
        void remove(String key);

        /**
         * Adds an enum value with the specified key.
         *
         * @param key       the key
         * @param enumValue the enum value
         * @param <E>       the enum type
         */
        <E extends Enum> void add(String key, E enumValue);

        /**
         * Returns a set of all keys in this object.
         *
         * @return the set of keys
         */
        Set<String> keySet();
    }

    /**
     * Represents a JSON primitive value (string, number, or boolean).
     */
    interface Primitive extends Element {

        /**
         * Checks if this primitive is a boolean value.
         *
         * @return {@code true} if this primitive is a boolean
         */
        boolean isBoolean();

        /**
         * Checks if this primitive is a number value.
         *
         * @return {@code true} if this primitive is a number
         */
        boolean isNumber();
    }
}
