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

/**
 * A mock configuration that uses a {@link MockClient} instead of a real HTTP client.
 * <p>
 * This class extends {@link Config} and automatically creates a {@link MockClient}
 * as its HTTP client, allowing you to use all of Unirest's configuration options
 * without making actual network requests.
 * </p>
 * <p>
 * Use this class when you need a standalone mock configuration, for example when
 * creating a {@link UnirestInstance} specifically for testing.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create a mock configuration
 * MockConfig config = new MockConfig();
 *
 * // Use it with a UnirestInstance
 * UnirestInstance unirest = new UnirestInstance(config);
 *
 * // Access the mock client to set up expectations
 * MockClient mockClient = (MockClient) config.getClient();
 * mockClient.expect(HttpMethod.GET, "/api/users")
 *     .thenReturn("{\"id\": 1}")
 *     .withStatus(200);
 * }</pre>
 *
 * @see Config
 * @see MockClient
 * @see UnirestInstance
 */
public class MockConfig extends Config {
    private MockClient client = new MockClient(() -> this);


    /**
     * {@inheritDoc}
     *
     * @return the {@link MockClient} instance associated with this configuration
     */
    @Override
    public Client getClient() {
        return client;
    }
}
