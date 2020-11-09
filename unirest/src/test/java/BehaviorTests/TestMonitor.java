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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static kong.unirest.TestUtil.defaultIfNull;
import static kong.unirest.TestUtil.rezFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestMonitor implements ProgressMonitor {
    public File spidey = rezFile("/spidey.jpg");

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

    public void assertSpideyFileUpload() {
        assertSpideyFileUpload(spidey.getName());
    }

    public void assertSpideyFileUpload(String name) {
        Stats stat = get(name);
        assertEquals(spidey.length(), stat.total, "Wrong Expected Length");
        assertTrue(stat.timesCalled > 1);
    }

    public void assertSpideyFileDownload(String name) {
        Stats stat = get(name);
        assertEquals(spidey.length(), stat.total, "Wrong Expected Length");
        assertTrue(stat.timesCalled > 1);
    }

    static class Stats {
        List<Long> progress = new ArrayList<>();
        long total;
        long timesCalled;
    }
}
