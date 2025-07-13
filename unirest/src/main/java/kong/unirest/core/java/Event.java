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

package kong.unirest.core.java;

import java.util.Objects;

public class Event {
    private final String id;
    private final Object data;
    private final String field;

    public Event(String id, String field, Object data){
        this.id = id;
        this.field = field;
        this.data = data;
    }

    public Object data() {
        return data;
    }

    public String id() {
        return id;
    }

    public String name() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id)
                && Objects.equals(data, event.data)
                && Objects.equals(field, event.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, field);
    }

    @Override
    public String toString() {
        return "SseEvent{" +
                "data=" + data +
                ", field=" + field +
                ", id=" + id  +
                '}';
    }
}
