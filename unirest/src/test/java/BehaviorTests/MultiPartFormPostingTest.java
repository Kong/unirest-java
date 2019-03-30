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

import kong.unirest.*;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;

import static java.util.Arrays.asList;
import static kong.unirest.TestUtil.getFileBytes;
import static kong.unirest.TestUtil.rezFile;
import static org.junit.Assert.assertEquals;

public class MultiPartFormPostingTest extends BddTest {
    @Test
    public void testMultipart() throws Exception {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("funky","bunch")
                .field("file", rezFile("/test"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("name", "Mark")
                .assertMultiPartContentType()
                .getFile("test")
                .assertBody("This is a test file")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void fileSizeDoesntChange() throws Exception {
        File file = rezFile("/image.jpg");
        long size = file.length();

        RequestCapture capture = Unirest.post(MockServer.POST)
                .field("file", file)
                .field("file2", new FileInputStream(file), "file2.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType();

        capture.getFile("image.jpg").assertSize(size);
        capture.getFile("file2.jpg").assertSize(size);
    }

    @Test
    public void testMultipartContentType() {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", rezFile("/image.jpg"), "image/jpeg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("name", "Mark")
                .getFile("image.jpg")
                .assertFileType("image/jpeg");
    }

    @Test
    public void canSendRawInputStreamsWithoutAFileName()  throws Exception {
        FileInputStream stream = new FileInputStream(rezFile("/test"));

        Unirest.post(MockServer.POST)
                .field("file", stream)
                .asObject(RequestCapture.class)
                .getBody()
                .assertParam("file", "This is a test file");
    }

    @Test
    public void multipleFiles_sameField() {
        RequestCapture body = Unirest.post(MockServer.POST)
                .field("file", rezFile("/image.jpg"))
                .field("file", rezFile("/spidey.jpg"))
                .asObject(RequestCapture.class)
                .getBody();

        body.getFile("image.jpg").exists();
        body.getFile("spidey.jpg").exists();
        assertEquals(2, body.getAllFilesByInput("file").size());
    }

    @Test
    public void testMultipartInputStreamContentType() throws Exception {
        FileInputStream stream = new FileInputStream(rezFile("/image.jpg"));

        Unirest.post(MockServer.POST)
                .header("accept", ContentType.MULTIPART_FORM_DATA.toString())
                .field("name", "Mark")
                .field("file", stream, ContentType.APPLICATION_OCTET_STREAM, "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertHeader("Accept", ContentType.MULTIPART_FORM_DATA.toString())
                .assertParam("name", "Mark")
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartInputStreamContentTypeAsync() throws Exception {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", new FileInputStream(rezFile("/test")), ContentType.APPLICATION_OCTET_STREAM, "test")
                .asJsonAsync(new MockCallback<>(this, r -> parse(r)
                        .assertParam("name", "Mark")
                        .assertMultiPartContentType()
                        .getFile("test")
                        .assertFileType("application/octet-stream"))
                );

        assertAsync();
    }

    @Test
    public void testMultipartByteContentType() throws Exception {
        final byte[] bytes = getFileBytes("/image.jpg");

        Unirest.post(MockServer.POST)
                .field("boot","boot")
                .field("file", bytes, "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartByteContentTypeAsync() throws Exception {
        final byte[] bytes = getFileBytes("/test");

        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", bytes, "test")
                .asJsonAsync(new MockCallback<>(this, r ->
                        parse(r)
                                .assertParam("name", "Mark")
                                .assertMultiPartContentType()
                                .getFile("test")
                                .assertFileType("application/octet-stream"))
                );

        assertAsync();
    }



    @Test
    public void testMultipartAsync() throws Exception {
        Unirest.post(MockServer.POST)
                .field("name", "Mark")
                .field("file", rezFile("/test"))
                .asJsonAsync(new MockCallback<>(this, r ->
                        parse(r)
                                .assertParam("name", "Mark")
                                .assertMultiPartContentType()
                                .getFile("test")
                                .assertFileType("application/octet-stream")
                                .assertBody("This is a test file"))
                );

        assertAsync();
    }

    @Test
    public void utf8FileNames() {
        InputStream fileData = new ByteArrayInputStream(new byte[] {'t', 'e', 's', 't'});
        final String filename = "fileäöü.pöf";

        Unirest.post(MockServer.POST)
                .field("file", fileData, filename)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .getFile(filename)
                .assertFileName(filename);
    }



    @Test
    public void canSetModeToStrictForLegacySupport() {
        InputStream fileData = new ByteArrayInputStream(new byte[] {'t', 'e', 's', 't'});
        final String filename = "fileäöü.pöf";

        Unirest.post(MockServer.POST)
                .field("file", fileData, filename)
                .mode(MultipartMode.STRICT)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .getFile("file???.p?f")
                .assertFileName("file???.p?f");
    }

    @Test
    public void canPostInputStreamWithContentType() throws Exception {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST)
                .field("testfile", new FileInputStream(file), ContentType.IMAGE_JPEG, "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .getFileByInput("testfile")
                .assertFileName("image.jpg")
                .assertFileType("image/jpeg");
    }

    @Test
    public void canPostInputStream() throws Exception {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST)
                .field("testfile", new FileInputStream(file), "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .getFileByInput("testfile")
                .assertFileName("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void postFieldsAsMap() throws URISyntaxException {
        File file = TestUtil.getImageFile();

        Unirest.post(MockServer.POST)
                .fields(TestUtil.mapOf("big", "bird", "charlie", 42, "testfile", file, "gonzo", null))
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("big", "bird")
                .assertParam("charlie", "42")
                .assertParam("gonzo", "")
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void postFileWithoutContentType() {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST)
                .field("testfile", file)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .getFile("image.jpg")
                .assertFileType("application/octet-stream");
    }

    @Test
    public void postFileWithContentType() {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST)
                .field("testfile", file, ContentType.IMAGE_JPEG.getMimeType())
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .getFile("image.jpg")
                .assertFileType(ContentType.IMAGE_JPEG);
    }

    @Test
    public void multiPartInputStreamAsFile() throws FileNotFoundException {
        Unirest.post(MockServer.POST)
                .field("foo", "bar")
                .field("filecontents", new FileInputStream(rezFile("/image.jpg")), "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("foo", "bar")
                .getFileByInput("filecontents")
                .assertFileType(ContentType.APPLICATION_OCTET_STREAM)
                .assertFileName("image.jpg");
    }

    @Test
    public void testPostMulipleFIles() {
        RequestCapture cap = Unirest.post(MockServer.POST)
                .field("name", asList(rezFile("/test"), rezFile("/test2")))
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType();

        cap.getFile("test").assertBody("This is a test file");
        cap.getFile("test2").assertBody("this is another test");
    }

    @Test
    public void testPostMultipleFiles()throws Exception {
        Unirest.post(MockServer.POST)
                .field("param3", "wot")
                .field("file1", rezFile("/test"))
                .field("file2", rezFile("/test"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("param3", "wot")
                .assertFileContent("file1", "This is a test file")
                .assertFileContent("file2", "This is a test file");
    }

    @Test
    public void testPostBinaryUTF8() throws Exception {
        Unirest.post(MockServer.POST)
                .header("Accept", ContentType.MULTIPART_FORM_DATA.getMimeType())
                .field("param3", "こんにちは")
                .field("file", rezFile("/test"))
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("param3", "こんにちは")
                .assertFileContent("file", "This is a test file");
    }

    @Test
    public void testMultipeInputStreams() throws FileNotFoundException {
        Unirest.post(MockServer.POST)
                .field("name", asList(new FileInputStream(rezFile("/test")), new FileInputStream(rezFile("/test2"))))
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("name", "This is a test file")
                .assertParam("name", "this is another test");
    }

    @Test
    public void multiPartInputStream() throws FileNotFoundException {
        Unirest.post(MockServer.POST)
                .field("foo", "bar")
                .field("filecontents", new FileInputStream(rezFile("/test")), ContentType.WILDCARD)
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("foo", "bar")
                .assertParam("filecontents", "This is a test file");
    }

    @Test
    public void passFileAsByteArray() {
        Unirest.post(MockServer.POST)
                .field("foo", "bar")
                .field("filecontents", TestUtil.getFileBytes("/image.jpg"), ContentType.IMAGE_JPEG, "image.jpg")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("foo", "bar")
                .getFileByInput("filecontents")
                .assertFileType(ContentType.IMAGE_JPEG)
                .assertFileName("image.jpg");
    }

    @Test
    public void nullFileResultsInEmptyPost() {
        Unirest.post(MockServer.POST)
                .field("testfile", (Object)null, ContentType.IMAGE_JPEG.getMimeType())
                .asObject(RequestCapture.class)
                .getBody()
                .assertContentType("application/x-www-form-urlencoded; charset=UTF-8")
                .assertParam("testfile", "");
    }

    @Test
    public void canForceIntoMultiPart() {
        Unirest.post(MockServer.POST)
                .multiPartContent()
                .field("foo", "bar")
                .asObject(RequestCapture.class)
                .getBody()
                .assertMultiPartContentType()
                .assertParam("foo", "bar");
    }

    @Test
    public void rawInspection() {
        String body = Unirest.post(MockServer.ECHO_RAW)
                .field("marky","mark")
                .field("funky","bunch")
                .field("file", rezFile("/test"))
                .asString()
                .getBody();

        String expected = TestUtil.getResource("rawPost.txt").replaceAll("\r","").trim();

        String id = body.substring(2, body.indexOf("\n") -1);
        body = body.replaceAll(id, "IDENTIFIER").replaceAll("\r","").trim();

        assertEquals(expected, body);
    }

}
