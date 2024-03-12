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

/*
The MIT License

Copyright for portions of unirest-java are held by Kong Inc (c) 2018.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package kong.unirest.mappers.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kong.unirest.core.GenericType;
import kong.unirest.core.ObjectMapper;
import kong.unirest.core.UnirestException;

import java.io.IOException;
import java.util.function.Consumer;


public class JacksonObjectMapper implements ObjectMapper {
    private final com.fasterxml.jackson.databind.ObjectMapper om;

    public JacksonObjectMapper(){
        this(c -> {});
    }

    /**
     * Pass in any additional ObjectMapper configurations you want
     * @param configurations consumer of confiruations to perform on the com.fasterxml.jackson.databind.ObjectMapper
     */
    public JacksonObjectMapper(Consumer<com.fasterxml.jackson.databind.ObjectMapper> configurations) {
        this(new com.fasterxml.jackson.databind.ObjectMapper());
        om.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //om.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        om.registerModule(new JavaTimeModule());
        configurations.accept(om);
    }

    public JacksonObjectMapper(com.fasterxml.jackson.databind.ObjectMapper om){
        this.om = om;
    }

    public com.fasterxml.jackson.databind.ObjectMapper getJacksonMapper(){
        return om;
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
}
