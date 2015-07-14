package com.mashape.unirest.http;

public interface ObjectMapper {

    <T> T readValue(String value, Class<T> type);

    String writeValue(Object value);
}
