/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

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

    public Headers(){}

    public Headers(Collection<Entry> entries){
        entries.forEach(e -> add(e.name, e.value));
    }

    public void add(String name, String value) {
        add(name, () -> value);
    }

    public void add(String name, Supplier<String> value) {
        if(Objects.nonNull(name)) {
            headers.add(new Entry(name, value));
        }
    }

    public void replace(String name, String value){
        remove(name);
        add(name, value);
    }

    private void remove(String name) {
        headers.removeIf(h -> isName(h, name));
    }

    public int size() {
        return headers.stream().map(Header::getName).collect(toSet()).size();
    }

    public List<String> get(String name) {
        return headers.stream()
                .filter(h -> isName(h, name))
                .map(Header::getValue)
                .collect(toList());
    }

    private boolean isName(Header h, String name) {
       return Util.nullToEmpty(name).equalsIgnoreCase(h.getName());
    }

    public void putAll(Headers defaultHeaders) {
        this.headers.addAll(defaultHeaders.headers);
    }

    public boolean containsKey(String key) {
        return this.headers.stream().anyMatch(h -> isName(h, key));
    }

    public void clear() {
        this.headers.clear();
    }

    public String getFirst(String key) {
        return headers
                .stream()
                .filter(h -> isName(h, key))
                .findFirst()
                .map(Header::getValue)
                .orElse("");
    }

    public List<Header> all() {
        return this.headers;
    }

    static class Entry implements Header {
        private final String name;
        private final Supplier<String> value;

        public Entry(String name, String value){
            this.name = name;
            this.value = () -> value;
        }

        public Entry(String name, Supplier<String> value){
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue(){
            return value.get();
        }
    }
}
