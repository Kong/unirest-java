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

import static org.junit.jupiter.api.Assertions.assertEquals;

class MockResponseTest {

    @Test
    void canCreateASuccessfulMockResponse() {
        HttpResponse<String> response = MockResponse.ok("Hi Mom");
        assertEquals(200, response.getStatus());
        assertEquals("Hi Mom", response.getBody());
    }

    @Test
    void canAddHeaders() {
        HttpResponse<String> response = MockResponse.ok("Hi Mom")
                .withHeader("Accept", "application/xml");

        assertEquals("application/xml", response.getHeaders().getFirst("Accept"));
    }

    @Test
    void badRequestWithParsingException() {
        HttpResponse<String> response = MockResponse.bad("Hi Mom")
                .failedToParse();

        assertEquals(400, response.getStatus());
        assertEquals("Hi Mom", response.getParsingError().get().getOriginalBody());
    }

    @Test
    void canSetConfigOptions() {
        HttpResponse<String> response = MockResponse.bad("Hi Mom")
                .withConfigOptions(c -> c.setObjectMapper(new ObjectMapper() {
                    @Override
                    public <T> T readValue(String value, Class<T> valueType) {
                        return (T)new TransformedError();
                    }

                    @Override
                    public String writeValue(Object value) {
                        return null;
                    }
                }));

        assertEquals("Transformed!", response.mapError(TransformedError.class).message);
    }

    class TransformedError {
        String message = "Transformed!";
    }
}