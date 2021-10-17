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

import com.google.common.base.Throwables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import kong.unirest.Unirest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsumerTest extends BddTest {

    Path test = Paths.get("1.jpeg");
    private int status;
    private String error;
    private boolean asyncDone = false;

    @Override
    @AfterEach
    public void tearDown() {
        super.tearDown();
        asyncDone = false;
        status = 0;
        File file = test.toFile();
        if(file.exists()){
            file.delete();
        }
    }

    @Test
    void canSimplyConsumeAResponse() {
        Unirest.get(MockServer.GET)
                .thenConsume(r -> status = r.getStatus());

        assertEquals(200, status);
    }

    @Test
    void downloadAFileAsync() {
        Unirest.get(MockServer.BINARYFILE)
                .thenConsumeAsync(r -> {
                    try {
                        BufferedImage image = ImageIO.read(r.getContent());
                        ImageIO.write(image, "jpeg", new File("1.jpeg"));
                        status = r.getStatus();
                    } catch (IOException e) {
                        error = Throwables.getStackTraceAsString(e);
                    } finally {
                        asyncDone = true;
                    }
                });
        while (!asyncDone){
            System.out.print(".");
        }
        assertEquals(null, error);
        assertEquals(200, status);
        assertTrue(test.toFile().exists());
    }

    @Test
    void downloadAFile() {
        Unirest.get(MockServer.BINARYFILE)
                .thenConsume(r -> {
                    try {
                        BufferedImage image = ImageIO.read(r.getContent());
                        ImageIO.write(image, "jpeg", new File("1.jpeg"));
                        status = r.getStatus();
                    } catch (IOException e) {
                        error = Throwables.getStackTraceAsString(e);
                    }
                });

        assertEquals(null, error);
        assertEquals(200, status);
        assertTrue(test.toFile().exists());
    }

    @Test
    void canSimplyConsumeAResponseAsync() {
        Unirest.get(MockServer.GET)
                .thenConsumeAsync(r -> status = r.getStatus());

        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < 5000) {
            if (status != 0) {
                break;
            }
        }
        assertEquals(200, status);
    }
}
