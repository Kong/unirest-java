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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

import static java.util.Arrays.asList;
import static BehaviorTests.TestUtils.rezFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UploadProgressTest extends BddTest {

    private TestMonitor monitor;

    @Override @BeforeEach
    public void setUp() {
        super.setUp();
        this.monitor = new TestMonitor();
    }

    @Test
    void canAddUploadProgress() {
        Unirest.post(MockServer.POST)
                .field("spidey", monitor.spidey)
                .uploadMonitor(monitor)
                .asEmpty();

        monitor.assertSpideyFileUpload();
    }

    @Test
    void canAddUploadProgressAsync() throws Exception {
        Unirest.post(MockServer.POST)
                .field("spidey", monitor.spidey)
                .uploadMonitor(monitor)
                .asEmpty();

        monitor.assertSpideyFileUpload();
    }

    @Test
    void canKeepTrackOfMultipleFiles() {
        Unirest.post(MockServer.POST)
                .field("spidey", monitor.spidey)
                .field("other", rezFile("/test.txt"))
                .uploadMonitor(monitor)
                .asEmpty();

        monitor.assertSpideyFileUpload();
        assertOtherFileUpload();
    }

    @Test
    void canMonitorIfPassedAsInputStream() throws Exception {
        Unirest.post(MockServer.POST)
                .field("spidey", new FileInputStream(monitor.spidey))
                .uploadMonitor(monitor)
                .asEmpty();

        monitor.assertSpideyFileUpload("spidey");
    }

    @Test
    void canUseWithInputStreamBody() throws Exception {
        Unirest.post(MockServer.POST)
                .body(new FileInputStream(monitor.spidey))
                .uploadMonitor(monitor)
                .asEmpty();

        monitor.assertSpideyFileUpload("body");
    }

    private void assertOtherFileUpload() {
        TestMonitor.Stats stat = monitor.get("test.txt");
        assertEquals(1, stat.timesCalled);
        assertEquals(asList(19L), stat.progress);
        assertEquals(19L, stat.total);
    }

}
