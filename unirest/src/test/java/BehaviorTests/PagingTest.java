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

import org.junit.Test;
import kong.unirest.PagedList;
import kong.unirest.Unirest;

import static org.junit.Assert.assertEquals;

public class PagingTest extends BddTest {

    @Test
    public void canFollowPaging() {
        MockServer.expectedPages(10);

        PagedList<RequestCapture> result =  Unirest.get(MockServer.PAGED)
                .asPaged(
                        r -> r.asObject(RequestCapture.class),
                        r -> r.getHeaders().getFirst("nextPage")
                );

        assertEquals(10, result.size());
    }

    @Test
    public void willReturnOnePageIfthereWasNoPaging() {

        PagedList<RequestCapture> result =  Unirest.get(MockServer.PAGED)
                .asPaged(
                        r -> r.asObject(RequestCapture.class),
                        r -> null
                );

        assertEquals(1, result.size());
    }
}
