package com.mashape.unirest.http;

public interface ObjectMapper {

    Object readValue(String value);

    String writeValue(Object value);
}
