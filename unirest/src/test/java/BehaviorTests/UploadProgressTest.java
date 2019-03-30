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

import com.google.common.base.Strings;
import kong.unirest.ProgressMonitor;
import kong.unirest.Unirest;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static kong.unirest.TestUtil.defaultIfNull;
import static kong.unirest.TestUtil.rezFile;
import static org.junit.Assert.assertEquals;

public class UploadProgressTest extends BddTest {
    private static class Monitor implements ProgressMonitor {
        private Map<String, Stats> stats = new HashMap<>();

        @Override
        public void accept(String field, String file, Long bytesWritten, Long totalBytes) {
            String key = firstNotEmpty(file, field);
            stats.compute(key, (f, s) -> {
                s = defaultIfNull(s, Stats::new);
                s.progress.add(bytesWritten);
                s.timesCalled++;
                s.total = totalBytes;
                return s;
            });
        }

        private String firstNotEmpty(String... s) {
            return Stream.of(s)
                    .filter(string -> !Strings.isNullOrEmpty(string))
                    .findFirst()
                    .orElse("");
        }

        public Stats get(String fineName) {
            return stats.getOrDefault(fineName, new Stats());
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
        Unirest.post(MockServer.POST)
                .field("spidey", this.spidey)
                .uploadMonitor(monitor)
                .asEmpty();

        assertSpideyFileUpload("spidey.jpg");
    }

    @Test
    public void canAddUploadProgressAsync() throws Exception {
        Unirest.post(MockServer.POST)
                .field("spidey", spidey)
                .uploadMonitor(monitor)
                .asEmpty();

        assertSpideyFileUpload("spidey.jpg");
    }

    @Test
    public void canKeepTrackOfMultipleFiles() {
        Unirest.post(MockServer.POST)
                .field("spidey", this.spidey)
                .field("other", rezFile("/test"))
                .uploadMonitor(monitor)
                .asEmpty();

        assertSpideyFileUpload("spidey.jpg");
        assertOtherFileUpload();
    }

    @Test
    public void canMonitorIfPassedAsInputStream() throws Exception {
        Unirest.post(MockServer.POST)
                .field("spidey", new FileInputStream(spidey))
                .uploadMonitor(monitor)
                .asEmpty();

        assertSpideyFileUpload("spidey");
    }

    private void assertOtherFileUpload() {
        Monitor.Stats stat = monitor.get("test");
        assertEquals(1, stat.timesCalled);
        assertEquals(asList(19L), stat.progress);
        assertEquals(19L, stat.total);
    }

    private void assertSpideyFileUpload(String name) {
        Monitor.Stats stat = monitor.get(name);
        assertEquals(12, stat.timesCalled);
        assertEquals(asList(4096L, 8192L, 12288L, 16384L, 20480L, 24576L, 28672L,
                32768L, 36864L, 40960L, 45056L, 46246L), stat.progress);
        assertEquals(this.spidey.length(), stat.total);
    }
}
