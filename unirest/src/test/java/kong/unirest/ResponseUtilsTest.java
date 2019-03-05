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

package kong.unirest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResponseUtilsTest {
    @Mock
    private Config config;
    @InjectMocks
    TestResponse test;

    @Test
    public void getCharsetDefaults() {
        defaultEncoding("UTF-8");

        assertEquals("UTF-8", getCharSet(null));
        assertEquals("UTF-8", getCharSet(""));
        assertEquals("UTF-8", getCharSet("         "));
        assertEquals("UTF-8", getCharSet("Content-Type: text/html;"));
        assertEquals("UTF-8", getCharSet("Content-Type: text/html; charset="));
    }

    @Test
    public void contentTypeWhenYouGotIt() {
        assertEquals("LATIN-1", getCharSet("Content-Type: text/html; charset=latin-1"));
    }

    @Test
    public void changeTheDefault() {
        defaultEncoding("KINGON-1");
        assertEquals("KINGON-1", getCharSet(null));
        defaultEncoding("SINDARIN-42");
        assertEquals("SINDARIN-42", getCharSet(null));
    }

    private void defaultEncoding(String t) {
        when(config.getDefaultResponseEncoding()).thenReturn(t);
    }

    private String getCharSet(String content) {
        test.type = content;
        return test.getCharSet();
    }

    public static class TestResponse extends RawResponseBase {

        public String type;

        protected TestResponse(Config config) {
            super(config);
        }

        @Override
        public int getStatus() {
            return 0;
        }

        @Override
        public String getStatusText() {
            return null;
        }

        @Override
        public Headers getHeaders() {
            return null;
        }

        @Override
        public InputStream getContent() {
            return null;
        }

        @Override
        public byte[] getContentAsBytes() {
            return new byte[0];
        }

        @Override
        public String getContentAsString() {
            return null;
        }

        @Override
        public String getContentAsString(String charset) {
            return null;
        }

        @Override
        public InputStreamReader getContentReader() {
            return null;
        }

        @Override
        public boolean hasContent() {
            return false;
        }

        @Override
        public String getContentType() {
            return type;
        }

        @Override
        public String getEncoding() {
            return null;
        }
    }
}