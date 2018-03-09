package io.github.openunirest.http;

import com.google.common.collect.Sets;
import org.apache.http.entity.ContentType;
import spark.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

import static java.lang.System.getProperty;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FormCapture {
    public Map<String, String> headers = new LinkedHashMap<>();
    public Map<String, File> files = new LinkedHashMap<>();
    public Map<String, Set<String>> query = new LinkedHashMap<>();

    public FormCapture() {
    }

    public FormCapture(Request req) {
        writeHeaders(req);
        writeQuery(req);
    }

    public void writeFiles(Request req) {
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(getProperty("java.io.tmpdir")));

        try {
            for (Part p : req.raw().getParts()) {
                if (p.getContentType().equals(ContentType.APPLICATION_OCTET_STREAM.getMimeType())) {
                    buildFilePart(p);
                } else {
                    buildFormPart(p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void buildFormPart(Part p) throws IOException {
        java.util.Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\A");
        String value = s.hasNext() ? s.next() : "";
        query.put(p.getName(), Sets.newHashSet(value));
    }

    public void buildFilePart(Part part) throws IOException {
        File file = new File();
        file.fileName = part.getSubmittedFileName();
        file.type = part.getContentType();
        file.inputName = part.getName();
        file.body = TestUtil.toString(part.getInputStream());

        files.put(file.fileName, file);
    }


    public static class File {
        public String fileName;
        public String type;
        public String inputName;
        public String body;

        public void assertBody(String content){
            assertEquals(content, body);
        }
    }

    private void writeQuery(Request req) {
        req.queryParams().forEach(q -> query.computeIfAbsent(q, (w) -> Sets.newHashSet(req.queryMap(q).values())));
    }

    private void writeHeaders(Request req) {
        req.headers().forEach(h -> headers.put(h, req.headers(h)));
    }

    public void assertHeader(String key, String value) {
        assertEquals("Expected Header Failed", value, headers.get(key));
    }

    public void assertQuery(String key, String value) {
        assertThat("Expected Query or Form value", query.getOrDefault(key, Collections.emptySet()), hasItem(value));
    }

    public File getFile(String fileName) {
        return files.get(fileName);
    }
}
