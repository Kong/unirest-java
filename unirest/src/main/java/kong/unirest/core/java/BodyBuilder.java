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

import kong.unirest.core.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.stream.Collectors;

class BodyBuilder {
    public static final Charset ASCII = Charset.forName("US-ASCII");
    private final kong.unirest.core.HttpRequest request;

    BodyBuilder(kong.unirest.core.HttpRequest request) {
        this.request = request;
    }

    HttpRequest.BodyPublisher getBody() {
        Optional<Body> body = request.getBody();
        return body
                .map(o -> toPublisher(o))
                .orElseGet(HttpRequest.BodyPublishers::noBody);
    }

    private HttpRequest.BodyPublisher toPublisher(Body o) {
        if (o.isEntityBody()) {
            return mapToUniBody(o);
        } else {
            return mapToMultipart(o);
        }
    }

    private java.net.http.HttpRequest.BodyPublisher mapToMultipart(Body o) {
        try {
            if (o.multiParts().isEmpty()) {
                setContentAsFormEncoding(o);
                return HttpRequest.BodyPublishers.noBody();
            }
            if (!o.isMultiPart()) {
                setContentAsFormEncoding(o);
                return HttpRequest.BodyPublishers.ofString(
                        toFormParams(o)
                );
            }

            var builder = MultipartBodyPublisher.newBuilder(o.getBoundary());
            o.multiParts().forEach(part -> {
                setMultiPart(o, builder, part);
            });

            MultipartBodyPublisher build = builder.build(o.getMonitor());
            request.header("Content-Type", "multipart/form-data; boundary=" + build.boundary() + ";charset=" + o.getCharset());
            return build;
        } catch (Exception e) {
            throw new UnirestException(e);
        }
    }

    private void setContentAsFormEncoding(Body o) {
        String content = "application/x-www-form-urlencoded";
        if(o.getCharset() != null){
            content = content + "; charset="+o.getCharset().toString();
        }
        if(!alreadyHasMultiPartHeader()){
            request.header(HeaderNames.CONTENT_TYPE, content);
        }
    }

    private boolean alreadyHasMultiPartHeader() {
        return request.getHeaders()
                .containsKey(HeaderNames.CONTENT_TYPE);
    }

    private String toFormParams(Body o) {
       return o.multiParts()
                .stream()
                .filter(p -> p instanceof ParamPart)
                .map(p -> (ParamPart) p)
                .map(p -> toPair(p, o))
                .collect(Collectors.joining("&"));
    }

    private String toPair(ParamPart p, Body o) {
        try {
            String encoding = o.getCharset() == null ? "UTF-8" : o.getCharset().toString();
            return String.format("%s=%s", p.getName(), URLEncoder.encode(p.getValue(), encoding));
        } catch (UnsupportedEncodingException e) {
            throw new UnirestException(e);
        }
    }


    private void setMultiPart(Body o, MultipartBodyPublisher.Builder builder, BodyPart part) {
        String contentType = part.getContentType();
        if (part.isFile()) {
            if (part instanceof FilePart) {
                try {
                    builder.filePart(part.getName(),
                            ((File) part.getValue()).toPath(),
                            contentType);
                } catch (FileNotFoundException e) {
                    throw new UnirestException(e);
                }
            } else if (part instanceof InputStreamPart) {
                if (part.getFileName() != null) {
                    builder.formPart(part.getName(), standardizeName(part, o.getMode()),
                            new PartPublisher(HttpRequest.BodyPublishers.ofInputStream(() -> (InputStream) part.getValue()), contentType), contentType);
                } else {
                    builder.formPart(part.getName(),
                            new PartPublisher(HttpRequest.BodyPublishers.ofInputStream(() -> (InputStream) part.getValue()), contentType), contentType);
                }

            } else if (part instanceof ByteArrayPart) {
                builder.formPart(part.getName(),
                        standardizeName(part, o.getMode()),
                        new PartPublisher(HttpRequest.BodyPublishers.ofByteArray((byte[]) part.getValue()), contentType), contentType);
            }
        } else {
            builder.textPart(part.getName(), String.valueOf(part.getValue()), contentType);
        }
    }

    private String standardizeName(BodyPart part, MultipartMode mode) {
        if (mode.equals(MultipartMode.STRICT)) {
            return part.getFileName().chars()
                    .mapToObj(c -> {
                        if (!ASCII.newEncoder().canEncode((char) c)) {
                            return '?';
                        }
                        return Character.valueOf((char) c);
                    }).map(c -> c.toString())
                    .collect(Collectors.joining());
        }
        return part.getFileName();
    }

    private HttpRequest.BodyPublisher mapToUniBody(Body b) {
        BodyPart bodyPart = b.uniPart();
        if (bodyPart == null) {
            return HttpRequest.BodyPublishers.noBody();
        } else if (String.class.isAssignableFrom(bodyPart.getPartType())) {
            return createStringBody(b, bodyPart);
        } else if (InputStream.class.isAssignableFrom(bodyPart.getPartType())) {
            return createInputStreamBody(b, bodyPart);
        }else {
            return HttpRequest.BodyPublishers.ofByteArray((byte[]) bodyPart.getValue());
        }
    }

    private HttpRequest.BodyPublisher createInputStreamBody(Body b, BodyPart bodyPart) {
        if(b.getMonitor() != null){
            return HttpRequest.BodyPublishers.ofInputStream(
                    () -> new MonitoringInputStream((InputStream) bodyPart.getValue(), b.getMonitor()));
        }
        return HttpRequest.BodyPublishers.ofInputStream(
                () -> (InputStream) bodyPart.getValue());
    }

    private HttpRequest.BodyPublisher createStringBody(Body b, BodyPart bodyPart) {
        Charset charset = b.getCharset();
        if (charset == null) {
            return HttpRequest.BodyPublishers.ofString((String) bodyPart.getValue());
        }
        return HttpRequest.BodyPublishers.ofString((String) bodyPart.getValue(), charset);
    }
}
