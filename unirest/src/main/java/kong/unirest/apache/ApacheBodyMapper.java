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

package kong.unirest.apache;

import kong.unirest.*;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.*;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

class ApacheBodyMapper {

    private final HttpRequest request;

    ApacheBodyMapper(HttpRequest request){
        this.request = request;
    }

    HttpEntity apply() {
        Optional<Body> body = request.getBody();
        return body.map(this::applyBody).orElseGet(BasicHttpEntity::new);

    }

    private HttpEntity applyBody(Body o) {
        if(o.isEntityBody()){
            return mapToUniBody(o);
        }else {
            return mapToMultipart(o);
        }
    }
    
    private HttpEntity mapToUniBody(Body b) {
        BodyPart bodyPart = b.uniPart();
        if(bodyPart == null){
            return new StringEntity("", StandardCharsets.UTF_8);
        } else if(String.class.isAssignableFrom(bodyPart.getPartType())){
            return new StringEntity((String) bodyPart.getValue(), b.getCharset());
        } else {
            return new ByteArrayEntity((byte[])bodyPart.getValue());
        }
    }

    private HttpEntity mapToMultipart(Body body) {
        if (body.isMultiPart()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(body.getCharset());
            builder.setMode(HttpMultipartMode.valueOf(body.getMode().name()));
            for (BodyPart key : body.multiParts()) {
                builder.addPart(key.getName(), apply(key, body));
            }
            return builder.build();
        } else {
            return new UrlEncodedFormEntity(getList(body.multiParts()), body.getCharset());
        }
    }

    private ContentBody apply(BodyPart value, Body body) {
        if (is(value, File.class)) {
            return toFileBody(value, body);
        } else if (is(value, InputStream.class)) {
            return toInputStreamBody(value, body);
        } else if (is(value, byte[].class)) {
            return toByteArrayBody(value);
        } else {
            return toStringBody(value);
        }
    }

    private ContentBody toFileBody(BodyPart value, Body body) {
        File file = (File)value.getValue();
        return new MonitoringFileBody(value.getName(), file, toApacheType(value.getContentType()), body.getMonitor());
    }

    private ContentBody toInputStreamBody(BodyPart value, Body body) {
        InputStream part = (InputStream)value.getValue();
        return new MonitoringStreamBody(part,
                toApacheType(value.getContentType()),
                value.getFileName(),
                value.getName(),
                body.getMonitor());
    }



    private ContentBody toByteArrayBody(BodyPart value) {
        byte[] part = (byte[])value.getValue();
        return new ByteArrayBody(part,
                toApacheType(value.getContentType()),
                value.getFileName());
    }

    private ContentBody toStringBody(BodyPart value) {
        return new StringBody(String.valueOf(value.getValue()), toApacheType(value.getContentType()));
    }

    private boolean is(BodyPart value, Class<?> cls) {
        return cls.isAssignableFrom(value.getPartType());
    }

    private org.apache.http.entity.ContentType toApacheType(String type) {
        return org.apache.http.entity.ContentType.parse(type);
    }

    static List<NameValuePair> getList(Collection<BodyPart> parameters) {
        List<NameValuePair> result = new ArrayList<>();
        for (BodyPart entry : parameters) {
            result.add(new BasicNameValuePair(entry.getName(), entry.getValue().toString()));
        }
        return result;
    }
}
