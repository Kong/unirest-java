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
package kong.unirest.modules.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import kong.unirest.core.GenericType;
import kong.unirest.core.ObjectMapper;
import kong.unirest.core.UnirestException;
import tools.jackson.databind.json.JsonMapper;

import java.util.function.Consumer;


public class JacksonObjectMapper implements ObjectMapper {
    private tools.jackson.databind.ObjectMapper om;

    public JacksonObjectMapper(){
        this(c -> {});
    }

    /**
     * Pass in any additional JsonMapper configurations you want
     * @param configurations consumer of configurations to perform on the tools.jackson.databind.JsonMapper.Builder
     */
    public JacksonObjectMapper(Consumer<JsonMapper.Builder> configurations) {
        JsonMapper.Builder builder = JsonMapper.builderWithJackson2Defaults();
        builder.configure(StreamWriteFeature.IGNORE_UNKNOWN, true);
        builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configurations.accept(builder);
        this.om = builder.build();
    }

    public JacksonObjectMapper(tools.jackson.databind.ObjectMapper om){
        this.om = om;
    }

    public tools.jackson.databind.ObjectMapper getJacksonMapper(){
        return om;
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
}
