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
package kong.unirest.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.GenericType;
import kong.unirest.jackson.JacksonObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JacksonObjectMapperTest {
    private JacksonObjectMapper om = new JacksonObjectMapper();

    @Test
    void canWrite() throws JSONException {
        TestMe test = new TestMe("foo", 42, new TestMe("bar", 666, null));

        String json = om.writeValue(test);

        JSONAssert.assertEquals(
                "{\"text\":\"foo\",\"nmbr\":42,\"another\":{\"text\":\"bar\",\"nmbr\":666,\"another\":null}}"
                , json
                , true
        );
    }

    @Test
    void canRead(){
        TestMe test = om.readValue("{\"text\":\"foo\",\"nmbr\":42,\"another\":{\"text\":\"bar\",\"nmbr\":666,\"another\":null}}",
                TestMe.class);

        assertEquals("foo", test.text);
        assertEquals(42, test.nmbr);
        assertEquals("bar", test.another.text);
        assertEquals(666, test.another.nmbr);
        assertEquals(null, test.another.another);
    }

    @Test
    void canReadGenerics(){
        List<TestMe> testList = om.readValue("[{\"text\":\"foo\",\"nmbr\":42,\"another\":{\"text\":\"bar\",\"nmbr\":666,\"another\":null}}]",
                new GenericType<List<TestMe>>(){});

        TestMe test = testList.get(0);

        assertEquals("foo", test.text);
        assertEquals(42, test.nmbr);
        assertEquals("bar", test.another.text);
        assertEquals(666, test.another.nmbr);
        assertEquals(null, test.another.another);
    }

    @Test
    void configSoItFails() {
        ObjectMapper jom = new ObjectMapper();
        jom.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        JacksonObjectMapper j = new JacksonObjectMapper(jom);

        try {
            j.readValue("{\"something\": [1,2,3] }", TestMe.class);
            fail("Should have thrown");
        }catch (Exception e) {
            assertEquals("com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field \"something\" (class kong.unirest.jackson.JacksonObjectMapperTest$TestMe), not marked as ignorable (3 known properties: \"another\", \"text\", \"nmbr\"])\n" +
                            " at [Source: (String)\"{\"something\": [1,2,3] }\"; line: 1, column: 16] (through reference chain: kong.unirest.jackson.JacksonObjectMapperTest$TestMe[\"something\"])",
                    e.getMessage());
        }
    }

    public static class TestMe {
        public String text;
        public int nmbr;
        public TestMe another;

        public TestMe(){}

        public TestMe(String text, Integer nmbr, TestMe another) {
            this.text = text;
            this.nmbr = nmbr;
            this.another = another;
        }
    }
}