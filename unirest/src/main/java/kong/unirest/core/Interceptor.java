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
 * Each configuration of Unirest has an interceptor.
 * This has three stages:
 *      * onRequest allows you to modify of view the request before it it sent
 *      * onResponse allows you to view the response. This includes both successful and failed but valid responses
 *      * onFail is called if a total connection or network  failure occurred.
 */
public interface Interceptor {
    /**
     * Called just before a request. This can be used to view or modify the request.
     * this could be used for things like
     *      - Logging the request
     *      - Injecting tracer headers
     *      - Record metrics
     * The default implementation does nothing at all
     * @param request the request
     * @param config the current configuration
     */
    default void onRequest(HttpRequest<?> request, Config config) {
    }

    /**
     * Called just after the request. This can be used to view the response,
     * Perhaps for logging purposes or just because you're curious.
     *  @param response the response
     *  @param request a summary of the request
     *  @param config the current configuration
     */
    default void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
    }

    /**
     * Called in the case of a total failure.
     * This would be where Unirest was completely unable to make a request at all for reasons like:
     *      - DNS errors
     *      - Connection failure
     *      - Connection or Socket timeout
     *      - SSL/TLS errors
     *
     * The default implimentation simply wraps the exception in a UnirestException and throws it.
     * It is possible to return a different response object from the original if you really
     * didn't want to every throw exceptions. Keep in mind that this is a lie
     *
     * Nevertheless, you could return something like a kong.unirest.core.FailedResponse
     * 
     * @param e the exception
     * @param request the original request
     * @param config the current config
     * @return a alternative response.
     */
    default HttpResponse<?> onFail(Exception e, HttpRequestSummary request, Config config) throws UnirestException {
        throw new UnirestException(e);
    }
}
