package io.github.openunirest.http;

import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

import static io.github.openunirest.http.utils.ResponseUtils.getCharsetfromResponse;
import static io.github.openunirest.http.utils.ResponseUtils.getRawBody;

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
    private final InputStream rawInput;
    private T transFormedBody;

    private BodyData() {
        charset = null;
        rawBytes = null;
        rawInput = null;
    }

    private BodyData(HttpEntity entity) {
        this.charset = getCharsetfromResponse(entity);
        this.rawBytes = getRawBody(entity);
        this.rawInput = new ByteArrayInputStream(rawBytes);
    }

    public String getCharset() {
        return charset;
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    public InputStream getRawInput() {
        return rawInput;
    }

    public T getTransFormedBody() {
        return transFormedBody;
    }

    private void transformBody(Function<BodyData<T>, T> transformer) {
        this.transFormedBody = transformer.apply(this);
    }
}
