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
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import io.javalin.http.Context;
import kong.unirest.core.*;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.getProperty;
import static kong.unirest.core.JsonPatchRequest.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class RequestCapture {
    public String requestId = UUID.randomUUID().toString();
    public ListMultimap<String, String> headers = LinkedListMultimap.create();
    public List<FormPart> files = new ArrayList<>();
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
        writeHeaders(req);
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
                if (!Strings.isNullOrEmpty(p.getSubmittedFileName())) {
                    buildFilePart(p);
                } else {
                    buildUrlEncodedParamPart(p);
                }
            }
        } catch (ServletException e) {
            this.body = req.body();
            parseBodyToFormParams();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void buildUrlEncodedParamPart(Part p) throws IOException {
        java.util.Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\A");
        String value = s.hasNext() ? s.next() : "";
        params.put(p.getName(), value);
    }

    public void buildFilePart(Part part) throws IOException {
        FormPart file = new FormPart();
        file.fileName = part.getSubmittedFileName();
        file.type = part.getContentType();
        file.inputName = part.getName();
        file.fileType = part.getContentType();
        file.size = part.getSize();
        file.body = toString(part.getInputStream());
        file.headers = extractHeaders(part);

        files.add(file);
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
        assertFalse(headers.containsKey(s), "Should Have No Header " + s);
        return this;
    }

    private RequestCapture writeHeaders(Context req) {
        Collections.list(req.req().getHeaderNames())
                .forEach(name -> Collections.list(req.req().getHeaders(name))
                        .forEach(value -> headers.put(name, value)));
        return this;
    }

    public RequestCapture assertHeader(String key, String... value) {
        assertThat(headers.asMap()).containsKey(key);
        assertThat(headers.get(key)).contains(value);
        return this;
    }

    public RequestCapture assertParam(String key, String value) {
        assertTrue(params.containsKey(key), String.format("Expect param of '%s' but none was present", key));
        assertTrue(params.get(key).contains(value), "Expected Query or Form value: " + value);
        return this;
    }

    public FormPart getFile(String fileName) {
        return getFileStream()
                .filter(f -> Objects.equals(f.fileName, fileName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("\nNo File With Name: " + fileName + "\n"
                        + "Found: " + getFileStream().map(f -> f.fileName).collect(Collectors.joining(" "))));
    }

    private Stream<FormPart> getFileStream() {
        return files.stream()
                .filter(f -> f.isFile());
    }

    public FormPart getFileByInput(String input) {
        return getFileStream()
                .filter(f -> Objects.equals(f.inputName, input))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No File from form: " + input));
    }

    public List<FormPart> getAllFilesByInput(String input) {
        return getFileStream()
                .filter(f -> Objects.equals(f.inputName, input))
                .collect(Collectors.toList());
    }

    public RequestCapture assertFileContent(String input, String content) {
        assertEquals(content, getFileByInput(input).body);
        return this;
    }

    public RequestCapture assertBasicAuth(String username, String password) {
        String raw = headers.get("Authorization").get(0);
        assertNotNull(raw, "Authorization Header Missing");
        String credentials = raw.replace("Basic ","");
        assertEquals(username + ":" + password, new String(Base64.getDecoder().decode(credentials)));
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
        assertTrue(contentType.endsWith(charset.toString()), "Expected Content Type With Charset: " + charset.toString());
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

    public RequestCapture assertHeaderSize(String foo, int size) {
        assertEquals(size, headers.get(foo).size());
        return this;
    }

    public RequestCapture assertBody(String o) {
        assertEquals(o, body);
        return this;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public RequestCapture assertContentType(String content) {
        return assertHeader("Content-Type", content);
    }

    public RequestCapture assertMultiPartContentType() {
        List<String> h = headers.get("Content-Type");
        assertEquals(1, h.size(), "Expected exactly 1 Content-Type header");
        List<String> parts = Splitter.on(";").trimResults().splitToList(h.get(0));
        assertEquals("multipart/form-data", parts.get(0));
        assertTrue(parts.get(1).startsWith("boundary="));
        assertEquals("charset=UTF-8", parts.get(2));
        return this;
    }

    public RequestCapture assertUrlEncodedContent() {
        return assertContentType("application/x-www-form-urlencoded; charset=UTF-8");
    }

    public RequestCapture assertCookie(String name, String value) {
        String c = cookies.get(name);
        assertNotNull(c, "expected a cookie to be passed to the server but got none. Name: " + name);
        assertEquals(value, c);
        return this;
    }

    public void assertNoCookie(String name) {
        assertNull(cookies.get(name), "Cookie should not have been passed but it was! ");
    }

    public static class FormPart {
        public String contentType;
        public ListMultimap<String, String> headers = LinkedListMultimap.create();
        public String content;
        public String fileName;
        public String type;
        public String inputName;
        public String body;
        public String fileType;
        public long size;


        @JsonIgnore
        public boolean isFile(){
            return fileName != null;
        }

        public FormPart assertBody(String content) {
            assertEquals(content, body);
            return this;
        }

        public FormPart assertFileType(String type) {
            assertEquals(type, this.fileType);
            return this;
        }

        public FormPart assertFileType(ContentType imageJpeg) {
            return assertFileType(imageJpeg.toString());
        }

        public FormPart assertFileName(String s) {
            assertEquals(s, fileName);
            return this;
        }

        public void assertSize(long expected) {
            assertEquals(expected, this.size);
        }

        public void exists() {
            assertTrue(this.size > 0);
        }
    }

}
