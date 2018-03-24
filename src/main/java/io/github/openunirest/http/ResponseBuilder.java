package io.github.openunirest.http;


import io.github.openunirest.http.exceptions.UnirestException;
import io.github.openunirest.http.options.Option;
import io.github.openunirest.http.options.Options;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static io.github.openunirest.http.BodyData.from;

public class ResponseBuilder {

    public HttpResponse<InputStream> asBinary(org.apache.http.HttpResponse response){
        return new HttpResponse<>(response, from(response.getEntity(), BodyData::getRawInput));
    }

    public <T> HttpResponse<T> asObject(org.apache.http.HttpResponse response, Class<? extends T> aClass) {
        return new HttpResponse<>(response, from(response.getEntity(), b -> toObject(b, aClass)));
    }

    private <T> T toObject(BodyData<T> b, Class<? extends T> aClass) {
        try {
            ObjectMapper o = getObjectMapper();
            return o.readValue(new String(b.getRawBytes(), b.getCharset()), aClass);
        } catch (UnsupportedEncodingException e) {
            throw new ParsingException(e);
        }
    }

    private ObjectMapper getObjectMapper() {
        return Options.tryGet(Option.OBJECT_MAPPER, ObjectMapper.class)
                .orElseThrow(() -> new UnirestException("No Object Mapper Configured. Please configure one with Unirest.setObjectMapper"));
    }


    public static class ParsingException extends RuntimeException {
        public ParsingException(Exception e){
            super(e);
        }
    }
}
