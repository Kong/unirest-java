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

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface HttpRequest<R extends HttpRequest> {
    /**
     * add a route param that replaces the matching {name}
     * For example routeParam("name", "fred") will replace {name} in
     * https://localhost/users/{user}
     * to
     * https://localhost/users/fred
     *
     * @param name the name of the param (do not include curly braces {}
     * @param value the value to replace the placeholder with
     * @return this request builder
     */
    R routeParam(String name, String value);

    /**
     * Basic auth credentials
     * @param username the username
     * @param password the password
     * @return this request builder
     */
    R basicAuth(String username, String password);

    /**
     * The Accept heder to send (e.g. application/json
     * @param value a valid mime type for the Accept header
     * @return this request builder
     */
    R accept(String value);

    /**
     * Add a http header
     * @param name name of the header
     * @param value value for the header
     * @return this request builder
     */
    R header(String name, String value);

    /**
     * Add headers as a map
     * @param headerMap a map of headers
     * @return this request builder
     */
    R headers(Map<String, String> headerMap);

    /**
     * add a query param to the url. The value will be URL-Encoded
     * @param name the name of the param
     * @param value the value of the param
     * @return this request builder
     */
    R queryString(String name, Object value);

    /**
     * Add multiple param with the same param name.
     * queryString("name", Arrays.asList("bob", "linda")) will result in
     * ?name=bob&amp;name=linda
     * @param name the name of the param
     * @param value a collection of values
     * @return this request builder
     */
    R queryString(String name, Collection<?> value);

    /**
     * Add query params as a map of name value pairs
     * @param parameters a map of params
     * @return this request builder
     */
    R queryString(Map<String, Object> parameters);

    /**
     * Pass a ObjectMapper for the request. This will override any globally
     * configured ObjectMapper
     * @param mapper the ObjectMapper
     * @return this request builder
     */
    R withObjectMapper(ObjectMapper mapper);

    /**
     * Executes the request and returns the response with the body mapped into a String
     * @return response
     */
    HttpResponse<String> asString();

    /**
     * Executes the request asynchronously and returns the response with the body mapped into a String
     * @return a CompletableFuture of a response
     */
    CompletableFuture<HttpResponse<String>> asStringAsync();

    /**
     * Executes the request asynchronously and returns the response with the body mapped into a String
     * @param callback a callback handler
     * @return a CompletableFuture of a response
     */
    CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback);

    /**
     * Executes the request and returns the response with the body mapped into a JsonNode
     * @return response
     */
    HttpResponse<JsonNode> asJson();

    /**
     * Executes the request asynchronously and returns the response with the body mapped into a JsonNode
     * @return a CompletableFuture of a response
     */
    CompletableFuture<HttpResponse<JsonNode>> asJsonAsync();

    /**
     * Executes the request asynchronously and returns the response with the body mapped into a JsonNode
     * @param callback a callback handler
     * @return a CompletableFuture of a response
     */
    CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback);

    /**
     * Executes the request and returns the response with the body mapped into T by a configured ObjectMapper
     * @param responseClass the class to return. This will be passed to the ObjectMapper
     * @param <T> the return type
     * @return a response
     */
    <T> HttpResponse<T> asObject(Class<? extends T> responseClass);

    /**
     * Executes the request and returns the response with the body mapped into T by a configured ObjectMapper
     * @param genericType the genertic type to return. This will be passed to the ObjectMapper
     * @param <T> the return type
     * @return a response
     */
    <T> HttpResponse<T> asObject(GenericType<T> genericType);

    /**
     * Execute the request and pass the raw response to a function for mapping.
     * This raw response contains the original InputStream and is suitable for
     * reading large responses.
     * @param function the function to map the response into a object of T
     * @param <T> The type of the response mapping
     * @return A HttpResponse containing T as the body
     */
    <T> HttpResponse<T> asObject(Function<RawResponse, T> function);

    /**
     * Executes the request asynchronously and returns response with the body mapped into T by a configured ObjectMapper
     * @param responseClass the class type to map to
     * @param <T> the return type
     * @return a CompletableFuture of a response
     */
    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass);

    /**
     * Executes the request asynchronously, mapping to a type via the configured object mapper and then passed to a callback handler.
     * @param responseClass the type for the ObjectMapper to map to
     * @param callback a callback for handling the body post mapping
     * @param <T> the return type
     * @return a CompletableFuture of a HttpResponse containing the body of T
     */
    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass, Callback<T> callback);

    /**
     * Executes the request asynchronously, and use a GenericType with the ObjectMapper
     * @param genericType the generic type containing the type
     * @param <T> the type of the response
     * @return a CompletableFuture of a HttpResponse containing the body of T
     */
    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType);

    /**
     * Executes the request asynchronously, and use a GenericType with the ObjectMapper
     * @param genericType the generic type containing the type
     * @param callback a callback for handling the body post mapping
     * @param <T> the type of the response
     * @return a CompletableFuture of a HttpResponse containing the body of T
     */
    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType, Callback<T> callback);

    /**
     * Executes the request asynchronously, and pass the raw response to a function for mapping.
     * This raw response contains the original InputStream and is suitable for
     * reading large responses
     * @param function a function to map the raw request into a object
     * @param <T> the type of the response
     * @return a CompletableFuture of a HttpResponse containing the body of T
     */
    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Function<RawResponse, T> function);

    /**
     * Executes the request and returns a copy of the original InputStream
     * @deprecated This method returns a copy of the original stream and is not suitable for streaming. Use asObject(Function&lt;RawResponse, T&gt; function)
     * @return a HttpResponse with a InputStream
     */
    @Deprecated
    HttpResponse<InputStream> asBinary();

    /**
     * Executes the request and writes the contents into a file
     * @param path The path to the file.
     * @return a file containing the results
     */
    HttpResponse<File> asFile(String path);

    /**
     * asynchronously executes the request and writes the contents into a file
     * @param path The path to the file.
     * @return a file containing the results
     */
    CompletableFuture<HttpResponse<File>> asFileAsync(String path);

    /**
     * asynchronously executes the request and writes the contents into a file
     * @param path The path to the file.
     * @param callback a callback for handling the body post mapping
     * @return a file containing the results
     */
    CompletableFuture<HttpResponse<File>> asFileAsync(String path, Callback<File> callback);

    /**
     * Executes the request asynchronously and returns a copy of the original InputStream
     * @deprecated This method returns a copy of the original stream and is not suitable for streaming. Use asObjectAsync(Function&lt;RawResponse, T&gt; function)
     * @return a CompletableFuture of a HttpResponse with a InputStream
     */
    @Deprecated
    CompletableFuture<HttpResponse<InputStream>> asBinaryAsync();

    /**
     * Executes the request asynchronously and returns a copy of the original InputStream which is passed to a callback
     * @param callback the callback
     * @deprecated This method returns a copy of the original stream and is not suitable for streaming. Use asObjectAsync(Function&lt;RawResponse, T&gt; function)
     * @return a CompletableFuture of a HttpResponse with a InputStream
     */
    @Deprecated
    CompletableFuture<HttpResponse<InputStream>> asBinaryAsync(Callback<InputStream> callback);

    /**
     * Execute the request asynchronously and pass the raw response to a consumer.
     * This raw response contains the original InputStream and is suitable for
     * reading large responses
     * @param consumer a consumer function
     */
    void thenConsume(Consumer<RawResponse> consumer);

    /**
     * Execute the request and pass the raw response to a consumer.
     * This raw response contains the original InputStream and is suitable for
     * reading large responses
     * @param consumer a consumer function
     */
    void thenConsumeAsync(Consumer<RawResponse> consumer);

    /**
     * @return The HTTP method of the request
     */
    HttpMethod getHttpMethod();

    /**
     * @return The current URL string for the request
     */
    String getUrl();

    /**
     * @return the current headers for the request
     */
    Headers getHeaders();

    /**
     * Deprecated because it exposes Apache
     * @return The current body object.
     */
    @Deprecated
    Body getBody();
}
