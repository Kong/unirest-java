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

import kong.unirest.core.SseListener;

import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.stream.Stream;

class SseResponseHandler implements Consumer<HttpResponse<Stream<String>>> {
    private final SseListener listener;
    private EventBuffer databuffer = new EventBuffer();

    public SseResponseHandler(SseListener listener) {
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

    private ParsedLine parse(String line) {
        if(line == null || line.isBlank()) {
            return new ParsedLine();
        } else if (line.trim().startsWith(":")){
            return new ParsedLine(line.substring(1));
        } else if (!line.contains(":")) {
            return new ParsedLine(line, "");
        } else {
            var spl = line.split(":");
            return new ParsedLine(spl[0].trim(), spl[1]);
        }
    }

    private class ParsedLine {

        private final String value;
        private final String name;

        public ParsedLine(String value) {
            this(null, value);
        }

        public ParsedLine(){
            this(null, null);
        }

        public ParsedLine(String name, String value) {
            this.name = name;
            if(value == null){
                this.value = null;
            } else if (value.startsWith(" ")) {
                this.value = value.substring(1);
            } else {
                this.value = value;
            }
        }

        public boolean isDispatch() {
            return name == null && value == null;
        }

        public boolean isComment() {
            return name == null;
        }

        public String value() {
            return value;
        }

        public String name() {
            return name;
        }

        public boolean isData() {
            return name != null && name.equalsIgnoreCase("data");
        }

        public boolean isEvent() {
            return name != null && name.equalsIgnoreCase("event");
        }

        public boolean isId() {
            return name != null
                    && name.equalsIgnoreCase("id")
                    && value != null;
        }

        public boolean isRetry() {
            return value == null && name != null
                    && name.matches("^\\d+$");

        }
    }


    private class EventBuffer {
        String id = "";
        String type = "";
        StringBuffer buffer = new StringBuffer();

        public SseEvent toEvent() {
            return new SseEvent(id, type, getValue());
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
