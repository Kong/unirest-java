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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompoundInterceptorTest {
    @Mock
    Interceptor t1;
    @Mock
    Interceptor t2;
    @Mock
    HttpRequest<?> request;
    @Mock
    Config config;
    MockResponse<?> response = new MockResponse<>();
    @Mock
    HttpRequestSummary summary;


    @Test
    void willExecuteAllOnBefore() {
        CompoundInterceptor compound = new CompoundInterceptor(
                Arrays.asList(t1, t2)
        );

        compound.onRequest(request, config);

        verify(t1).onRequest(request, config);
        verify(t2).onRequest(request, config);
        verifyNoMoreInteractions(t1);
        verifyNoMoreInteractions(t2);
    }

    @Test
    void willExecuteAllOnAfter() {
        CompoundInterceptor compound = new CompoundInterceptor(
                Arrays.asList(t1, t2)
        );

        compound.onResponse(response, summary, config);

        verify(t1).onResponse(response, summary, config);
        verify(t2).onResponse(response, summary, config);
        verifyNoMoreInteractions(t1);
        verifyNoMoreInteractions(t2);
    }

    @Test
    void exceptionsStopsArFirstOne() {
        CompoundInterceptor compound = new CompoundInterceptor(
                Arrays.asList(t1, t2)
        );
        RuntimeException e = new RuntimeException();
        when(t2.onFail(e, summary, config)).thenReturn(new MockResponse<>());
        HttpResponse r = compound.onFail(e, summary, config);
        assertTrue(r instanceof MockResponse);

        verify(t1).onFail(e, summary, config);
        verify(t2).onFail(e, summary, config);
        verifyNoMoreInteractions(t1);
        verifyNoMoreInteractions(t2);
    }

    @Test
    void willThrowIfNothingElse() {
        CompoundInterceptor compound = new CompoundInterceptor(
                Collections.emptyList()
        );

        RuntimeException e = new RuntimeException();
        UnirestException u = assertThrows(UnirestException.class,
                () -> compound.onFail(e, summary, config));

        assertSame(e, u.getCause());
    }

    @Test
    void theDefaultInterceptorIsTheDefault() {
        CompoundInterceptor compound = new CompoundInterceptor();
        assertEquals(1, compound.size());
        assertTrue(compound.getInterceptors().get(0) instanceof DefaultInterceptor);
    }

    @Test
    void buildingWithCollectionDoesNotIncludeDefault() {
        CompoundInterceptor compound = new CompoundInterceptor(Collections.emptyList());
        assertEquals(0, compound.size());
    }

    @Test
    void canReplaceDefault() {
        CompoundInterceptor compound = new CompoundInterceptor();
        compound.register(t1);
        compound.register(t2);
        assertEquals(2, compound.size());
        assertSame(t1, compound.getInterceptors().get(0));
        assertSame(t2, compound.getInterceptors().get(1));
    }

    @Test
    void cantAddTheSameOneTwice() {
        CompoundInterceptor compound = new CompoundInterceptor();
        compound.register(t1);
        compound.register(t1);

        assertEquals(1, compound.size());
    }
}