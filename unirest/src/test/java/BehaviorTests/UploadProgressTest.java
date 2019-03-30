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

import kong.unirest.ProgressMonitor;
import kong.unirest.Unirest;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static kong.unirest.TestUtil.rezFile;
import static org.junit.Assert.assertEquals;

public class UploadProgressTest extends BddTest {
    private static class Monitor implements ProgressMonitor {
        private Map<String, Stats> stats = new HashMap<>();

        @Override
        public void accept(String file, Long bytesWritten, Long totalBytes) {
            stats.computeIfAbsent(file, f -> new Stats());
            stats.compute(file, (f,s) -> {
               s.progress.add(bytesWritten);
               s.timesCalled++;
               s.total = totalBytes;
               return s;
            });
        }

        public Stats get(String spidey) {
            return stats.getOrDefault(spidey, new Stats());
        }

        static class Stats {
            List<Long> progress = new ArrayList<>();
            long total;
            long timesCalled;
        }
    }

    private Monitor monitor;
    private File spidey;

    @Override
    public void setUp() {
        super.setUp();
        this.monitor = new Monitor();
        spidey = rezFile("/spidey.jpg");
    }

    @Test
    public void canAddUploadProgress() {
        RequestCapture cap = Unirest.post(MockServer.POST)
                .field("spidey", this.spidey)
                .uploadMonitor(monitor)
                .asObject(RequestCapture.class)
                .getBody();

        assertSpideyFileUpload(cap);
    }

    @Test
    public void canAddUploadProgressAsync() throws Exception {
        RequestCapture cap = Unirest.post(MockServer.POST)
                .field("spidey", spidey)
                .uploadMonitor(monitor)
                .asObjectAsync(RequestCapture.class)
                .get()
                .getBody();

        assertSpideyFileUpload(cap);
    }

    @Test
    public void canKeepTrackOfMultipleFiles() {
        RequestCapture cap = Unirest.post(MockServer.POST)
                .field("spidey", this.spidey)
                .field("other", rezFile("/test"))
                .uploadMonitor(monitor)
                .asObject(RequestCapture.class)
                .getBody();

        assertSpideyFileUpload(cap);

    }

    private void assertSpideyFileUpload(RequestCapture capture) {
        Monitor.Stats stat = monitor.get(spidey.getName());
        assertEquals(12, stat.timesCalled);
        assertEquals(asList(4096L, 8192L, 12288L, 16384L, 20480L, 24576L, 28672L,
                32768L, 36864L, 40960L, 45056L, 46246L), stat.progress);
        assertEquals(this.spidey.length(), stat.total);
        capture.getFile("spidey.jpg").assertSize(this.spidey.length());
    }
}
