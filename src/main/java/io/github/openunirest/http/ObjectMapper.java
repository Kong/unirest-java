package io.github.openunirest.http;

import io.github.openunirest.http.exceptions.UnirestException;
import io.github.openunirest.request.GenericType;

public interface ObjectMapper {
	<T> T readValue(String value, Class<T> valueType);
	default <T> T readValue(String value, GenericType<T> genericType){
		throw new UnirestException("Please implement me");
	}
	String writeValue(Object value);
}
