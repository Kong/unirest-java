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

import kong.unirest.core.Config;
import kong.unirest.core.GenericType;

import java.util.Objects;

/**
 * A server sent event.
 * Generally modeled on the HTML5 EventSource standard
 * https://html.spec.whatwg.org/multipage/
 */
public class Event {
    private final Config config;
    private final String id;
    private final String data;
    private final String event;

    /**
     * Constructor used by unirest which includes the Unirest config which uis used for event serialization
     * @param id The event ID
     * @param event the event label. "message" is the default from the server
     * @param data the data in the event. This is concatenated from all lines before a dispatch event (a blank line)
     * @param config the unirest config used for deserialization
     */
    public Event(String id, String event, String data, Config config) {
        this.config = config;
        this.id = id;
        this.event = event;
        this.data = data;
    }

    /**
     * The data field for the message.
     * When the EventSource receives multiple consecutive lines that begin with data:, it concatenates them, inserting a newline character between each one.
     * Trailing newlines are removed.
     * @return the data
     */
    public String data() {
        return data;
    }

    /**
     * The event ID to set the EventSource object's last event ID value.
     * @return the id
     */
    public String id() {
        return id;
    }

    /**
     * A string identifying the type of event described.
     * If this is specified, an event will be dispatched on the browser to the listener for the specified event name;
     * @return the event name
     */
    public String event() {
        return event;
    }

    /**
     * Deserialize the data of the event using the Unirest Config's ObjectMapper
     * @param responseClass the type of class you want back
     * @return an instance of the class
     * @param <T> the class type
     */
    public <T> T asObject(Class<? extends T> responseClass) {
        return config.getObjectMapper().readValue(data, responseClass);
    }

    /**
     * Deserialize the data of the event using the Unirest Config's ObjectMapper
     * This method takes a GenericType which retains Generics information for types lke List&lt;Foo&gt;
     * @param genericType the type of class you want back using a generictype object
     * @return an instance of the class
     * @param <T> the class type
     */
    public <T> T asObject(GenericType<T> genericType){
        return config.getObjectMapper().readValue(data, genericType);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id)
                && Objects.equals(data, event.data)
                && Objects.equals(this.event, event.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, event);
    }

    @Override
    public String toString() {
        return "SseEvent{" +
                "data=" + data +
                ", field=" + event +
                ", id=" + id  +
                '}';
    }
}
