/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static unirest.BodyData.from;


class ResponseBuilder {

    private final Config config;

    public ResponseBuilder(Config config) {
        this.config = config;
    }

    public HttpResponse<JsonNode> asJson(org.apache.http.HttpResponse response) {
        return new HttpResponseImpl<>(response, from(response.getEntity(), b -> toJson(b)));
    }

    public HttpResponse<InputStream> asBinary(org.apache.http.HttpResponse response){
        return new HttpResponseImpl<>(response, from(response.getEntity(), BodyData::getRawInput));
    }

    public <T> HttpResponse<T> asObject(org.apache.http.HttpResponse response, Class<? extends T> aClass) {
        return new HttpResponseImpl<>(response, from(response.getEntity(), b -> toObject(b, aClass)));
    }

    public <T> HttpResponse<T> asObject(org.apache.http.HttpResponse response, GenericType<T> genericType) {
        return new HttpResponseImpl<>(response, from(response.getEntity(), b -> toObject(b, genericType)));
    }

    public HttpResponse<String> asString(org.apache.http.HttpResponse response) {
        return new HttpResponseImpl<>(response, from(response.getEntity(), this::toString));
    }

    private <T> T toObject(BodyData<T> b, GenericType<T> genericType) {
        ObjectMapper o = getObjectMapper();
        return o.readValue(toString(b), genericType);
    }

    private <T> T toObject(BodyData<T> b, Class<? extends T> aClass) {
        ObjectMapper o = getObjectMapper();
        return o.readValue(toString(b), aClass);
    }

    private String toString(BodyData b) {
        try {
            return new String(b.getRawBytes(), b.getCharset());
        } catch (UnsupportedEncodingException e) {
            throw new ParsingException(e);
        }
    }

    private JsonNode toJson(BodyData<JsonNode> b) {
        String jsonString = toString(b);
        return new JsonNode(jsonString);
    }

    private ObjectMapper getObjectMapper() {
        return config.getObjectMapper();
    }

    public static class ParsingException extends RuntimeException {
        public ParsingException(Exception e){
            super(e);
        }
    }
}
