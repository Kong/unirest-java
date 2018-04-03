package io.github.openunirest.http;

import io.github.openunirest.http.exceptions.UnirestException;
import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

import static io.github.openunirest.http.utils.ResponseUtils.getCharSet;
import static io.github.openunirest.http.utils.ResponseUtils.getRawBody;

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
