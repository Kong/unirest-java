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

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.*;
import java.util.stream.Stream;

public class Headers extends TreeMap<String, List<String>> {

    private static final long serialVersionUID = 71310341388734766L;

    public Headers() {
        super(String.CASE_INSENSITIVE_ORDER);
    }

    public Headers(Header[] pairs) {
        for (Header header : pairs) {
            add(header.getName(), header.getValue());
        }
    }

    public String getFirst(Object key) {
        return getOrDefault(key, Collections.emptyList())
                .stream()
                .findFirst()
                .orElse(null);
    }

    public void add(String name, String value) {
        if(Objects.nonNull(name)){
            computeIfAbsent(name, k -> new ArrayList<>()).add(Util.nullToEmpty(value));
        }
    }

    public Stream<Header> entries() {
        return entrySet().stream().flatMap(this::toEntries);
    }

    private Stream<Header> toEntries(Map.Entry<String, List<String>> k) {
        String key = k.getKey();
        return k.getValue().stream().map(e -> new BasicHeader(key, e));
    }

    public void addAll(Headers headers) {
        headers.entries().forEach(e -> add(e.getName(), e.getValue()));
    }
}
