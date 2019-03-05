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

public class Unirest {

    private static UnirestInstance primaryInstance = new UnirestInstance(new Config());

    /**
     * Access the default configuration for the primary Unirest instance.
     * @return the config object of the primary instance
     */
    public static Config config() {
        return primaryInstance.config();
    }

    /**
     * Close the asynchronous client and its event loop. Use this method to close all the threads and allow an application to exit.
     * This will also clear any options returning Unirest to a default state
     */
    public static void shutDown() {
        shutDown(true);
    }

    /**
     * Close the asynchronous client and its event loop. Use this method to close all the threads and allow an application to exit.
     * @param clearOptions  indicates if options should be cleared. Note that the HttpClient, AsyncClient and thread monitors will not be retained after shutDown.
     */
    public static void shutDown(boolean clearOptions) {
        primaryInstance.shutDown(clearOptions);
    }

    /**
     * Start a GET HttpRequest which does not support a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public static GetRequest get(String url) {
        return primaryInstance.get(url);
    }

    /**
     * Start a HEAD HttpRequest which does not support a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public static GetRequest head(String url) {
        return primaryInstance.head(url);
    }

    /**
     * Start a OPTIONS HttpRequest which does not support a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public static GetRequest options(String url) {
        return primaryInstance.options(url);
    }

    /**
     * Start a POST HttpRequest which supports a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public static HttpRequestWithBody post(String url) {
        return primaryInstance.post(url);
    }

    /**
     * Start a DELETE HttpRequest which supports a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public static HttpRequestWithBody delete(String url) {
        return primaryInstance.delete(url);
    }

    /**
     * Start a PATCH HttpRequest which supports a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public static HttpRequestWithBody patch(String url) {
        return primaryInstance.patch(url);
    }

    /**
     * Start a PUT HttpRequest which supports a body from the primary config
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public static HttpRequestWithBody put(String url) {
        return primaryInstance.put(url);
    }

    /**
     * Start a PATCH HttpRequest which supports a JSON Patch builder.
     * this supports RFC-6902 https://tools.ietf.org/html/rfc6902
     * @param url the endpoint to access. Can include placeholders for path params using curly braces {}
     * @return A HttpRequest builder
     */
    public static JsonPatchRequest jsonPatch(String url) {
        return primaryInstance.jsonPatch(url);
    }

    public static HttpRequestWithBody request(String method, String url) {
        return primaryInstance.request(method, url);
    }

    /**
     * Does the config have currently running clients? Find out here.
     *
     * @return boolean
     */
    public static boolean isRunning() {
        return primaryInstance.isRunning();
    }

    /**
     * Spawn a new Unirest Instance with a new config.
     * Don't forget to shut it down when your done.
     * It will not be tracked and shutdown with Unirest.shutDown()
     *
     * @return a new UnirestInstance
     */
    public static UnirestInstance spawnInstance() {
        return new UnirestInstance(new Config());
    }

    /**
     * return the primary UnirestInstance.
     *
     * @return a new UnirestInstance
     */
    public static UnirestInstance primaryInstance() {
        return primaryInstance;
    }
}
