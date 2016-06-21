package com.mashape.unirest.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class BodyReaders {
    public static BodyReader<JsonNode> forJsonNode(final byte[] rawBody, final String charset) {
        return new BodyReader<JsonNode>() {
            public JsonNode getBody() {
                try {
                    String jsonString = new String(rawBody, charset).trim();
                    return new JsonNode(jsonString);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static BodyReader<String> forString(final byte[] rawBody, final String charset) {
        return new BodyReader<String>() {
            public String getBody() {
                try {
                    return new String(rawBody, charset);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static BodyReader<InputStream> forInputStream(final InputStream rawBody) {
        return new BodyReader<InputStream>() {
            public InputStream getBody() {
                return rawBody;
            }
        };
    }

    public static <T> BodyReader<T> forObjectMapper(final ObjectMapper objectMapper,
                                                           final byte[] rawBody,
                                                           final String charset,
                                                           final Class<T> responseClass) {
        return new BodyReader<T>() {
            public T getBody() {
                try {
                    return objectMapper.readValue(new String(rawBody, charset), responseClass);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
