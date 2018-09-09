package io.github.openunirest.http;

import com.google.gson.Gson;
import io.github.openunirest.request.GenericType;

public class GsonObjectMapper implements ObjectMapper {

    private final Gson gson = new Gson();

    @Override
    public <T> T readValue(String value, Class<T> valueType) {
        return gson.fromJson(value, valueType);
    }

    @Override
    public <T> T readValue(String value, GenericType<T> genericType) {
        return gson.fromJson(value, genericType.getType());
    }

    @Override
    public String writeValue(Object value) {
        return gson.toJson(value);
    }
}
