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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentTypeTest {
    @Test
    void checkForBinaryTypes() {
        assertTrue(ContentType.BINARY_OCTET_STREAM.isBinary());
        assertTrue(ContentType.APPLICATION_OCTET_STREAM.isBinary());
        assertFalse(ContentType.APPLICATION_JSON.isBinary());
    }

    @Test
    void checkForBinaryTypesByString() {
        assertTrue(ContentType.isBinary(ContentType.APPLICATION_OCTET_STREAM.getMimeType()));
        assertTrue(ContentType.isBinary(ContentType.APPLICATION_OCTET_STREAM.getMimeType().toUpperCase()));
        assertFalse(ContentType.isBinary(ContentType.APPLICATION_JSON.getMimeType()));
        assertFalse(ContentType.isBinary(null));
    }

    @Test
    void anyOldBinary() {
        assertTrue(ContentType.isBinary("binary/thing"));
        assertFalse(ContentType.isBinary("application/thing"));
    }
}