package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import io.github.openunirest.http.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JacksonObjectMapper implements ObjectMapper {

	private com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

	public JacksonObjectMapper(){
		om.registerModule(new GuavaModule());
	}

	public <T> T readValue(String value, Class<T> valueType) {
		try {
			return om.readValue(value, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String writeValue(Object value) {
		try {
			return om.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

    public <T> T readValue(InputStream rawBody, Class<T> as) {
        try {
            return om.readValue(rawBody, as);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
