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


import kong.unirest.core.Headers;

import java.net.http.HttpRequest;

import static java.util.Objects.requireNonNull;

/**
 * Represents a part in a multipart request body.
 */
class Part {

    private final Headers headers;
    private final HttpRequest.BodyPublisher bodyPublisher;
    private final String fieldName;
    private final String filename;

    Part(String fieldName, String filename, HttpRequest.BodyPublisher bodyPublisher, String contentType) {
        requireNonNull(bodyPublisher, "bodyPublisher");
        this.fieldName = fieldName;
        this.filename = filename;
        this.headers = getFormHeaders(fieldName, filename, contentType);
        this.bodyPublisher = bodyPublisher;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFilename() {
        return filename;
    }

    public Headers headers() {
        return headers;
    }

    public HttpRequest.BodyPublisher bodyPublisher() {
        return bodyPublisher;
    }



    private static Headers getFormHeaders(String name, String filename, String contentType) {
        StringBuilder disposition = new StringBuilder();
        appendEscaped(disposition.append("form-data; name="), name);
        if (filename != null) {
            appendEscaped(disposition.append("; filename="), filename);
        }
        Headers headers = new Headers();
        headers.add("Content-Disposition", disposition.toString());
        headers.add("Content-Type", contentType);
        return headers;
    }

    private static void appendEscaped(StringBuilder target, String field) {
        target.append("\"");
        for (int i = 0, len = field.length(); i < len; i++) {
            char c = field.charAt(i);
            if (c == '\\' || c == '\"') {
                target.append('\\');
            }
            target.append(c);
        }
        target.append("\"");
    }
}
