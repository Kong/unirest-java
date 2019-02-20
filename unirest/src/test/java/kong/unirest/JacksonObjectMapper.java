/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kong.unirest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.json.JSONObject;

import java.io.File;
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
		simpleModule.addSerializer(HttpMethod.class, new HttpMethodSerializer());
		simpleModule.addDeserializer(HttpMethod.class, new HttpMethodDeSerializer());
		om.registerModule(simpleModule);
	}

	public <T> T readValue(File f, Class<T> valueType){
		try {
			return om.readValue(f, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T readValue(String value, Class<T> valueType) {
		try {
			return om.readValue(value, valueType);
		} catch (IOException e) {
			throw new UnirestException(e);
		}
	}

	@Override
	public <T> T readValue(String value, GenericType<T> genericType) {
		try {
			return om.readValue(value,  om.constructType(genericType.getType()));
		} catch (IOException e) {
			throw new UnirestException(e);
		}
	}

	@Override
	public String writeValue(Object value) {
		try {
			return om.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new UnirestException(e);
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

	public static class HttpMethodSerializer extends JsonSerializer<HttpMethod> {
		@Override
		public void serialize(HttpMethod httpMethod, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
			jgen.writeString(httpMethod.name());
		}
	}

	public static class HttpMethodDeSerializer extends JsonDeserializer<HttpMethod> {
		@Override
		public HttpMethod deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
			String s = jsonParser.readValueAs(String.class);
			return HttpMethod.valueOf(s);
		}
	}
}
