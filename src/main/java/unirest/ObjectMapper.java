package unirest;

public interface ObjectMapper {
	<T> T readValue(String value, Class<T> valueType);
	default <T> T readValue(String value, GenericType<T> genericType){
		throw new UnirestException("Please implement me");
	}
	String writeValue(Object value);
}
