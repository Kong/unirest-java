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

package kong.unirest.core.java;

import kong.unirest.core.MultipartBody;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BodyBuilderTest {

    @ParameterizedTest
    @CsvSource({
            "plain, plain",
            "a&b, a%26b",
            "a=b, a%3Db",
            "a+b, a%2Bb",
            "a%b, a%25b",
            "first name, first+name",
            "café, caf%C3%A9",
            "日本語, %E6%97%A5%E6%9C%AC%E8%AA%9E",
            "a%26b, a%2526b"
    })
    void encodesFormFieldNamesAndValues(String name, String encodedName) throws Exception {
        try (UnirestInstance client = Unirest.spawnInstance()) {
            MultipartBody request = client.post("http://localhost/form").field(name, "v +&=%");

            assertEquals(encodedName + "=v+%2B%26%3D%25", body(request));
        }
    }

    @Test
    void usesTheFormCharsetForNamesAndValues() throws Exception {
        try (UnirestInstance client = Unirest.spawnInstance()) {
            MultipartBody request = client.post("http://localhost/form")
                    .charset(StandardCharsets.ISO_8859_1)
                    .field("café+", "thé +");

            assertEquals("caf%E9%2B=th%E9+%2B", body(request));
        }
    }

    @Test
    void defaultsToUtf8WhenCharsetIsOmitted() throws Exception {
        try (UnirestInstance client = Unirest.spawnInstance()) {
            MultipartBody request = client.post("http://localhost/form")
                    .noCharset()
                    .field("café+", "thé +");

            assertEquals("caf%C3%A9%2B=th%C3%A9+%2B", body(request));
            assertEquals("application/x-www-form-urlencoded", request.getHeaders().getFirst("Content-Type"));
        }
    }

    @Test
    void preservesRepeatedFieldNames() throws Exception {
        try (UnirestInstance client = Unirest.spawnInstance()) {
            MultipartBody request = client.post("http://localhost/form")
                    .field("a&b", "one")
                    .field("a&b", "two");

            assertEquals("a%26b=one&a%26b=two", body(request));
        }
    }

    private String body(MultipartBody request) throws Exception {
        HttpRequest.BodyPublisher publisher = new BodyBuilder(request).getBody();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompletableFuture<String> result = new CompletableFuture<>();
        publisher.subscribe(new Flow.Subscriber<ByteBuffer>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                byte[] chunk = new byte[item.remaining()];
                item.get(chunk);
                bytes.write(chunk, 0, chunk.length);
            }

            @Override
            public void onError(Throwable error) {
                result.completeExceptionally(error);
            }

            @Override
            public void onComplete() {
                result.complete(bytes.toString(StandardCharsets.UTF_8));
            }
        });
        return result.get(5, TimeUnit.SECONDS);
    }
}
