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

package kong.unirest;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.*;

import java.io.File;
import java.nio.charset.StandardCharsets;

class FormPart implements Comparable {
    private final String name;
    private final Object value;
    private final ContentType contentType;

    FormPart(String name, Object value, ContentType contentType) {
        this.name = name;
        this.value = value;
        this.contentType = contentType;
    }

    public Object getValue() {
        return value;
    }

    private ContentType getContentType() {
        if(contentType == null){
            if(isFile()){
                return ContentType.APPLICATION_OCTET_STREAM;
            }
            return ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8);
        }
        return contentType;
    }

    ContentBody toApachePart() {
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
