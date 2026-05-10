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

package kong.unirest.core;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MultiPartBuilderTest {

    @Test
    void buildSimple() {
        var part =  MultiPartBuilder.named("jerry")
                                .value("is cool")
                                .contentType("plain/text")
                                .toBodyPart();

        assertInstanceOf(ParamPart.class, part);
        assertEquals("jerry", part.getName());
        assertEquals("is cool", part.getValue());
        assertEquals("plain/text", part.getContentType());
    }

    @Test
    void buildInputStreamPart() {
        var value = new ByteArrayInputStream(new byte[]{});
        var part = MultiPartBuilder.named("jerry")
                .value(value)
                .fileName("jazzy.json")
                .contentType("application/json")
                .toBodyPart();

        assertInstanceOf(InputStreamPart.class, part);
        assertEquals("jerry", part.getName());
        assertSame(value, part.getValue());
        assertEquals("application/json", part.getContentType());
        assertEquals("jazzy.json", part.getFileName());
    }

    @Test
    void buildFilePart() {
        var value = new File("/jazzy.json");
        var part = MultiPartBuilder.named("jerry")
                .value(value)
                .fileName("jazzy.json")
                .contentType("application/json")
                .toBodyPart();

        assertInstanceOf(FilePart.class, part);
        assertEquals("jerry", part.getName());
        assertSame(value, part.getValue());
        assertEquals("application/json", part.getContentType());
        assertEquals("jazzy.json", part.getFileName());
    }

    @Test
    void byteArrayPart() {
        var value = new byte[]{};
        var part = MultiPartBuilder.named("jerry")
                .value(value)
                .fileName("jazzy.json")
                .contentType("application/json")
                .toBodyPart();

        assertInstanceOf(ByteArrayPart.class, part);
        assertEquals("jerry", part.getName());
        assertSame(value, part.getValue());
        assertEquals("application/json", part.getContentType());
        assertEquals("jazzy.json", part.getFileName());
    }

    @Test
    void buildWithObject(){
        Object value = new File("/jazzy.json");
        var part = MultiPartBuilder.named("jerry")
                .value(value)
                .fileName("jazzy.json")
                .contentType("application/json")
                .toBodyPart();

        assertInstanceOf(FilePart.class, part);
        assertEquals("jerry", part.getName());
        assertSame(value, part.getValue());
        assertEquals("application/json", part.getContentType());
        assertEquals("jazzy.json", part.getFileName());
    }
}