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

import com.google.common.base.Strings;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PagedListTest {

    @Test
    public void canGetAllTheBodies() {
        PagedList<String> list = new PagedList<>();
        list.addAll(asList(
                mkRequest("foo"),
                mkRequest("bar"),
                mkRequest("baz")
                ));

        List<String> bodies = list.getBodies();
        assertEquals(Arrays.asList("foo","bar","baz"), bodies);
    }

    @Test
    public void bodiesMustBeSucessful() {
        PagedList<String> list = new PagedList<>();
        list.addAll(asList(
                mkRequest("foo"),
                mkRequest(null),
                mkRequest("baz")
        ));

        List<String> bodies = list.getBodies();
        assertEquals(Arrays.asList("foo","baz"), bodies);
    }

    @Test
    public void canProcessSuccessfullResuls() {
        PagedList<String> list = new PagedList<>();
        list.addAll(asList(
                mkRequest("foo"),
                mkRequest(null),
                mkRequest("baz")
        ));

        final List<String> processed = new ArrayList<>();
        list.ifSuccess(e -> processed.add(e.getBody()));
        assertEquals(Arrays.asList("foo","baz"), processed);
    }

    @Test
    public void canProcessFailed() {
        PagedList<String> list = new PagedList<>();
        list.addAll(asList(
                mkRequest("foo"),
                mkRequest(null),
                mkRequest("baz")
        ));

        final List<String> processed = new ArrayList<>();
        list.ifFailure(e -> processed.add(e.getBody()));
        assertEquals(null, processed.get(0));
    }

    private HttpResponse<String> mkRequest(String foo) {
        HttpResponse<String> r = mock(HttpResponse.class);
        when(r.getBody()).thenReturn(foo);
        when(r.isSuccess()).thenReturn(!Strings.isNullOrEmpty(foo));
        return r;
    }
}