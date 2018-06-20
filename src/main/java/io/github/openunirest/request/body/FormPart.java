package io.github.openunirest.request.body;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.*;

import java.io.File;

public final class FormPart {
    private final Object o;
    private final ContentType contentType;

    public FormPart(Object o, ContentType contentType) {
        this.o = o;
        this.contentType = contentType;
    }

    public Object getValue() {
        return o;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public ContentBody toApachePart() {
        if (o instanceof File) {
            File file = (File) o;
            return new FileBody(file, contentType, file.getName());
        } else if (o instanceof InputStreamBody) {
            return (ContentBody) o;
        } else if (o instanceof ByteArrayBody) {
            return (ContentBody) o;
        } else {
            return new StringBody(o.toString(), contentType);
        }
    }
}
