package io.github.openunirest.http;

public interface ObjectMapper {

	<T> T readValue(String value, Class<T> valueType);

	String writeValue(Object value);
}
