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

import java.nio.charset.StandardCharsets;
import java.util.Optional;

class HttpRequestJsonPatch extends BaseRequest<JsonPatchRequest> implements JsonPatchRequest {

    private JsonPatch items = new JsonPatch();

    HttpRequestJsonPatch(Config config, String url) {
        super(config, HttpMethod.PATCH, url);
        header("Content-Type", CONTENT_TYPE);
    }

    @Override
    public JsonPatchRequest add(String path, Object value) {
        items.add(path, value);
        return this;
    }

    @Override
    public JsonPatchRequest remove(String path) {
        items.remove(path);
        return this;
    }

    @Override
    public JsonPatchRequest replace(String path, Object value) {
        items.replace(path, value);
        return this;
    }

    @Override
    public JsonPatchRequest test(String path, Object value) {
        items.test(path, value);
        return this;
    }

    @Override
    public JsonPatchRequest move(String from, String path) {
        items.move(from, path);
        return this;
    }

    @Override
    public JsonPatchRequest copy(String from, String path) {
        items.copy(from, path);
        return this;
    }

    @Override
    public Optional<Body> getBody() {
        return Optional.of(this);
    }

    @Override
    public BodyPart uniPart() {
        String bodyAsString = items.toString();
        return new UnibodyString(bodyAsString, StandardCharsets.UTF_8);
    }

    @Override
    public boolean isMultiPart() {
        return false;
    }

    @Override
    public boolean isEntityBody() {
        return true;
    }
}