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
import kong.unirest.core.SseHandler;

import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Internal SSE Event Handler which reads the raw stream of event data
 * Event dispatching follows the spec outlined in the HTML5 standard
 * https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream
 */
class SseResponseHandler implements Consumer<HttpResponse<Stream<String>>> {
    private final Config config;
    private final SseHandler listener;
    private EventBuffer databuffer = new EventBuffer();

    public SseResponseHandler(Config config, SseHandler listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    public void accept(HttpResponse<Stream<String>> response) {
            response.body().forEach(this::accept);
    }

    private void accept(String line) {
        var pl = parse(line);

        if(pl.isDispatch()){
            listener.onEvent(databuffer.toEvent());
            databuffer = new EventBuffer();
        } else if (pl.isComment()) {
            listener.onComment(pl.value());
        } else if(pl.isData()) {
            databuffer.buffer.append(pl.value()).append("\n");
        } else if(pl.isEvent()){
            databuffer.type = pl.value();
        } else if (pl.isId()){
            databuffer.id = pl.value();
        } else if (pl.isRetry()){

        }
    }

    /**
     * Lines must be processed, in the order they are received, as follows:
     *
     * If the line is empty (a blank line)
     * Dispatch the event, as defined below.
     *
     * If the line starts with a U+003A COLON character (:)
     * Ignore the line.
     *
     * If the line contains a U+003A COLON character (:)
     * Collect the characters on the line before the first U+003A COLON character (:), and let field be that string.
     *
     * Collect the characters on the line after the first U+003A COLON character (:), and let value be that string. If value starts with a U+0020 SPACE character, remove it from value.
     *
     * Process the field using the steps described below, using field as the field name and value as the field value.
     *
     * Otherwise, the string is not empty but does not contain a U+003A COLON character (:)
     * Process the field using the steps described below, using the whole line as the field name, and the empty string as the field value.
     *
     * Once the end of the file is reached, any pending data must be discarded. (If the file ends in the middle of an event, before the final empty line, the incomplete event is not dispatched.)
     */
    private ParsedLine parse(String line) {
        if(line == null || line.isBlank()) {
            return new ParsedLine();
        } else if (line.startsWith(":")){
            return new ParsedLine(line.substring(1));
        } else if (!line.contains(":")) {
            return new ParsedLine(line, "");
        } else {
            var spl = line.split(":", 2);
            return new ParsedLine(spl[0].trim(), spl[1]);
        }
    }

    private class ParsedLine {

        private final String value;
        private final String field;

        public ParsedLine(String value) {
            this(null, value);
        }

        public ParsedLine(){
            this(null, null);
        }

        public ParsedLine(String field, String value) {
            this.field = field;
            if(value == null){
                this.value = null;
            } else if (value.startsWith(" ")) {
                this.value = value.substring(1);
            } else {
                this.value = value;
            }
        }

        public boolean isDispatch() {
            return field == null && value == null;
        }

        public boolean isComment() {
            return field == null;
        }

        public String value() {
            return value;
        }

        public String name() {
            return field;
        }

        public boolean isData() {
            return field != null && field.equalsIgnoreCase("data");
        }

        public boolean isEvent() {
            return field != null && field.equalsIgnoreCase("event");
        }

        public boolean isId() {
            return field != null
                    && field.equalsIgnoreCase("id")
                    && value != null;
        }

        public boolean isRetry() {
            return value == null && field != null
                    && field.matches("^\\d+$");

        }
    }


    private class EventBuffer {
        String id = "";
        String type = "";
        StringBuffer buffer = new StringBuffer();

        public Event toEvent() {
            return new Event(id, type, getValue(), config);
        }

        private String getValue() {
            String string = buffer.toString();
            if(string.endsWith("\n")){
                return string.substring(0, string.length() -1);
            }
            return string;
        }
    }
}
