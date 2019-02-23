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

import org.junit.Test;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.TestUtil;
import kong.unirest.Unirest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AsFileTest extends BddTest {

    private Path test = Paths.get("results.json");
    private JacksonObjectMapper om = new JacksonObjectMapper();

    @Override
    public void tearDown() {
        try {
            Files.delete(test);
        } catch (Exception e) { }
    }

    @Test
    public void canSaveContentsIntoFile() {
        File result = Unirest.get(MockServer.GET)
                .queryString("talking","heads")
                .queryString("param3", "こんにちは")
                .asFile(test.toString())
                .getBody();

        om.readValue(result, RequestCapture.class)
                .assertParam("talking", "heads")
                .assertParam("param3", "こんにちは")
                .assertStatus(200);

        assertEquals(test.toFile().getPath(), result.getPath());
    }

    @Test
    public void canSaveContentsIntoFileAsync() throws Exception {
        File result = Unirest.get(MockServer.GET)
                .queryString("talking","heads")
                .queryString("param3", "こんにちは")
                .asFileAsync(test.toString())
                .get()
                .getBody();

        om.readValue(result, RequestCapture.class)
                .assertParam("talking", "heads")
                .assertParam("param3", "こんにちは")
                .assertStatus(200);

        assertEquals(test.toFile().getPath(), result.getPath());
    }

    @Test
    public void canSaveContentsIntoFileAsyncWithCallback() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("talking","heads")
                .queryString("param3", "こんにちは")
                .asFileAsync(test.toString(), r -> {
                        om.readValue(r.getBody(), RequestCapture.class)
                            .assertParam("talking", "heads")
                            .assertParam("param3", "こんにちは")
                            .assertStatus(200);
                    assertEquals(test.toFile().getPath(), r.getBody().getPath());
                    asyncSuccess();
                });

        assertAsync();
    }

    @Test
    public void canDownloadABinaryFile() throws Exception {
        File f1 = TestUtil.rezFile("/image.jpg");

        File f2 = Unirest.get(MockServer.BINARYFILE)
                .asFile(test.toString())
                .getBody();

        assertTrue(com.google.common.io.Files.equal(f1, f2));
    }
}
