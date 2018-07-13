package BehaviorTests;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.github.openunirest.http.HttpMethod;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import spark.Request;
import util.TestUtil;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static java.lang.System.getProperty;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class RequestCapture {
    public Map<String, String> headers = new LinkedHashMap<>();
    public List<File> files = new ArrayList<>();
    public Multimap<String, String> params = HashMultimap.create();
    public String body;
    public String url;
    public String queryString;
    public HttpMethod method;
    public String param;
    public String contentType;

    public RequestCapture() {
    }

    public RequestCapture(Request req) {
        url = req.url();
        queryString = req.queryString();
        method = HttpMethod.valueOf(req.requestMethod());
        writeHeaders(req);
        writeQuery(req);
        param = req.params("p");
        contentType = req.contentType();
    }

    public void writeBody(Request req) {
        //parseBodyToFormParams(req);
        writeMultipart(req);
    }

    private void parseBodyToFormParams() {
        URLEncodedUtils.parse(this.body , Charset.forName("UTF-8"))
                .forEach(p -> {
                    params.put(p.getName(), p.getValue());
                });
    }

    public void writeMultipart(Request req) {
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(getProperty("java.io.tmpdir")));

        try {
            for (Part p : req.raw().getParts()) {
                if (!Strings.isNullOrEmpty(p.getSubmittedFileName())) {
                    buildFilePart(p);
                } else {
                    buildFormPart(p);
                }
            }
        } catch (ServletException e){
            this.body = req.body();
            parseBodyToFormParams();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void buildFormPart(Part p) throws IOException {
        java.util.Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\A");
        String value = s.hasNext() ? s.next() : "";
        params.put(p.getName(), value);
    }

    public void buildFilePart(Part part) throws IOException {
        File file = new File();
        file.fileName = part.getSubmittedFileName();
        file.type = part.getContentType();
        file.inputName = part.getName();
        file.fileType = part.getContentType();
        file.body = TestUtil.toString(part.getInputStream());

        files.add(file);
    }

    private void writeQuery(Request req) {
        req.queryParams().forEach(q -> params.putAll(q, Sets.newHashSet(req.queryMap(q).values())));
    }

    public RequestCapture asserBody(String s) {
        assertEquals(s, body);
        return this;
    }

    public RequestCapture assertNoHeader(String s) {
        assertFalse("Should Have No Header " + s, headers.containsKey(s));
        return this;
    }

    private RequestCapture writeHeaders(Request req) {
        req.headers().forEach(h -> headers.put(h, req.headers(h)));
        return this;
    }

    public RequestCapture assertHeader(String key, String value) {
        assertEquals("Expected Header Failed", value, headers.get(key));
        return this;
    }

    public RequestCapture assertParam(String key, String value) {
        assertThat("Expected Query or Form value", params.get(key), hasItem(value));
        return this;
    }

    public File getFile(String fileName) {
        return files.stream()
                .filter(f -> Objects.equals(f.fileName, fileName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No File With Name: " + fileName));
    }

    public File getFileByInput(String input) {
        return files.stream()
                .filter(f -> Objects.equals(f.inputName, input))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No File from form: " + input));
    }

    public RequestCapture assertFileContent(String input, String content) {
        assertEquals(content, getFileByInput(input).body);
        return this;
    }

    public void assertBasicAuth(String username, String password) {
        String raw = headers.get("Authorization");
        TestUtil.assertBasicAuth(raw, username, password);
    }

    public void assertQueryString(String s) {
        assertEquals(s, queryString);
    }

    public void asserMethod(HttpMethod get) {
        assertEquals(get, method);
    }

    public RequestCapture assertPathParam(String value) {
        assertEquals(value, param);
        return this;
    }

    public RequestCapture assertUrl(String s) {
         assertEquals(s, url);
         return this;
    }

    public void assertCharset(Charset charset) {
        assertThat(contentType, endsWith(charset.toString()));
    }

    public static class File {
        public String fileName;
        public String type;
        public String inputName;
        public String body;
        public String fileType;

        public File assertBody(String content){
            assertEquals(content, body);
            return this;
        }

        public File assertFileType(String type){
            assertEquals(type, this.fileType);
            return this;
        }

        public File assertFileType(ContentType imageJpeg) {
            return assertFileType(imageJpeg.toString());
        }

        public File assertFileName(String s) {
            assertEquals(s, fileName);
            return this;
        }
    }
}
