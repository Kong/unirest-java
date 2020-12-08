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



import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class HeadersTest {

    private final String ls = System.lineSeparator();

    @Test
    void canGetHeaders() {
        Headers headers = new Headers();
        headers.add("Accepts","application/json");

        Header h = headers.all().get(0);

        assertEquals("Accepts", h.getName());
        assertEquals("application/json", h.getValue());
    }

    @Test
    void dontBombOnNull(){
        Headers h = new Headers();
        h.add(null, "application/json");

        assertEquals(0, h.size());
    }

    @Test
    void toStringOverride() {
        Headers h  = new Headers();
        h.add("a", "1");
        h.add(null, "2");
        h.add("c", () -> "3");
        h.add("d", (String) null);

        String toString = h.toString();
        assertEquals("a: 1" + ls +
                "c: 3" + ls +
                "d: null", toString);
    }

    @Test
    void headersAreEqualIfEntryListIsEqual() {
        Headers h  = new Headers();
        h.add("Accepts", "application/json");

        Headers j  = new Headers();
        j.add("Accepts", "application/json");

        assertEquals(h, j);
    }

    @Test
    void headersAreEqualIfEntryListIsEqual_orderMatters() {
        Headers h  = new Headers();
        h.add("Accepts", "application/json");
        h.add("Content-Type", "application/xml");

        Headers j  = new Headers();
        j.add("Content-Type", "application/xml");
        j.add("Accepts", "application/json");

        assertNotEquals(h, j);
    }

    @Test
    void canCreateHeadersFromACollection() {
        Headers h = new Headers(asList(
                new Headers.Entry("Accepts", "application/json"),
                new Headers.Entry("Content-Type", "application/xml"))
        );

        assertEquals("application/json", h.getFirst("Accepts"));
        assertEquals("application/xml", h.getFirst("Content-Type"));
    }
}