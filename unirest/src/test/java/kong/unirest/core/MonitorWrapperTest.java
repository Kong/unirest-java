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

package kong.unirest.core;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MonitorWrapperTest {

    private RecordingMonitor recorder;
    private TestRawResponse rawResponse;
    private InputStream stream;

    @BeforeEach
    void setUp() {
        recorder = new RecordingMonitor();
        rawResponse = new TestRawResponse(null);
        rawResponse.getHeaders().add("Content-Length", "5");
        stream = new MonitoringInputStream(new ByteArrayInputStream("hello".getBytes()), recorder, "", rawResponse);

    }

    @Test
    void dontRecordTwice() {
        String result = readStreamToString(stream);

        assertEquals("hello", result);

        assertEquals(4, recorder.bytesWritten);
        assertEquals(5, recorder.totalBytes);
    }

    @Test
    void dontRecordTwice_directAccess() throws Exception {
        while(stream.read() > -1){}

        assertEquals(5, recorder.bytesWritten);
        assertEquals(5, recorder.totalBytes);
    }

    @Test
    void dontRecordTwice_directAccess_2() throws Exception {
        while(stream.read(new byte[5]) > -1){}

        assertEquals(4, recorder.bytesWritten);
        assertEquals(5, recorder.totalBytes);
    }

    @Test
    void dontRecordTwice_directAccess_3() throws Exception {
        while(stream.read(new byte[5], 0, 5) > -1){}

        assertEquals(4, recorder.bytesWritten);
        assertEquals(5, recorder.totalBytes);
    }


    public static String readStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.next());
        }
        return stringBuilder.toString();
    }

    public class RecordingMonitor implements ProgressMonitor {
        private Long bytesWritten;
        private Long totalBytes;

        @Override
        public void accept(String field, String fileName, Long bytesWritten, Long totalBytes) {
            this.bytesWritten = bytesWritten;
            this.totalBytes = totalBytes;
        }
    }
}