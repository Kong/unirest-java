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

package kong.unirest;

import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Headers {

    private static final long serialVersionUID = 71310341388734766L;
    private List<Header> headers = new ArrayList<>();

    public Headers() {
    }

    public Headers(Collection<Entry> entries) {
        entries.forEach(e -> add(e.name, e.value));
    }

    /**
     * Add a header element
     * @param name the name of the header
     * @param value the value for the header
     */
    public void add(String name, String value) {
        add(name, () -> value);
    }

    /**
     * Add a header element with a supplier which will be evaluated on request
     * @param name the name of the header
     * @param value the value for the header
     */
    public void add(String name, Supplier<String> value) {
        if (Objects.nonNull(name)) {
            headers.add(new Entry(name, value));
        }
    }

    /**
     * Replace a header value. If there are multiple instances it will overwrite all of them
     * @param name the name of the header
     * @param value the value for the header
     */
    public void replace(String name, String value) {
        remove(name);
        add(name, value);
    }

    /**
     * Remove a header
     * @param name the header name to remove
     */
    public void remove(String name) {
        headers.removeIf(h -> isName(h, name));
    }

    /**
     * Get the number of header keys.
     * @return the size of the header keys
     */
    public int size() {
        return headers.stream().map(Header::getName).collect(toSet()).size();
    }

    /**
     * Get all the values for a header name
     * @param name name of the header element
     * @return a list of values
     */
    public List<String> get(String name) {
        return headers.stream()
                .filter(h -> isName(h, name))
                .map(Header::getValue)
                .collect(toList());
    }

    /**
     * Add a bunch of headers at once
     * @param header a header
     */
    public void putAll(Headers header) {
        this.headers.addAll(header.headers);
    }

    /**
     * Check if a header is present
     * @param name a header
     * @return if the headers contain this name.
     */
    public boolean containsKey(String name) {
        return this.headers.stream().anyMatch(h -> isName(h, name));
    }

    /**
     * Clear the headers!
     */
    public void clear() {
        this.headers.clear();
    }

    /**
     * Get the first header value for a name
     * @param key the name of the header
     * @return the first value
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
     * Get all of the headers
     * @return all the headers, in order
     */
    public List<Header> all() {
        return new ArrayList<>(this.headers);
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
     * @return list all headers like this: <pre>Content-Length: 42
     * Cache-Control: no-cache
     * ...</pre>
     */
    @Override
    public String toString() {
       final StringJoiner sb = new StringJoiner(System.lineSeparator());
        headers.forEach(header -> sb.add(header.toString()));
        return sb.toString();
    }

    public void cookie(Cookie cookie) {
        headers.add(new Entry("cookie", cookie.toString()));
    }

    public void cookie(Collection<Cookie> cookies) {
        cookies.forEach(this::cookie);
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

    public void setBasicAuth(String username, String password) {
        this.replace("Authorization", Util.toBasicAuthValue(username, password));
    }

    public void accepts(String value) {
        add(HeaderNames.ACCEPT, value);
    }

    public void add(Map<String, String> headerMap) {
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                add(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Replace all headers from a given map.
     * @param headerMap the map of headers
     */
    public void replace(Map<String, String> headerMap) {
        if (headerMap != null) {
            headerMap.forEach(this::replace);
        }
    }

    static class Entry implements Header {

        private final String name;
        private final Supplier<String> value;

        public Entry(String name, String value) {
            this.name = name;
            this.value = () -> value;
        }

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
            return value.get();
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
