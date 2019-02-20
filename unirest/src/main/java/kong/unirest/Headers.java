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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    private void remove(String name) {
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
        return this.headers;
    }

    private boolean isName(Header h, String name) {
        return Util.nullToEmpty(name).equalsIgnoreCase(h.getName());
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
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return value.get();
        }
    }
}
