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
package kong.unirest;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


import java.util.List;

import static org.junit.Assert.*;

public class GsonObjectMapperTest {
    private GsonObjectMapper om = new GsonObjectMapper();

    @Test
    public void canWrite() {
        TestMe test = new TestMe("foo", 42, new TestMe("bar", 666, null));

        String json = om.writeValue(test);

        JSONAssert.assertEquals(
                "{\"text\":\"foo\",\"nmbr\":42,\"another\":{\"text\":\"bar\",\"nmbr\":666}}"
                , json
                , true
        );
    }

    @Test
    public void canRead(){
        TestMe test = om.readValue("{\"text\":\"foo\",\"nmbr\":42,\"another\":{\"text\":\"bar\",\"nmbr\":666}}",
                TestMe.class);

        assertEquals("foo", test.text);
        assertEquals(42, test.nmbr);
        assertEquals("bar", test.another.text);
        assertEquals(666, test.another.nmbr);
        assertEquals(null, test.another.another);
    }

    @Test
    public void canReadGenerics(){
        List<TestMe> testList = om.readValue("[{\"text\":\"foo\",\"nmbr\":42,\"another\":{\"text\":\"bar\",\"nmbr\":666,\"another\":null}}]",
                new GenericType<List<TestMe>>(){});

        TestMe test = testList.get(0);

        assertEquals("foo", test.text);
        assertEquals(42, test.nmbr);
        assertEquals("bar", test.another.text);
        assertEquals(666, test.another.nmbr);
        assertEquals(null, test.another.another);
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