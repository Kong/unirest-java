/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

import static unirest.ResponseUtils.getCharSet;
import static unirest.ResponseUtils.getRawBody;

@Deprecated // use at your own risk
public class BodyData<T>  {

    public static <T> BodyData<T> from(HttpEntity e, Function<BodyData<T>, T> transformer) {
        if(Objects.isNull(e)){
            return new BodyData<>();
        }
        BodyData d = new BodyData(e);
        d.transformBody(transformer);
        return d;
    }

    private final String charset;
    private final byte[] rawBytes;
    private T transFormedBody;
    private RuntimeException parseEx;

    private BodyData() {
        charset = null;
        rawBytes = null;
    }

    private BodyData(HttpEntity entity) {
        this.charset = getCharSet(entity);
        this.rawBytes = getRawBody(entity);
    }

    public String getCharset() {
        return charset;
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    public InputStream getRawInput() {
        if(rawBytes == null){
            return new ByteArrayInputStream(new byte[]{});
        }
        return new ByteArrayInputStream(rawBytes);
    }

    public T getTransFormedBody() {
        return transFormedBody;
    }

    public RuntimeException getParseEx() {
        return parseEx;
    }

    private void transformBody(Function<BodyData<T>, T> transformer) {
        try {
            this.transFormedBody = transformer.apply(this);
        }catch (UnirestException e){
            throw e;
        }catch (RuntimeException e){
            parseEx = e;
        }
    }
}
