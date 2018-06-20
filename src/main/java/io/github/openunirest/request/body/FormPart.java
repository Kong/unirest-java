package io.github.openunirest.request.body;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.*;

import java.io.File;

public final class FormPart implements Comparable {
    private final String name;
    private final Object value;
    private final ContentType contentType;

    public FormPart(String name, Object value, ContentType contentType) {
        this.name = name;
        this.value = value;
        this.contentType = contentType;
    }

    public Object getValue() {
        return value;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public ContentBody toApachePart() {
        if (value instanceof File) {
            File file = (File) value;
            return new FileBody(file, contentType, file.getName());
        } else if (value instanceof InputStreamBody) {
            return (ContentBody) value;
        } else if (value instanceof ByteArrayBody) {
            return (ContentBody) value;
        } else {
            return new StringBody(value.toString(), contentType);
        }
    }

    public String getName() {
        return name == null ? "" : name;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof FormPart){
            return getName().compareTo(((FormPart)o).getName());
        }
        return 0;
    }
}
