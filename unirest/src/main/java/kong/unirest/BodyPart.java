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

import java.nio.charset.StandardCharsets;

public abstract class BodyPart<T> implements Comparable { ;
    private final String name;
    private final T value;
    private final String contentType;
    private final Class<?> partType;

    protected BodyPart(T value, String name, String contentType) {
        this.name = name;
        this.value = value;
        this.contentType = contentType;
        this.partType = value.getClass();
    }

    public T getValue() {
        return value;
    }

    public Class<?> getPartType(){
        return partType;
    }

    public String getContentType() {
        if(contentType == null){
            if(isFile()){
                return ContentType.APPLICATION_OCTET_STREAM.toString();
            }
            return ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8).toString();
        }
        return contentType;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public String getFileName(){
        return null;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof BodyPart){
            return getName().compareTo(((BodyPart)o).getName());
        }
        return 0;
    }


    abstract public boolean isFile();
}
