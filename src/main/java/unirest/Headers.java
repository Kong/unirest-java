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

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Headers {

    private static final long serialVersionUID = 71310341388734766L;
    private TreeMap<String, List<Entry>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public Headers(){}

    public Headers(Collection<Entry> entries){
        entries.forEach(e -> add(e.name, e.value));
    }

    public void add(String name, String value) {
        add(name, () -> value);
    }

    public void add(String name, Supplier<String> value) {
        if(Objects.nonNull(name)) {
            headers.computeIfAbsent(name, k -> new ArrayList<>()).add(new Entry(name, value));
        }
    }

    public void replace(String name, String value){
        if(headers.containsKey(name)) {
            List<Entry> repl = new ArrayList<>();
            repl.add(new Entry(name, () -> value));
            headers.replace(name.trim(), repl);
        } else {
            add(name, value);
        }
    }

    public int size() {
        return headers.size();
    }

    public List<String> get(String name) {
        return headers.get(name)
                .stream()
                .map(Entry::getValue)
                .collect(toList());
    }

    public void putAll(Headers defaultHeaders) {
        this.headers.putAll(defaultHeaders.headers);
    }

    public boolean containsKey(String key) {
        return this.headers.containsKey(key);
    }

    public void clear() {
        this.headers.clear();
    }

    public String getFirst(String key) {
        return headers.getOrDefault(key, Collections.emptyList())
                .stream()
                .map(e -> e.value)
                .map(Supplier::get)
                .findFirst()
                .orElse("");
    }

    Stream<Entry> stream() {
        return this.headers.values().stream().flatMap(Collection::stream);
    }

    static class Entry {
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

        public String getName() {
            return name;
        }

        public String getValue(){
            return value.get();
        }
    }
}
