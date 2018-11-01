package unirest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import unirest.GenericType;
import unirest.JsonPatch;
import unirest.JsonPatchItem;
import org.json.JSONObject;
import unirest.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JacksonObjectMapper implements ObjectMapper {

	private com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

	public JacksonObjectMapper(){
		om.registerModule(new GuavaModule());
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(JsonPatchItem.class, new PatchSerializer());
		simpleModule.addSerializer(JsonPatch.class, new JsonPatchSerializer());
		simpleModule.addDeserializer(JsonPatchItem.class, new PatchDeserializer());
		simpleModule.addDeserializer(JsonPatch.class, new JsonPatchDeSerializer());
		om.registerModule(simpleModule);
	}

	@Override
	public <T> T readValue(String value, Class<T> valueType) {
		try {
			return om.readValue(value, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T readValue(String value, GenericType<T> genericType) {
		try {
			return om.readValue(value,  om.constructType(genericType.getType()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
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

	public static class JsonPatchSerializer extends JsonSerializer<JsonPatch>{
		@Override
		public void serialize(JsonPatch patch, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
			jgen.writeRawValue(patch.toString());
		}
	}

	public static class JsonPatchDeSerializer extends JsonDeserializer<JsonPatch>{
		@Override
		public JsonPatch deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
			String s = jsonParser.readValueAsTree().toString();
			return new JsonPatch(s);
		}
	}

	public static class PatchSerializer extends JsonSerializer<JsonPatchItem> {
		@Override
		public void serialize(JsonPatchItem jwk, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
			jgen.writeRaw(jwk.toString());
		}
	}

	public static class PatchDeserializer extends JsonDeserializer<JsonPatchItem> {
		@Override
		public JsonPatchItem deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
			String s = jsonParser.readValueAsTree().toString();
			return new JsonPatchItem(new JSONObject(s));
		}
	}
}
