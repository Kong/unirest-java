package io.github.openunirest.http;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import spark.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static java.lang.System.getProperty;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class RequestCapture {
    public Map<String, String> headers = new LinkedHashMap<>();
    public Map<String, File> files = new LinkedHashMap<>();
    public Multimap<String, String> params = HashMultimap.create();
    public String body;

    public RequestCapture() {
    }

    public RequestCapture(Request req) {
        writeHeaders(req);
        writeQuery(req);
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

        files.put(file.fileName, file);
    }

    public void asserBody(String s) {
        assertEquals(s, body);
    }

    public void assertNoHeader(String s) {
        assertFalse("Should Have No Header " + s, headers.containsKey(s));
    }


    public static class File {
        public String fileName;
        public String type;
        public String inputName;
        public String body;
        public String fileType;

        public void assertBody(String content){
            assertEquals(content, body);
        }

        public void assertFileType(String type){
            assertEquals(type, this.fileType);
        }
    }

    private void writeQuery(Request req) {
        req.queryParams().forEach(q -> params.putAll(q, Sets.newHashSet(req.queryMap(q).values())));
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
        return Optional.ofNullable(files.get(fileName))
                .orElseThrow(() -> new RuntimeException("No Such File! " + fileName));
    }
}
