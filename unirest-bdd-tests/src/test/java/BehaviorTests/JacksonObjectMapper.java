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

package BehaviorTests;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.guava.GuavaModule;
import kong.unirest.core.*;
import kong.unirest.core.json.JSONObject;

import java.io.File;
import java.io.InputStream;

public class JacksonObjectMapper implements kong.unirest.core.ObjectMapper {

	public tools.jackson.databind.ObjectMapper om;

	public JacksonObjectMapper(tools.jackson.databind.ObjectMapper om){
		this.om = om;
	}

	public JacksonObjectMapper(){
        JsonMapper.Builder builder = JsonMapper.builderWithJackson2Defaults();
        builder.addModule(new GuavaModule());
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(JsonPatchItem.class, new PatchSerializer());
		simpleModule.addSerializer(JsonPatch.class, new JsonPatchSerializer());
		simpleModule.addDeserializer(JsonPatchItem.class, new PatchDeserializer());
		simpleModule.addDeserializer(JsonPatch.class, new JsonPatchDeSerializer());
		simpleModule.addSerializer(HttpMethod.class, new HttpMethodSerializer());
		simpleModule.addDeserializer(HttpMethod.class, new HttpMethodDeSerializer());
        builder.configure(StreamWriteFeature.IGNORE_UNKNOWN, true);
        builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        builder.addModule(simpleModule);
        this.om = builder.build();
	}

	public <T> T readValue(File f, Class<T> valueType){
		try {
			return om.readValue(f, valueType);
		} catch (JacksonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T readValue(String value, Class<T> valueType) {
		try {
			return om.readValue(value, valueType);
		} catch (JacksonException e) {
			throw new UnirestException(e);
		}
	}

	@Override
	public <T> T readValue(String value, GenericType<T> genericType) {
		try {
			return om.readValue(value,  om.constructType(genericType.getType()));
		} catch (JacksonException e) {
			throw new UnirestException(e);
		}
	}

	@Override
	public String writeValue(Object value) {
		try {
			return om.writeValueAsString(value);
		} catch (JacksonException e) {
			throw new UnirestException(e);
		}
	}

	public <T> T readValue(InputStream rawBody, Class<T> as) {
		try {
			return om.readValue(rawBody, as);
		} catch (JacksonException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T readValue(byte[] content, Class<T> clazz) {
		try {
			return om.readValue(content, clazz);
		} catch (JacksonException e) {
			throw new RuntimeException(e);
		}
	}

	public static class JsonPatchSerializer extends ValueSerializer<JsonPatch>{
        @Override
        public void serialize(JsonPatch patch, JsonGenerator jgen, SerializationContext ctxt) throws JacksonException {
            jgen.writeRawValue(patch.toString());
        }
    }

	public static class JsonPatchDeSerializer extends ValueDeserializer<JsonPatch>{
		@Override
		public JsonPatch deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
			String s = jsonParser.readValueAsTree().toString();
			return new JsonPatch(s);
		}
	}

	public static class PatchSerializer extends ValueSerializer<JsonPatchItem> {
        @Override
        public void serialize(JsonPatchItem jwk, JsonGenerator jgen, SerializationContext ctxt) throws JacksonException {
            jgen.writeRaw(jwk.toString());
        }
    }

	public static class PatchDeserializer extends ValueDeserializer<JsonPatchItem> {
		@Override
		public JsonPatchItem deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
			String s = jsonParser.readValueAsTree().toString();
			return new JsonPatchItem(new JSONObject(s));
		}
	}

	public static class HttpMethodSerializer extends ValueSerializer<HttpMethod> {
        @Override
        public void serialize(HttpMethod httpMethod, JsonGenerator jgen, SerializationContext ctxt) throws JacksonException {
            jgen.writeString(httpMethod.name());
        }
    }

	public static class HttpMethodDeSerializer extends ValueDeserializer<HttpMethod> {
		@Override
		public HttpMethod deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
			String s = jsonParser.readValueAs(String.class);
			return HttpMethod.valueOf(s);
		}
	}
}
