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

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static kong.unirest.JsonPatchOperation.*;
import static org.junit.Assert.*;

public class JsonPatchItemTest {

    @Test
    public void patchOperationsAreUniqueByTheirElements() {
        assertEquals(
                new JsonPatchItem(add, "/foo", "bar"),
                new JsonPatchItem(add, "/foo", "bar")
        );

        assertEquals(
                new JsonPatchItem(remove, "/foo"),
                new JsonPatchItem(remove, "/foo")
        );

        assertNotEquals(
                new JsonPatchItem(add, "/foo", "bar"),
                new JsonPatchItem(add, "/foo", "baz")
        );

        assertNotEquals(
                new JsonPatchItem(add, "/foo", "bar"),
                new JsonPatchItem(replace, "/foo", "bar")
        );

        assertNotEquals(
                new JsonPatchItem(remove, "/foo"),
                new JsonPatchItem(remove, "/foo", "baz")
        );
    }

    @Test
    public void testHash() {
        Set s = Sets.newHashSet(new JsonPatchItem(add, "/foo", "bar"),
                new JsonPatchItem(add, "/foo", "bar"));

        assertEquals(1, s.size());
    }

    @Test
    public void edgeEquals() {
        JsonPatchItem i = new JsonPatchItem(add, "/foo", "bar");
        assertTrue(i.equals(i));
        assertFalse(i.equals(null));
        assertFalse(i.equals(new Object()));
    }

    @Test
    public void basicTest() {
        JsonPatchItem i = new JsonPatchItem(add, "/foo", "bar");
        assertEquals(add, i.getOp());
        assertEquals("/foo", i.getPath());
        assertEquals("bar", i.getValue());
    }
}