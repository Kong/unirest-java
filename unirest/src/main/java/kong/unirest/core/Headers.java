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

import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Represents a collection of HTTP headers.
 * <p>
 * This class provides methods for adding, removing, and querying HTTP headers.
 * Headers are stored as name-value pairs, and multiple values can be associated
 * with the same header name. Header names are case-insensitive for lookups.
 *
 * @see Header
 */
public class Headers {

    private static final long serialVersionUID = 71310341388734766L;
    private List<Header> headers = new ArrayList<>();

    /**
     * Creates an empty Headers collection.
     */
    public Headers() {
    }

    /**
     * Creates a Headers collection initialized with the specified entries.
     *
     * @param entries the collection of header entries to add
     */
    public Headers(Collection<Entry> entries) {
        entries.forEach(e -> add(e.name, e.value));
    }

    /**
     * Adds a header element.
     *
     * @param name the name of the header
     * @param value the value for the header
     */
    public void add(String name, String value) {
        add(name, () -> value);
    }

    /**
     * Adds a header element with a supplier which will be evaluated on request.
     *
     * @param name the name of the header
     * @param value a supplier providing the value for the header
     */
    public void add(String name, Supplier<String> value) {
        if (Objects.nonNull(name)) {
            headers.add(new Entry(name, value));
        }
    }

    /**
     * Replaces a header value.
     * <p>
     * If there are multiple instances of the header, all of them will be removed
     * and replaced with the new value.
     *
     * @param name the name of the header
     * @param value the value for the header
     */
    public void replace(String name, String value) {
        remove(name);
        add(name, value);
    }

    /**
     * Removes all headers with the specified name.
     *
     * @param name the name of the header to remove
     */
    public void remove(String name) {
        headers.removeIf(h -> isName(h, name));
    }

    /**
     * Returns the number of unique header names.
     *
     * @return the count of distinct header names
     */
    public int size() {
        return headers.stream().map(Header::getName).collect(toSet()).size();
    }

    /**
     * Returns all values for a header name.
     *
     * @param name the name of the header
     * @return a list of values for the specified header name
     */
    public List<String> get(String name) {
        return headers.stream()
                .filter(h -> isName(h, name))
                .map(Header::getValue)
                .collect(toList());
    }

    /**
     * Adds all headers from another Headers collection.
     *
     * @param other the headers to add
     */
    public void putAll(Headers other) {
        if(other != null) {
            this.headers.addAll(other.headers);
        }
    }

    /**
     * Checks if a header with the specified name exists.
     *
     * @param name the header name to check
     * @return {@code true} if a header with the name exists, {@code false} otherwise
     */
    public boolean containsKey(String name) {
        return this.headers.stream().anyMatch(h -> isName(h, name));
    }

    /**
     * Removes all headers from this collection.
     */
    public void clear() {
        this.headers.clear();
    }

    /**
     * Returns the first value for a header name.
     *
     * @param key the name of the header
     * @return the first value, or an empty string if the header is not present
     */
    public String getFirst(String key) {
        return headers
                .stream()
                .filter(h -> isName(h, key))
                .findFirst()
                .map(Header::getValue)
                .orElse("");
    }

    /**
     * Returns all headers in this collection.
     *
     * @return a new list containing all headers in order
     */
    public List<Header> all() {
        return new ArrayList<>(this.headers);
    }

    /**
     * Adds a Cookie header.
     *
     * @param cookie the cookie to add
     */
    public void cookie(Cookie cookie) {
        headers.add(new Entry("cookie", cookie.toString()));
    }

    /**
     * Adds multiple Cookie headers.
     *
     * @param cookies the cookies to add
     */
    public void cookie(Collection<Cookie> cookies) {
        cookies.forEach(this::cookie);
    }

    private boolean isName(Header h, String name) {
        return Util.nullToEmpty(name).equalsIgnoreCase(h.getName());
    }

    void remove(String key, String value) {
        List<Header> header = headers.stream().
                filter(h -> key.equalsIgnoreCase(h.getName()) && value.equalsIgnoreCase(h.getValue()))
                .collect(toList());
        headers.removeAll(header);
    }

    /**
     * Returns a string representation of all headers.
     *
     * @return headers formatted as {@code Name: Value}, one per line
     */
    @Override
    public String toString() {
       final StringJoiner sb = new StringJoiner(System.lineSeparator());
        headers.forEach(header -> sb.add(header.toString()));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true;}
        if (o == null || getClass() != o.getClass()) { return false; }
        Headers headers1 = (Headers) o;
        return Objects.equals(headers, headers1.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    /**
     * Sets a Basic Authentication header.
     * <p>
     * Creates an Authorization header with the Base64 encoded value of
     * {@code username:password} prefixed with "Basic".
     * For example, given "username" and "password", produces:
     * {@code Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=}
     *
     * @param username the username
     * @param password the password
     */
    public void setBasicAuth(String username, String password) {
        this.replace("Authorization", Util.toBasicAuthValue(username, password));
    }

    /**
     * Sets the Accept header.
     *
     * @param value the accept header value (e.g., "application/json")
     */
    public void accepts(String value) {
        add(HeaderNames.ACCEPT, value);
    }

    /**
     * Adds headers from a map of key-value pairs.
     *
     * @param headerMap a map of header names to values
     */
    public void add(Map<String, String> headerMap) {
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                add(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Replaces headers from a map of key-value pairs.
     * <p>
     * Each header in the map will replace any existing header with the same name.
     *
     * @param headerMap a map of header names to values
     */
    public void replace(Map<String, String> headerMap) {
        if (headerMap != null) {
            headerMap.forEach(this::replace);
        }
    }

    /**
     * A header entry implementation that supports supplier-based values.
     */
    static class Entry implements Header {

        private final String name;
        private final Supplier<String> value;

        /**
         * Creates a header entry with a fixed value.
         *
         * @param name the header name
         * @param value the header value
         */
        public Entry(String name, String value) {
            this.name = name;
            this.value = () -> value;
        }

        /**
         * Creates a header entry with a supplier-based value.
         *
         * @param name the header name
         * @param value a supplier that provides the header value
         */
        public Entry(String name, Supplier<String> value) {
            this.name = name;
            this.value = value == null ? () -> null : value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            String s = value.get();
            if(s == null){
                return "";
            }
            return s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)  { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            Entry entry = (Entry) o;
            return Objects.equals(name, entry.name) &&
                    Objects.equals(value.get(), entry.value.get());
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value.get());
        }

        @Override
        public String toString() {
            return String.format("%s: %s",getName(), getValue());
        }
    }
}
