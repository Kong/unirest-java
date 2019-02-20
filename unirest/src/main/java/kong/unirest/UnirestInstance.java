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

public class UnirestInstance implements AutoCloseable {

    private final Config config;

    public UnirestInstance(Config config){
        this.config = config;
    }
    /**
     * Access the default configuration for the primary Unirest instance.
     * @return the instance's config
     */
    public Config config() {
        return config;
    }

    /**
     * Close the asynchronous client and its event loop. Use this method to close all the threads and allow an application to exit.
     * This will also clear any options returning Unirest to a default state
     */
    public void shutDown() {
        shutDown(true);
    }

    /**
     * Close the asynchronous client and its event loop. Use this method to close all the threads and allow an application to exit.
     *
     * @param clearOptions indicates if options should be cleared. Note that the HttpClient, AsyncClient and thread monitors will not be retained after shutDown.
     */
    public void shutDown(boolean clearOptions) {
        config.shutDown(clearOptions);
    }

    /**
     * Start a GET HttpRequest which does not support a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public GetRequest get(String url) {
        return new HttpRequestNoBody(config, HttpMethod.GET, url);
    }

    /**
     * Start a HEAD HttpRequest which does not support a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public GetRequest head(String url) {
        return new HttpRequestNoBody(config, HttpMethod.HEAD, url);
    }

    /**
     * Start a OPTIONS HttpRequest which does not support a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public GetRequest options(String url) {
        return new HttpRequestNoBody(config, HttpMethod.OPTIONS, url);
    }

    /**
     * Start a POST HttpRequest which supports a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public HttpRequestWithBody post(String url) {
        return new HttpRequestBody(config, HttpMethod.POST, url);
    }

    /**
     * Start a DELETE HttpRequest which supports a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public HttpRequestWithBody delete(String url) {
        return new HttpRequestBody(config, HttpMethod.DELETE, url);
    }

    /**
     * Start a PATCH HttpRequest which supports a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public HttpRequestWithBody patch(String url) {
        return new HttpRequestBody(config, HttpMethod.PATCH, url);
    }

    /**
     * Start a PUT HttpRequest which supports a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public HttpRequestWithBody put(String url) {
        return new HttpRequestBody(config, HttpMethod.PUT, url);
    }

    /**
     * Start a PATCH HttpRequest which supports a JSON Patch builder.
     * this supports RFC-6902 https://tools.ietf.org/html/rfc6902
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public JsonPatchRequest jsonPatch(String url) {
        return new HttpRequestJsonPatch(config, url);
    }

    public HttpRequestWithBody request(String method, String url) {
        return new HttpRequestBody(config, HttpMethod.valueOf(method), url);
    }

    /**
     * Does the config have currently running clients? Find out here.
     *
     * @return boolean
     */
    public boolean isRunning() {
        return config.isRunning();
    }

    /**
     * Wraps shutdown and will automatically be called when UnirestInstance is
     * used with try-with-resource. This will alleviate the need to manually
     * call shutDown as it will be done automatically.
     */
    public void close() {
        shutDown(true);
    }
}
