/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
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

package unirest;

public class Unirest {

    private static UnirestInstance primaryInstance = new UnirestInstance(new Config());

    /**
     * Access the default configuration for the primary Unirest instance.
     */
    public static Config config(){
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

    public static GetRequest get(String url) {
        return primaryInstance.get(url);
    }

    public static GetRequest head(String url) {
        return primaryInstance.head(url);
    }

    public static GetRequest options(String url) {
        return primaryInstance.options(url);
    }

    public static HttpRequestWithBody post(String url) {
        return primaryInstance.post(url);
    }

    public static HttpRequestWithBody delete(String url) {
        return primaryInstance.delete(url);
    }

    public static HttpRequestWithBody patch(String url) {
        return primaryInstance.patch(url);
    }

    public static HttpRequestWithBody put(String url) {
        return primaryInstance.put(url);
    }

    public static JsonPatchRequest jsonPatch(String url) {
        return primaryInstance.jsonPatch(url);
    }

    public static boolean isRunning() {
        return primaryInstance.isRunning();
    }

    public static UnirestInstance spawnInstance() {
        return new UnirestInstance(new Config());
    }

    public static UnirestInstance primaryInstance() {
        return primaryInstance;
    }
}
