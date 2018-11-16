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

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface HttpRequest<R extends HttpRequest> {
    R routeParam(String name, String value);

    R basicAuth(String username, String password);

    R accept(String value);

    R header(String name, String value);

    R headers(Map<String, String> headerMap);

    R queryString(String name, Collection<?> value);

    R queryString(String name, Object value);

    R queryString(Map<String, Object> parameters);

    HttpRequest getHttpRequest();

    HttpResponse<String> asString() throws UnirestException;

    CompletableFuture<HttpResponse<String>> asStringAsync();

    CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback);

    HttpResponse<JsonNode> asJson() throws UnirestException;

    CompletableFuture<HttpResponse<JsonNode>> asJsonAsync();

    CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback);

    <T> HttpResponse<T> asObject(Class<? extends T> responseClass) throws UnirestException;

    <T> HttpResponse<T> asObject(GenericType<T> genericType) throws UnirestException;

    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass);

    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass, Callback<T> callback);

    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType);

    <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType, Callback<T> callback);

    HttpResponse<InputStream> asBinary() throws UnirestException;

    CompletableFuture<HttpResponse<InputStream>> asBinaryAsync();

    CompletableFuture<HttpResponse<InputStream>> asBinaryAsync(Callback<InputStream> callback);

    HttpMethod getHttpMethod();

    String getUrl();

    Headers getHeaders();

    Body getBody();
}
