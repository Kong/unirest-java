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


import kong.unirest.core.ProgressMonitor;
import kong.unirest.core.UnirestException;

import java.io.FileNotFoundException;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

/**
 * A {@code BodyPublisher} implementing the multipart request type.
 *
 * @see <a href="https://tools.ietf.org/html/rfc2046#section-5.1">RFC 2046 Multipart Media Type</a>
 */
final class MultipartBodyPublisher implements BodyPublisher {

    private static final long UNKNOWN_LENGTH = -1;
    private static final long UNINITIALIZED_LENGTH = -2;

    private final List<Part> parts;
    private final ProgressMonitor monitor;
    private final String boundary;
    private long contentLength;

    private MultipartBodyPublisher(List<Part> parts, ProgressMonitor monitor, String boundary) {
        this.parts = parts;
        this.monitor = monitor;
        this.boundary = boundary;
        contentLength = UNINITIALIZED_LENGTH;
    }

    String boundary() {
        return boundary;
    }

    List<Part> parts() {
        return parts;
    }

    @Override
    public long contentLength() {
        long len = contentLength;
        if (len == UNINITIALIZED_LENGTH) {
            len = computeLength();
            contentLength = len;
        }
        return len;
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
        requireNonNull(subscriber);
        new MultipartSubscription(this.boundary, this.parts, monitor, subscriber).signal(true);
    }

    private long computeLength() {
        long lengthOfParts = 0L;
        String boundary = boundary();
        StringBuilder headings = new StringBuilder();
        for (int i = 0, sz = parts.size(); i < sz; i++) {
            Part part = parts.get(i);
            long partLength = part.bodyPublisher().contentLength();
            if (partLength < 0) {
                return UNKNOWN_LENGTH;
            }
            lengthOfParts += partLength;
            // Append preceding boundary + part header
            BoundaryAppender.get(i, sz).append(headings, boundary);
            appendPartHeaders(headings, part);
            headings.append("\r\n");
        }
        BoundaryAppender.LAST.append(headings, boundary);
        // Use headings' utf8-encoded length
        return lengthOfParts + UTF_8.encode(CharBuffer.wrap(headings)).remaining();
    }

    static void appendPartHeaders(StringBuilder target, Part part) {
        part.headers().all().forEach(h -> appendHeader(target, h.getName(), h.getValue()));
    }

    private static void appendHeader(StringBuilder target, String name, String value) {
        target.append(name).append(": ").append(value).append("\r\n");
    }

    /** Returns a new {@code MultipartBodyPublisher.Builder}. */
    static MultipartBodyPublisher.Builder newBuilder(String boundary) {
        return new MultipartBodyPublisher.Builder(boundary);
    }

    static final class Builder {

        private final List<Part> parts;
        private final String boundary;

        Builder(String boundary) {
            this.boundary = boundary;
            parts = new ArrayList<>();
        }

        /**
         * Adds a form field with the given name and body.
         *
         * @param name          the field's name
         * @param bodyPublisher the field's body publisher
         * @param contentType   the content type for the part
         */
        MultipartBodyPublisher.Builder formPart(String name, BodyPublisher bodyPublisher, String contentType) {
            requireNonNull(name, "name");
            requireNonNull(bodyPublisher, "body");
            parts.add(new Part(name, null, bodyPublisher, contentType));
            return this;
        }

        /**
         * Adds a form field with the given name, filename and body.
         *
         * @param name the field's name
         * @param filename the field's filename
         * @param body the field's body publisher
         */
        MultipartBodyPublisher.Builder formPart(String name, String filename, BodyPublisher body,  String contentType) {
            requireNonNull(name, "name");
            requireNonNull(filename, "filename");
            requireNonNull(body, "body");
            parts.add(new Part(name, filename, body, contentType));
            return this;
        }

        /**
         * Adds a {@code text/plain} form field with the given name and value. {@code UTF-8} is used for
         * encoding the field's body.
         *
         * @param name the field's name
         * @param value an object whose string representation is used as the value
         */
        MultipartBodyPublisher.Builder textPart(String name, Object value, String contentType) {
            return textPart(name, value, UTF_8, contentType);
        }

        /**
         * Adds a {@code text/plain} form field with the given name and value using the given charset
         * for encoding the field's body.
         *
         * @param name the field's name
         * @param value an object whose string representation is used as the value
         * @param charset the charset for encoding the field's body
         */
        MultipartBodyPublisher.Builder textPart(String name, Object value, Charset charset, String contentType) {
            requireNonNull(name, "name");
            requireNonNull(value, "value");
            requireNonNull(charset, "charset");
            return formPart(name, BodyPublishers.ofString(value.toString(), charset), contentType);
        }



        /**
         * Adds a file form field with given name, file and media type. The field's filename property
         * will be that of the given path's {@link Path#getFileName() filename compontent}.
         *
         * @param name the field's name
         * @param file the file's path
         * @param mediaType the part's media type
         * @throws FileNotFoundException if a file with the given path cannot be found
         */
        MultipartBodyPublisher.Builder filePart(String name, Path file, String mediaType)
                throws FileNotFoundException {
            requireNonNull(name, "name");
            requireNonNull(file, "file");
            requireNonNull(mediaType, "mediaType");
            Path filenameComponent = file.getFileName();
            String filename = filenameComponent != null ? filenameComponent.toString() : "";
            PartPublisher publisher =
                    new PartPublisher(BodyPublishers.ofFile(file), mediaType);
            return formPart(name, filename, publisher, mediaType);
        }

        /**
         * Creates and returns a new {@code MultipartBodyPublisher} with a snapshot of the added parts.
         * If no boundary was previously set, a randomly generated one is used.
         *
         * @throws IllegalStateException if no part was added
         */
        MultipartBodyPublisher build(ProgressMonitor monitor) {
            List<Part> addedParts = List.copyOf(parts);
            if (!!addedParts.isEmpty()) {
                throw new UnirestException("at least one part should be added");
            }
            return new MultipartBodyPublisher(addedParts, monitor, boundary);
        }
    }
}