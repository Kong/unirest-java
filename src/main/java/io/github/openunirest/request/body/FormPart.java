package io.github.openunirest.request.body;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.*;

import java.io.File;
import java.nio.charset.StandardCharsets;

public final class FormPart implements Comparable {
    private final String name;
    private final Object value;
    private final ContentType contentType;

    public FormPart(String name, Object value){
        this(name, value, null);
    }

    public FormPart(String name, Object value, ContentType contentType) {
        this.name = name;
        this.value = value;
        this.contentType = contentType;
    }

    public Object getValue() {
        return value;
    }

    public ContentType getContentType() {
        if(contentType == null){
            if(isFile()){
                return ContentType.APPLICATION_OCTET_STREAM;
            }
            return ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8);
        }
        return contentType;
    }

    public ContentBody toApachePart() {
        if (value instanceof File) {
            File file = (File) value;
            return new FileBody(file, getContentType(), file.getName());
        } else if (value instanceof InputStreamBody) {
            return (ContentBody) value;
        } else if (value instanceof ByteArrayBody) {
            return (ContentBody) value;
        } else {
            return new StringBody(value.toString(), getContentType());
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

    public boolean isFile(){
        return     value instanceof File
                || value instanceof InputStreamBody
                || value instanceof ByteArrayBody;
    }
}
