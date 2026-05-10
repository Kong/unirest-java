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
 * A callback interface for handling asynchronous HTTP responses.
 * <p>
 * Implement this interface to receive notifications when an asynchronous
 * HTTP request completes, fails, or is cancelled. This is used with
 * asynchronous request methods to process responses without blocking
 * the calling thread.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Unirest.get("http://example.com/api")
 *     .asStringAsync(new Callback<String>() {
 *         @Override
 *         public void completed(HttpResponse<String> response) {
 *             System.out.println("Response: " + response.getBody());
 *         }
 *
 *         @Override
 *         public void failed(UnirestException e) {
 *             System.err.println("Request failed: " + e.getMessage());
 *         }
 *
 *         @Override
 *         public void cancelled() {
 *             System.out.println("Request was cancelled");
 *         }
 *     });
 * }</pre>
 *
 * @param <T> the type of the response body
 */
public interface Callback<T> {
	/**
	 * Called when the asynchronous HTTP request completes successfully.
	 * <p>
	 * This method is invoked when the HTTP request finishes and a response
	 * is received, regardless of the HTTP status code. Check
	 * {@link HttpResponse#getStatus()} to determine if the response
	 * indicates success or an error.
	 * </p>
	 *
	 * @param response the HTTP response containing the status, headers, and body
	 */
	void completed(HttpResponse<T> response);

	/**
	 * Called when the asynchronous HTTP request fails due to an exception.
	 * <p>
	 * This method is invoked when the request cannot be completed due to
	 * network errors, connection timeouts, or other exceptional conditions
	 * that prevent receiving a response.
	 * </p>
	 * <p>
	 * The default implementation does nothing. Override this method to
	 * handle failure scenarios.
	 * </p>
	 *
	 * @param e the exception that caused the request to fail
	 */
	default void failed(UnirestException e) {}

	/**
	 * Called when the asynchronous HTTP request is cancelled.
	 * <p>
	 * This method is invoked when the request is cancelled before completion,
	 * typically by calling {@code cancel()} on the returned {@link java.util.concurrent.Future}.
	 * </p>
	 * <p>
	 * The default implementation does nothing. Override this method to
	 * handle cancellation scenarios.
	 * </p>
	 */
	default void cancelled() {}
}
