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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CallbackFutureTest {
    @Mock
    Callback<Empty> callback;
    @Mock
    HttpResponse<Empty> response;

    @Test
    void cancellationNotifiesOnlyOnceAndIgnoresLateResults() {
        var future = CallbackFuture.wrap(callback);

        assertThat(future.cancel(true)).isTrue();
        future.cancel(true);
        assertThat(future.complete(response)).isFalse();
        assertThat(future.completeExceptionally(new IOException("late failure"))).isFalse();

        verify(callback).cancelled();
        verifyNoMoreInteractions(callback);
    }

    @Test
    void cancellationAfterSuccessDoesNotNotifyCancelled() {
        var future = CallbackFuture.wrap(callback);

        assertThat(future.complete(response)).isTrue();
        assertThat(future.cancel(true)).isFalse();
        assertThat(future.complete(response)).isFalse();

        verify(callback).completed(response);
        verifyNoMoreInteractions(callback);
    }

    @Test
    void cancellationAfterFailureDoesNotNotifyCancelled() {
        var future = CallbackFuture.wrap(callback);
        var failure = new IOException("network failure");

        assertThat(future.completeExceptionally(failure)).isTrue();
        assertThat(future.cancel(true)).isFalse();
        assertThat(future.complete(response)).isFalse();

        verify(callback).failed(argThat(error -> error.getCause() == failure));
        verifyNoMoreInteractions(callback);
    }
}
