package BehaviorTests;

import io.github.openunirest.http.Unirest;
import io.github.openunirest.request.HttpRequest;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import util.MockCallback;
import util.TestUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FormPostingTest extends BddTest {
    @Test
    public void testFormFields() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param1", "value1")
                .field("param2", "bye")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("param1", "value1")
                .assertParam("param2", "bye");
    }

    @Test
    public void formPostAsync() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param1", "value1")
                .field("param2", "bye")
                .asJsonAsync(new MockCallback<>(this, r -> {
                    RequestCapture req = parse(r);
                    req.assertParam("param1", "value1");
                    req.assertParam("param2", "bye");
                }));

        assertAsync();
    }

    @Test
    public void testMultipart() throws Exception {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new File(getClass().getResource("/test").toURI()))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .getFile("test")
                .assertBody("This is a test file")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartContentType() throws Exception {
         Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new File(getClass().getResource("/image.jpg").toURI()), "image/jpeg")
                 .asObject(RequestCapture.class)
                 .getBody()
                 .assertParam("name", "Mark")
                 .getFile("image.jpg")
                    .assertFileType("image/jpeg");
    }

    @Test
    public void testMultipartInputStreamContentType() throws Exception {
        FileInputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));

        Unirest.post(MockServer.POST)
                .header("accept", ContentType.MULTIPART_FORM_DATA.toString())
                .field("name", "Mark")
                .field("file", stream, ContentType.APPLICATION_OCTET_STREAM, "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertHeader("Accept", ContentType.MULTIPART_FORM_DATA.toString())
                .assertParam("name", "Mark")
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartInputStreamContentTypeAsync() throws Exception {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new FileInputStream(new File(getClass().getResource("/test").toURI())), ContentType.APPLICATION_OCTET_STREAM, "test")
                .asJsonAsync(new MockCallback<>(this, r -> parse(r)
                        .assertParam("name", "Mark")
                        .getFile("test")
                        .assertFileType("application/octet-stream"))
                );

        assertAsync();
    }

    @Test
    public void testMultipartByteContentType() throws Exception {
        final InputStream stream = new FileInputStream(new File(getClass().getResource("/image.jpg").toURI()));
        final byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        stream.close();

        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", bytes, "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartByteContentTypeAsync() throws Exception {
        final InputStream stream = new FileInputStream(new File(getClass().getResource("/test").toURI()));
        final byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        stream.close();

        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", bytes, "test")
                .asJsonAsync(new MockCallback<>(this, r ->
                        parse(r)
                                .assertParam("name", "Mark")
                                .getFile("test")
                                .assertFileType("application/octet-stream"))
                );

        assertAsync();
    }

    @Test
    public void testMultipartAsync() throws Exception {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new File(getClass().getResource("/test").toURI()))
                .asJsonAsync(new MockCallback<>(this, r ->
                        parse(r)
                                .assertParam("name", "Mark")
                                .getFile("test")
                                .assertFileType("application/octet-stream")
                                .assertBody("This is a test file"))
                );

        assertAsync();
    }

    @Test
    public void testAsyncCustomContentTypeAndFormParams() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("name", "Mark")
                .field("hello", "world")
                .asJsonAsync(new MockCallback<>(this, r -> parse(r)
                        .assertParam("name", "Mark")
                        .assertParam("hello", "world")
                        .assertHeader("Content-Type", "application/x-www-form-urlencoded")
                ));

        assertAsync();
    }

    @Test
    public void testPostMultipleFiles()throws Exception {
        Unirest.post(MockServer.POST)
                .field("param3", "wot")
                .field("file1", new File(getClass().getResource("/test").toURI()))
                .field("file2", new File(getClass().getResource("/test").toURI()))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("param3", "wot")
                .assertFileContent("file1", "This is a test file")
                .assertFileContent("file2", "This is a test file");
    }

    @Test
    public void testPostArray() {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("name", "Tom")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testPostUTF8() {
        Unirest.post(MockServer.POST)
                .header("accept", "application/json")
                .field("param3", "こんにちは")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("param3", "こんにちは");
    }

    @Test
    public void testPostBinaryUTF8() throws Exception {
        Unirest.post(MockServer.POST)
                .header("Accept", ContentType.MULTIPART_FORM_DATA.getMimeType())
                .field("param3", "こんにちは")
                .field("file", new File(getClass().getResource("/test").toURI()))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("param3", "こんにちは")
                .assertFileContent("file", "This is a test file");
    }

    @Test
    public void testPostCollection() {
        Unirest.post(MockServer.POST)
                .field("name", Arrays.asList("Mark", "Tom"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .assertParam("name", "Tom");
    }

    @Test
    public void testPostProvidesSortedParams() throws Exception {
        // Verify that fields are encoded into the body in sorted order.
        HttpRequest httpRequest = Unirest.post("test")
                .field("z", "Z")
                .field("y", "Y")
                .field("x", "X")
                .getHttpRequest();

        InputStream content = httpRequest.getBody().getEntity().getContent();
        String body = IOUtils.toString(content, "UTF-8");
        assertEquals("x=X&y=Y&z=Z", body);
    }

    @Test
    public void postFileWithContentType() throws Exception {
        File file = getImageFile();
        Unirest.post(MockServer.POST)
                .field("testfile", file, ContentType.IMAGE_JPEG.getMimeType())
                .asObject(RequestCapture.class)
                .getBody()
                .getFile("image.jpg")
                .assertFileType(ContentType.IMAGE_JPEG);
    }

    @Test
    public void nullFileResultsInEmptyPost() {
        Unirest.post(MockServer.POST)
                .field("testfile", (Object)null, ContentType.IMAGE_JPEG.getMimeType())
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("testfile", "");
    }

    @Test
    public void postFileWithoutContentType() throws URISyntaxException {
        File file = getImageFile();
        Unirest.post(MockServer.POST)
                .field("testfile", file)
                .asObject(RequestCapture.class)
                .getBody()
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void postFieldsAsMap() throws URISyntaxException {
        File file = getImageFile();

        Unirest.post(MockServer.POST)
                .fields(TestUtil.mapOf("big", "bird", "charlie", 42, "testfile", file, "gonzo", null))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("big", "bird")
                .assertParam("charlie", "42")
                .assertParam("gonzo", "")
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void nullMapDoesntBomb() {
        Unirest.post(MockServer.POST)
                .queryString("foo","bar")
                .fields(null)
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar");
    }

    @Test
    public void canPostInputStream() throws Exception {
        File file = getImageFile();
        Unirest.post(MockServer.POST)
                .field("testfile", new FileInputStream(file), "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .getFileByInput("testfile")
                .assertFileName("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void canPostInputStreamWithContentType() throws Exception {
        File file = getImageFile();
        Unirest.post(MockServer.POST)
                .field("testfile", new FileInputStream(file), ContentType.IMAGE_JPEG, "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .getFileByInput("testfile")
                .assertFileName("image.jpg")
                .assertFileType("image/jpeg");
    }

    @Test
    public void testDelete() {
        Unirest.delete(MockServer.DELETE)
                .field("name", "mark")
                .field("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "mark")
                .assertParam("foo", "bar");
    }

    @Test
    public void testChangingEncodingToForms(){
        Unirest.post(MockServer.POST)
                .charset(StandardCharsets.US_ASCII)
                .field("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar")
                .assertCharset(StandardCharsets.US_ASCII);
    }

    @Test
    public void testChangingEncodingAfterMovingToForm(){
        Unirest.post(MockServer.POST)
                .field("foo", "bar")
                .charset(StandardCharsets.US_ASCII)
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("foo", "bar")
                .assertCharset(StandardCharsets.US_ASCII);
    }

    @Test
    public void canSetCharsetOfBody(){
        Unirest.post(MockServer.POST)
                .charset(StandardCharsets.US_ASCII)
                .body("foo")
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("foo")
                .assertCharset(StandardCharsets.US_ASCII);
    }

    @Test
    public void canSetCharsetOfBodyAfterMovingToBody(){
        Unirest.post(MockServer.POST)
                .body("foo")
                .charset(StandardCharsets.US_ASCII)
                .asObject(RequestCapture.class)
                .getBody()
                .asserBody("foo")
                .assertCharset(StandardCharsets.US_ASCII);
    }

    private File getImageFile() throws URISyntaxException {
        return new File(getClass().getResource("/image.jpg").toURI());
    }
}
