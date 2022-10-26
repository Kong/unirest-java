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

import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

class DownloadProgressTest extends BddTest {
    @TempDir
    File targeFolder;

    private TestMonitor monitor;


    @BeforeEach
    public void setUp() {
        super.setUp();
        this.monitor = new TestMonitor();
    }

    @Test
    void canAddUploadProgress() {
        Unirest.get(MockServer.BINARYFILE)
                .downloadMonitor(monitor)
                .asFile(targeFolder.getPath() + "/spidey.jpg");

        monitor.assertSpideyFileDownload("spidey.jpg");
    }

    @Test
    void canAddUploadProgressAsync() throws Exception {
        Unirest.get(MockServer.BINARYFILE)
                .downloadMonitor(monitor)
                .asFileAsync(targeFolder.getPath() + "/spidey.jpg")
                .get();

        monitor.assertSpideyFileDownload("spidey.jpg");
    }


    @Test
    void canAddUploadProgressWithBytes() {
        Unirest.get(MockServer.BINARYFILE)
                .downloadMonitor(monitor)
                .asBytes();

        monitor.assertSpideyFileDownload("body");
    }

    @Test
    void canAddUploadProgressWithBytesAsync() throws Exception {
        Unirest.get(MockServer.BINARYFILE)
                .downloadMonitor(monitor)
                .asBytesAsync()
                .get();

        monitor.assertSpideyFileDownload("body");
    }

}
