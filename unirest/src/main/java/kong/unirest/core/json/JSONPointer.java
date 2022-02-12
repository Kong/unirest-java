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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * A Json Pointer query object following
 * https://tools.ietf.org/html/rfc6901
 *
 */
public class JSONPointer {

    private final String section;
    private final JSONPointer next;

    private JSONPointer(){
        section = null;
        next = null;
    }

    /**
     * a JSONPointer constructor
     * @param query the pointer query
     */
    public JSONPointer(String query){
        JSONPointer compiled = compile(query);
        this.section = compiled.section;
        this.next = compiled.next;
    }

    private JSONPointer(String section, JSONPointer nextNode) {
        this.section = section;
        this.next = nextNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("/");
        sb.append(unescape(section));
        if(next != null){
            sb.append(next.toString());
        }
        return sb.toString();
    }

    /**
     * @return the pointer as a URL encoded URI fragment
     */
    public String toURIFragment() {
        return "#" + toUriChunk();
    }

    private String toUriChunk() {
        try {
            StringBuilder sb = new StringBuilder("/");
            sb.append(URLEncoder.encode(section, "UTF-8"));
            if(next != null){
                sb.append(next.toUriChunk());
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new JSONPointerException("UTF-8 encoder not found. Is that even possible?");
        }
    }

    public static JSONPointer compile(String query) {
        Objects.requireNonNull(query, "pointer cannot be null");
        if (!query.equals("") && !query.startsWith("/") && !query.startsWith("#/")) {
            throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'");
        }
        return createPointer(query);
    }

    private static JSONPointer createPointer(String query) {
        if (query.equals("")) {
            return new JSONPointer();
        }
        return compileNext(query);
    }

    /**
     * Many of the path compiling code was borrowed from Jackson.
     * It is, slightly modified but similar enough to give credit.
     * please see com.fasterxml.jackson.core.JsonPointer
     * @author Tatu Saloranta
     */
    private static JSONPointer compileNext(String query) {
        final int end = query.length();
        for (int i = 1; i < end; ) {
            char c = query.charAt(i);
            if (c == '/') {
                return new JSONPointer(query.substring(1, i),
                        compileNext(query.substring(i)));
            }
            ++i;
            if (c == '~' && i < end) {
                return compileNextEscaped(query, i);
            }
        }
        return new JSONPointer(query.substring(1), null);
    }

    private static JSONPointer compileNextEscaped(String query, int i) {
        final int end = query.length();
        StringBuilder sb = new StringBuilder(Math.max(16, end));
        if (i > 2) {
            sb.append(query, 1, i - 1);
        }
        escape(sb, query.charAt(i++));
        while (i < end) {
            char c = query.charAt(i);
            if (c == '/') {
                return new JSONPointer(sb.toString(),
                        compileNext(query.substring(i)));
            }
            ++i;
            if (c == '~' && i < end) {
                escape(sb, query.charAt(i++));
                continue;
            }
            sb.append(c);
        }
        return new JSONPointer(sb.toString(), null);
    }

    private static String unescape(String s){
        String finalToken = s;
        if(s.contains("~")){
            finalToken  = finalToken.replaceAll("~","~0");
        }
        if(s.contains("/")){
            finalToken = finalToken.replaceAll("/","~1");
        }
        return finalToken;
    }

    private static void escape(StringBuilder sb, char c) {
        if (c == '0') {
            c = '~';
        } else if (c == '1') {
            c = '/';
        } else {
            sb.append('~');
        }
        sb.append(c);
    }

    public Object queryFrom(Object object) throws JSONPointerException {
        if(section == null){
            return object;
        }
        Queryable e = verify(object);
        Object o = e.querySection(section);
        if (next != null) {
            if(o == null){
                throw new JSONPointerException("Path Segment Missing: " + section);
            }
            return next.queryFrom(o);
        }
        return o;
    }

    private interface Queryable<T> {

        Object querySection(String section);
    }
    private Queryable verify(Object object) {
        if (JSONObject.class.isInstance(object)) {
            return new QueryObject((JSONObject) object);
        } else if (JSONArray.class.isInstance(object)) {
            return new QueryArray((JSONArray) object);
        }
        throw new IllegalArgumentException("May only query JSONObject or JSONArray");
    }

    private class QueryObject implements Queryable {

        private final JSONObject obj;
        public QueryObject(JSONObject object) {
            this.obj = object;
        }

        @Override
        public Object querySection(String key) {
            if (obj.has(key)) {
                return obj.get(key);
            }
            return null;
        }

    }
    private class QueryArray implements Queryable {

        private final JSONArray array;
        public QueryArray(JSONArray array) {
            this.array = array;
        }

        @Override
        public Object querySection(String key) {
            Integer index = getIndex(key);
            if (index > array.length()) {
                throw new JSONPointerException("index %s is out of bounds - the array has %s elements", index, array.length());
            }
            return array.get(index);
        }

        private Integer getIndex(String index) {
            try {
                return Integer.valueOf(index);
            } catch (NumberFormatException e) {
                throw new JSONPointerException("%s is not an array index", index);
            }
        }

    }

    public static JSONPointer.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private StringBuilder sb = new StringBuilder();
        private Builder(){
           // sb.append("/");
        }

        public Builder append(String token) {
            sb.append("/").append(token);
            return this;
        }

        public Builder append(int index) {
            return append(String.valueOf(index));
        }

        public JSONPointer build() {
            return new JSONPointer(sb.toString());
        }
    }
}
