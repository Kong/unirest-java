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

package BehaviorTests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import io.javalin.http.Context;
import kong.unirest.core.*;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import org.assertj.core.data.MapEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.getProperty;
import static kong.unirest.core.JsonPatchRequest.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class RequestCapture {
    public String requestId = UUID.randomUUID().toString();
    public HeaderAsserts headers = new HeaderAsserts();
    public List<MultiPart> multiformparts = new ArrayList<>();
    public ArrayListMultimap<String, String> params = ArrayListMultimap.create();
    public String body;
    public String url;
    public String queryString;
    public HttpMethod method;
    public HashMap<String, String> routeParams = new HashMap<>();
    public String contentType;
    public JsonPatch jsonPatches;
    public Integer status;
    private boolean isProxied;
    public HashMap<String, String> cookies = new HashMap<>();


    public RequestCapture(){

    }

    public RequestCapture(Context req) {
        url = req.url();
        queryString = req.queryString();
        method = HttpMethod.valueOf(req.method().name());
        headers = new HeaderAsserts(req);
        writeQuery(req);
        populateParams(req);
        cookies.putAll(req.cookieMap());
        contentType = req.contentType();
        status = 200;
    }

    private static String toString(InputStream is) {
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }

    private void populateParams(Context req) {
        routeParams.putAll(req.pathParamMap());
    }

    public void writeBody(Context req) {
        if (Strings.nullToEmpty(req.contentType()).equals(CONTENT_TYPE)) {
            String body = req.body();
            jsonPatches = new JsonPatch(body);
            this.body = jsonPatches.toString();
        } else {
            //parseBodyToFormParams(req);
            writeMultipart(req);
        }
    }

    private void parseBodyToFormParams() {
        try {
            QueryParams.fromBody(this.body)
                    .getQueryParams()
                    .forEach(p -> {
                        params.put(p.getName(), p.getValue());
                    });
        }catch (UnirestException e){}
    }

    public void writeMultipart(Context req) {
        req.req().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(getProperty("java.io.tmpdir")));

        try {
            for (Part p : req.req().getParts()) {
                buildMultiContentPart(p);
            }
        } catch (ServletException e) {
            this.body = req.body();
            parseBodyToFormParams();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void buildMultiContentPart(Part part) throws IOException {
        MultiPart file = new MultiPart();
        file.fileName = part.getSubmittedFileName();
        file.type = part.getContentType();
        file.name = part.getName();
        file.fileType = part.getContentType();
        file.size = part.getSize();
        file.body = toString(part.getInputStream());
        file.headers = extractHeaders(part);
        if (Strings.isNullOrEmpty(part.getSubmittedFileName())) {
            var s = new Scanner(part.getInputStream()).useDelimiter("\\A");
            var value = s.hasNext() ? s.next() : "";
            params.put(part.getName(), value);
        }

        multiformparts.add(file);
    }

    private ListMultimap<String, String> extractHeaders(Part part) {
        ListMultimap<String, String> h = LinkedListMultimap.create();
        for(String header : part.getHeaderNames()){
            h.putAll(header, part.getHeaders(header));
        }
        return h;
    }

    private void writeQuery(Context req) {
        req.queryParamMap().forEach((key, value) -> params.putAll(key, value));
    }

    public RequestCapture assertNoHeader(String s) {
        headers.assertNoHeader(s);
        return this;
    }

    public RequestCapture assertHeader(String key, String... value) {
        headers.assertHeader(key, value);
        return this;
    }

    public RequestCapture assertParam(String key, String value) {
        assertTrue(params.containsKey(key), String.format("Expect param of '%s' but none was present", key));
        assertTrue(params.get(key).contains(value), "Expected Query or Form value: " + value);
        return this;
    }

    public MultiPart getFile(String fileName) {
        return getFileStream()
                .filter(f -> Objects.equals(f.fileName, fileName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("\nNo File With Name: " + fileName + "\n"
                        + "Found: " + getFileStream().map(f -> f.fileName).collect(Collectors.joining(" "))));
    }

    private Stream<MultiPart> getFileStream() {
        return multiformparts.stream()
                .filter(f -> f.isFile());
    }

    public MultiPart getFileByInput(String input) {
        return getFileStream()
                .filter(f -> Objects.equals(f.name, input))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No File from form: " + input));
    }

    public List<MultiPart> getAllFilesByInput(String input) {
        return getFileStream()
                .filter(f -> Objects.equals(f.name, input))
                .collect(Collectors.toList());
    }

    public RequestCapture assertFileContent(String input, String content) {
        assertEquals(content, getFileByInput(input).body);
        return this;
    }

    public RequestCapture assertBasicAuth(String username, String password) {
        headers.assertBasicAuth(username, password);
        return this;
    }

    public RequestCapture assertQueryString(String s) {
        assertEquals(s, queryString);
        return this;
    }

    public RequestCapture assertMethod(HttpMethod get) {
        assertEquals(get, method);
        return this;
    }

    public RequestCapture assertPathParam(String name, String value) {
        assertEquals(value, routeParams.get(name));
        return this;
    }

    public RequestCapture assertUrl(String s) {
        assertEquals(s, url);
        return this;
    }

    public void assertCharset(Charset charset) {
        assertTrue(contentType.endsWith(charset.toString()), "Expected Content Type With Charset: " + charset);
    }

    public RequestCapture assertJsonPatch(JsonPatchOperation op, String path, Object value) {
        assertNotNull(jsonPatches, "Asserting JSONPatch but no patch object present");
        assertTrue(jsonPatches.getOperations().contains(new JsonPatchItem(op, path, value)));
        return this;
    }

    public void setPatch(JsonPatch patch) {
        this.jsonPatches = patch;
    }

    public RequestCapture assertStatus(Integer i) {
        assertEquals(i, status);
        return this;
    }

    public void setIsProxied(boolean b) {
        this.isProxied = b;
    }

    public RequestCapture assertIsProxied(boolean b) {
        assertEquals(b, isProxied);
        return this;
    }

    public RequestCapture assertHeaderSize(String name, int size) {
        headers.assertHeaderSize(name, size);
        return this;
    }

    public RequestCapture assertBody(String o) {
        assertEquals(o, body);
        return this;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public RequestCapture assertContentType(ContentType content) {
        return assertContentType(content.getMimeType());
    }

    public RequestCapture assertContentType(String content) {
        return assertHeader("Content-Type", content);
    }

    public RequestCapture assertContentType(String content, String paramKey, String paramValue) {
        headers.assertHeaderWithParam("Content-Type", content, paramKey, paramValue);
        return this;
    }

    public RequestCapture assertMultiPartContentType() {
        headers.assertMultiPartContentType();
        return this;
    }

    public RequestCapture assertUrlEncodedContent() {
        return assertRawContentType("application/x-www-form-urlencoded; charset=UTF-8");
    }

    public RequestCapture assertCookie(String name, String value) {
        String c = cookies.get(name);
        assertNotNull(c, "expected a cookie to be passed to the server but got none. Name: " + name);
        assertEquals(value, c);
        return this;
    }

    public RequestCapture assertNoCookie(String name) {
        assertNull(cookies.get(name), "Cookie should not have been passed but it was! ");
        return this;
    }

    public RequestCapture assertRawContentType(String value) {
        headers.assertRawValue("Content-Type", value);
        return this;
    }

    public RequestCapture assertHeader(String name, Consumer<HeaderAsserts.HeaderValue> validator) {
        validator.accept(headers.getFirst(name));
        return this;
    }

    public RequestCapture assertBodyPart(String name, Consumer<MultiPart> validator) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(validator);
        validator.accept(
                multiformparts.stream()
                        .filter(f -> name.equalsIgnoreCase(f.name))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("No Form Body Part Found Named: " + name))
        );
        return this;
    }

    public RequestCapture assertAccepts(ContentType type) {
        return assertHeader("Accept", type.toString());
    }

    public static class MultiPart {
        public ListMultimap<String, String> headers = LinkedListMultimap.create();
        public String content;
        public String fileName;
        public String type;
        public String name;
        public String body;
        public String fileType;
        public long size;


        @JsonIgnore
        public boolean isFile(){
            return fileName != null;
        }

        public MultiPart assertBody(String content) {
            assertEquals(content, body);
            return this;
        }

        public MultiPart assertFileType(String type) {
            assertEquals(type, this.fileType);
            return this;
        }

        public MultiPart assertFileType(ContentType imageJpeg) {
            return assertFileType(imageJpeg.toString());
        }

        public MultiPart assertFileName(String s) {
            assertEquals(s, fileName);
            return this;
        }

        public MultiPart assertSize(long expected) {
            assertEquals(expected, this.size);
            return this;
        }

        public MultiPart exists() {
            assertTrue(this.size > 0);
            return this;
        }

        public MultiPart assertContentType(String contentType) {
            TestUtil.assertMultiMap(headers).contains(MapEntry.entry("content-type", contentType));
            return this;
        }

        public MultiPart assertContentDisposition(String disposition) {
            TestUtil.assertMultiMap(headers).contains(MapEntry.entry("content-disposition", disposition));
            return this;
        }
    }

}
