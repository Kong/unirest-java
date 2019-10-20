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

/**
 * a error handler for various failure scenarios
 */
public interface ErrorHandler {
    /**
     * Handler method for response errors.
     * Invoked if the response was NOT a 200-series response or a mapping exception happened.
     * @param response The HTTP Response
     */
    void accept(HttpResponse<?> response);

    /**
     * Handler method for request errors.
     * Invoked if something terrible happened when attempting to make the request
     * this could be due to connection failures or other systemic issues.
     * No response is available in this case. Although though this handler a response may be attempted.
     *
     * @param request The current request object. Note that it may be in a partially consumed state and may not be re-requestable.
     * @param e The exception that occured which failed the request.
     * @return A HttpResponse object. Normally unirest thows the exception and ho response is returned.
     */
    default HttpResponse<?> handle(HttpRequest<?> request, Exception e){
        throw new UnirestException(e);
    }

}
