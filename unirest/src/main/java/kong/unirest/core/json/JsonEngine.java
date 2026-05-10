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
 * Service provider interface for JSON processing engines.
 * <p>
 * This interface defines the contract that JSON libraries (such as GSON or Jackson)
 * must implement to be used as Unirest's JSON processing engine. It provides methods
 * for serialization, deserialization, and manipulation of JSON structures.
 * <p>
 * Implementations are discovered via Java's ServiceLoader mechanism or can be
 * configured explicitly.
 *
 * @see kong.unirest.core.json.CoreFactory
 */
public interface JsonEngine {

    /**
     * Converts a JSON element to a pretty-printed JSON string.
     *
     * @param obj the JSON element to convert
     * @return a formatted JSON string with indentation
     */
    String toPrettyJson(Element obj);

    /**
     * Converts a JSON element to a compact JSON string.
     *
     * @param obj the JSON element to convert
     * @return a compact JSON string without extra whitespace
     */
    String toJson(Element obj);

    /**
     * Writes a JSON element to a Writer as a compact JSON string.
     *
     * @param obj the JSON element to write
     * @param sw the writer to write to
     */
    void toJson(Element obj, Writer sw);

    /**
     * Writes a JSON element to a Writer as a pretty-printed JSON string.
     *
     * @param engineElement the JSON element to write
     * @param sw the writer to write to
     */
    void toPrettyJson(Element engineElement, Writer sw);

    /**
     * Converts a Java object to a JSON tree structure.
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
     * Parses a JSON string into a JSON object.
     *
     * @param string the JSON string to parse
     * @return the parsed JSON object
     * @throws JSONException if the string is not valid JSON
     */
    Object newEngineObject(String string) throws JSONException;

    /**
     * Parses a JSON string into a JSON array.
     *
     * @param jsonString the JSON string to parse
     * @return the parsed JSON array
     * @throws JSONException if the string is not a valid JSON array
     */
    Array newJsonArray(String jsonString) throws JSONException;

    /**
     * Creates a new JSON array from a Java collection.
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
     * Deserializes a JSON element to a Java object of the specified type.
     *
     * @param <T> the target type
     * @param obj the JSON element to deserialize
     * @param mapClass the class of the target type
     * @return the deserialized Java object
     */
    <T> T fromJson(Element obj, Class<T> mapClass);

    /**
     * Creates a new JSON primitive from an enum value.
     *
     * @param <T> the enum type
     * @param enumValue the enum value
     * @return a JSON primitive representing the enum
     */
    <T extends Enum> Primitive newJsonPrimitive(T enumValue);

    /**
     * Creates a new JSON primitive from a string value.
     *
     * @param string the string value
     * @return a JSON primitive representing the string
     */
    Primitive newJsonPrimitive(String string);

    /**
     * Creates a new JSON primitive from a numeric value.
     *
     * @param number the numeric value
     * @return a JSON primitive representing the number
     */
    Primitive newJsonPrimitive(Number number);

    /**
     * Creates a new JSON primitive from a boolean value.
     *
     * @param bool the boolean value
     * @return a JSON primitive representing the boolean
     */
    Primitive newJsonPrimitive(Boolean bool);

    /**
     * Returns the ObjectMapper associated with this JSON engine.
     *
     * @return the ObjectMapper for object serialization/deserialization
     */
    ObjectMapper getObjectMapper();

    /**
     * Quotes and escapes a value for use in a JSON string.
     *
     * @param s the value to quote
     * @return the quoted and escaped string
     */
    String quote(java.lang.Object s);

    /**
     * Base interface for all JSON element types.
     * <p>
     * Provides common methods for type checking and value extraction
     * from JSON elements.
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
         * @return {@code true} if this is a JSON null
         */
        boolean isJsonNull();

        /**
         * Returns this element as a JSON primitive.
         *
         * @return the JSON primitive
         */
        Primitive getAsJsonPrimitive();

        /**
         * Returns this element as a JSON array.
         *
         * @return the JSON array
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
         * Returns this element as a JSON primitive.
         *
         * @return the JSON primitive
         */
        Primitive getAsPrimitive();

        /**
         * Checks if this element is a JSON array.
         *
         * @return {@code true} if this is a JSON array
         */
        boolean isJsonArray();

        /**
         * Checks if this element is a JSON primitive.
         *
         * @return {@code true} if this is a JSON primitive
         */
        boolean isJsonPrimitive();

        /**
         * Checks if this element is a JSON object.
         *
         * @return {@code true} if this is a JSON object
         */
        boolean isJsonObject();

        /**
         * Returns the underlying engine-specific element.
         *
         * @param <T> the type of the engine element
         * @return the underlying engine element
         */
        <T> T getEngineElement();
    }

    /**
     * Represents a JSON array.
     * <p>
     * Provides methods for accessing, modifying, and iterating over
     * array elements.
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
         * @param index the index of the element
         * @return the element at the index
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
         * Sets a numeric value at the specified index.
         *
         * @param index the index
         * @param number the numeric value
         * @return this array for chaining
         */
        Element put(int index, Number number);

        /**
         * Sets a string value at the specified index.
         *
         * @param index the index
         * @param number the string value
         * @return this array for chaining
         */
        Element put(int index, String number);

        /**
         * Sets a boolean value at the specified index.
         *
         * @param index the index
         * @param number the boolean value
         * @return this array for chaining
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
         * @param index the index
         * @param o the element to set
         */
        void set(int index, Element o);

        /**
         * Adds a numeric value to the end of this array.
         *
         * @param num the numeric value to add
         */
        void add(Number num);

        /**
         * Adds a string value to the end of this array.
         *
         * @param str the string value to add
         */
        void add(String str);

        /**
         * Adds a boolean value to the end of this array.
         *
         * @param bool the boolean value to add
         */
        void add(Boolean bool);

        /**
         * Joins array elements into a string with the specified separator.
         *
         * @param token the separator token
         * @return the joined string
         */
        String join(String token);
    }

    /**
     * Represents a JSON object (key-value map).
     * <p>
     * Provides methods for accessing and modifying object properties.
     */
    interface Object extends Element {

        /**
         * Returns the number of properties in this object.
         *
         * @return the number of properties
         */
        int size();

        /**
         * Checks if this object has a property with the specified key.
         *
         * @param key the property key
         * @return {@code true} if the property exists
         */
        boolean has(String key);

        /**
         * Returns the value associated with the specified key.
         *
         * @param key the property key
         * @return the element value, or {@code null} if not found
         */
        Element get(String key);

        /**
         * Adds a JSON element as a property.
         *
         * @param key the property key
         * @param value the element value
         */
        void add(String key, Element value);

        /**
         * Adds a boolean property.
         *
         * @param key the property key
         * @param value the boolean value
         */
        void addProperty(String key, Boolean value);

        /**
         * Adds a string property.
         *
         * @param key the property key
         * @param value the string value
         */
        void addProperty(String key, String value);

        /**
         * Adds a numeric property.
         *
         * @param key the property key
         * @param value the numeric value
         */
        void addProperty(String key, Number value);

        /**
         * Adds an element property.
         *
         * @param key the property key
         * @param value the element value
         */
        void addProperty(String key, Element value);

        /**
         * Removes the property with the specified key.
         *
         * @param key the property key to remove
         */
        void remove(String key);

        /**
         * Adds an enum value as a property.
         *
         * @param <E> the enum type
         * @param key the property key
         * @param enumValue the enum value
         */
        <E extends Enum> void add(String key, E enumValue);

        /**
         * Returns the set of all property keys in this object.
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
         * @return {@code true} if this is a boolean
         */
        boolean isBoolean();

        /**
         * Checks if this primitive is a numeric value.
         *
         * @return {@code true} if this is a number
         */
        boolean isNumber();
    }
}
